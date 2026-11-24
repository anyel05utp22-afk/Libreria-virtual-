package patrones.servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import patrones.comportamiento.command.CommandInvoker;
import patrones.comportamiento.memento.LibroMemento;
import patrones.comportamiento.observer.StockObservable;
import patrones.comportamiento.state.ContextoPublicacion;
import patrones.creacionales.prototype.LibroPrototype;
import patrones.estructurales.composite.CategoriaComposite;
import patrones.estructurales.facade.LibreriaFacade;
import patrones.estructurales.proxy.ProxyCoverService;
import patrones.grasp.controlador.LibreriaGRASPController;
import patrones.grasp.experto.PublicacionExperto;
import patrones.grasp.fabricacionpura.AuditoriaTransaccionesService;
import patrones.grasp.polimorfismo.IRecursoBiblioteca;
import patrones.solid.isp.EjemplarBibliotecaISP;
import patrones.solid.lsp.CuentoLSP;
import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.PublicacionLSP;
import patrones.solid.lsp.RevistaLSP;
import patrones.solid.ocp.IDescuentoOCP;
import patrones.solid.ocp.RegistroDescuentosOCP;
import patrones.solid.srp.LibroSRP;

/**
 * Capa de transporte HTTP de Librería Virtual El Saber (puerto 8084).
 * Delega el 100% de la lógica de negocio a LibreriaGRASPController (GRASP Controlador)
 * y a LibreriaFacade (GoF Facade), evitando el antipatrón God Object.
 */
public class ServidorLibreriaWeb {
    public static final int PUERTO = 8084;
    private static final LibreriaGRASPController controller = new LibreriaGRASPController();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.setExecutor(Executors.newFixedThreadPool(12));

        // Archivos estáticos del frontend
        server.createContext("/", new StaticFileHandler());

        // API REST completa (Semanas 1 a 17)
        server.createContext("/api/libros", new LibrosHandler());
        server.createContext("/api/publicaciones", new PublicacionesLspHandler());
        server.createContext("/api/cotizar", new CotizarComprarHandler(false));
        server.createContext("/api/comprar", new CotizarComprarHandler(true));
        server.createContext("/api/descarga-digital", new DescargaDigitalHandler());
        server.createContext("/api/prestamos/devolver", new DevolverPrestamoHandler());
        server.createContext("/api/prestamos", new PrestamosHandler());
        server.createContext("/api/mora", new MoraPolimorficaHandler());
        server.createContext("/api/reservar/liberar", new ReservarHandler(true));
        server.createContext("/api/reservar", new ReservarHandler(false));
        server.createContext("/api/suscribir", new SuscribirHandler());
        server.createContext("/api/clonar", new ClonarHandler());
        server.createContext("/api/prototipos", new PrototiposHandler());
        server.createContext("/api/builder", new BuilderHandler());
        server.createContext("/api/editorial", new EditorialHandler());
        server.createContext("/api/undo", new UndoRedoHandler(true));
        server.createContext("/api/redo", new UndoRedoHandler(false));
        server.createContext("/api/comandos", new ComandosEstadoHandler());
        server.createContext("/api/memento", new MementoHandler());
        server.createContext("/api/catalogo/arbol", new CompositeArbolHandler());
        server.createContext("/api/portada", new ProxyPortadaHandler());
        server.createContext("/api/inventario/metricas", new ExpertoMetricasHandler());
        server.createContext("/api/descuentos", new DescuentosOcpHandler());
        server.createContext("/api/antipatrones", new AntipatronesHandler());
        server.createContext("/api/arquitectura", new ArquitecturaHandler());
        server.createContext("/api/auditoria", new AuditoriaHandler());

        System.out.println("============================================================");
        System.out.println("  SERVIDOR WEB LIBRERÍA VIRTUAL EL SABER INICIADO");
        System.out.println("  URL:   http://localhost:" + PUERTO);
        System.out.println("  Motor: " + controller.getFacade().getMotorPersistenciaDIP());
        System.out.println("============================================================");

        if (!Boolean.getBoolean("libreria.headless")) {
            try {
                if (java.awt.Desktop.isDesktopSupported()
                        && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PUERTO));
                }
            } catch (Exception ignored) {}
        }

        server.start();
    }

    // =========================================================================
    // ARCHIVOS ESTÁTICOS
    // =========================================================================
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) path = "/index.html";
            if (path.contains("..")) {
                enviarRespuesta(ex, 403, "{\"error\":\"Ruta no permitida\"}");
                return;
            }
            File f = new File("web" + path);
            if (!f.exists() || f.isDirectory()) {
                f = new File("web/index.html");
                path = "/index.html";
            }
            String mime = "text/html; charset=UTF-8";
            if (path.endsWith(".css")) mime = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) mime = "application/javascript; charset=UTF-8";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mime = "image/jpeg";
            else if (path.endsWith(".png")) mime = "image/png";
            else if (path.endsWith(".svg")) mime = "image/svg+xml";

            ex.getResponseHeaders().set("Content-Type", mime);
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            ex.sendResponseHeaders(200, f.length());
            try (OutputStream os = ex.getResponseBody(); FileInputStream fis = new FileInputStream(f)) {
                byte[] buf = new byte[8192];
                int c;
                while ((c = fis.read(buf)) != -1) os.write(buf, 0, c);
            }
        }
    }

    // =========================================================================
    // 1. CRUD LIBROS (DIP + SRP + COMMAND + STATE + OBSERVER + GRASP)
    // =========================================================================
    static class LibrosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            String method = ex.getRequestMethod().toUpperCase();
            try {
                if (method.equals("GET")) {
                    List<LibroSRP> libros = controller.procesarListadoCatalogo();
                    enviarRespuesta(ex, 200, librosConEstadoToJson(libros));
                } else if (method.equals("POST")) {
                    Map<String, String> body = parseBody(ex);
                    LibroSRP candidato = parseLibro(body, 0);
                    List<String> errores = controller.validarAntesDeGuardar(candidato);
                    if (!errores.isEmpty()) {
                        enviarRespuesta(ex, 400, "{\"success\":false,\"message\":" + JsonUtil.str(String.join(" ", errores)) + "}");
                        return;
                    }
                    boolean ok = controller.procesarRegistro(candidato.getAutor(), candidato.getTitulo(),
                        candidato.getEditorial(), candidato.getMateria(), candidato.getPrecio(), candidato.getStock());
                    if (ok) {
                        enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro registrado correctamente (Validado con SRP y guardado en Command)\"}");
                    } else {
                        enviarRespuesta(ex, 500, "{\"success\":false,\"message\":\"No se pudo registrar el libro\"}");
                    }
                } else if (method.equals("PUT")) {
                    Map<String, String> body = parseBody(ex);
                    int id = parseInt(body.get("id"), 0);
                    LibroSRP candidato = parseLibro(body, id);
                    List<String> errores = controller.validarAntesDeGuardar(candidato);
                    if (id <= 0 || !errores.isEmpty()) {
                        String msg = id <= 0 ? "ID de libro inválido." : String.join(" ", errores);
                        enviarRespuesta(ex, 400, "{\"success\":false,\"message\":" + JsonUtil.str(msg) + "}");
                        return;
                    }
                    boolean ok = controller.procesarModificacion(id, candidato.getAutor(), candidato.getTitulo(),
                        candidato.getEditorial(), candidato.getMateria(), candidato.getPrecio(), candidato.getStock());
                    if (ok) {
                        enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro actualizado correctamente (Command + State + Observer sincronizados)\"}");
                    } else {
                        enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"No se encontró el libro #" + id + "\"}");
                    }
                } else if (method.equals("DELETE")) {
                    Map<String, String> query = parseQuery(ex.getRequestURI().getQuery());
                    int id = parseInt(query.get("id"), 0);
                    if (id <= 0) {
                        enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"Debe indicar un ID válido\"}");
                        return;
                    }
                    boolean ok = controller.procesarEliminacion(id);
                    if (ok) {
                        enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro eliminado (puedes revertir con Undo)\"}");
                    } else {
                        enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"No se pudo eliminar el libro #" + id + "\"}");
                    }
                } else {
                    enviarRespuesta(ex, 405, "{\"success\":false,\"message\":\"Método no permitido\"}");
                }
            } catch (Exception e) {
                enviarRespuesta(ex, 400, "{\"success\":false,\"message\":" + JsonUtil.str("Error procesando solicitud: " + e.getMessage()) + "}");
            }
        }
    }

    // =========================================================================
    // 2. PUBLICACIONES POLIMÓRFICAS (LSP + FACTORY METHOD)
    // =========================================================================
    static class PublicacionesLspHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            String method = ex.getRequestMethod().toUpperCase();
            if (method.equals("GET")) {
                List<PublicacionLSP> pubs = controller.procesarListadoPublicacionesLSP();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < pubs.size(); i++) {
                    PublicacionLSP p = pubs.get(i);
                    IRecursoBiblioteca regla = IRecursoBiblioteca.paraTipo(p.getTipo());
                    String autorOMarca = "";
                    String extra = "";
                    if (p instanceof LibroLSP) {
                        LibroLSP l = (LibroLSP) p;
                        autorOMarca = l.getAutor();
                        extra = l.getEditorial() + " • " + l.getMateria();
                    } else if (p instanceof CuentoLSP) {
                        CuentoLSP c = (CuentoLSP) p;
                        autorOMarca = c.getAutor();
                        extra = c.getEditorial() + " • +" + c.getEdadRecomendada() + " años (" + c.getNumeroPaginas() + " págs.)";
                    } else if (p instanceof RevistaLSP) {
                        RevistaLSP r = (RevistaLSP) p;
                        autorOMarca = r.getMarca();
                        extra = "Edición #" + r.getEdicion() + " • " + r.getFechaPublicacion();
                    }
                    if (i > 0) sb.append(",");
                    sb.append(String.format(Locale.US,
                        "{\"id\":%d,\"tipo\":%s,\"titulo\":%s,\"autorOMarca\":%s,\"extra\":%s,\"fichaTecnica\":%s,"
                        + "\"precio\":%.2f,\"precioConIgv\":%.2f,\"stock\":%d,\"hayStock\":%s,\"diasPrestamo\":%d,\"moraDia\":%.2f}",
                        p.getId(), JsonUtil.str(p.getTipo()), JsonUtil.str(p.getTitulo()),
                        JsonUtil.str(autorOMarca), JsonUtil.str(extra), JsonUtil.str(p.getFichaTecnica()),
                        p.getPrecio(), p.calcularPrecioConIGV(), p.getStock(), p.hayStock(),
                        regla.getDiasPrestamoMaximo(), regla.calcularMoraPorDia()));
                }
                sb.append("]");
                enviarRespuesta(ex, 200, sb.toString());
            } else if (method.equals("POST")) {
                Map<String, String> body = parseBody(ex);
                String tipo = body.getOrDefault("tipo", "libro");
                String titulo = body.getOrDefault("titulo", "").trim();
                String autorOMarca = body.getOrDefault("autor", body.getOrDefault("marca", "")).trim();
                String editorialOFecha = body.getOrDefault("editorial", body.getOrDefault("fecha", "Editorial General")).trim();
                String materiaOExtra = body.getOrDefault("materia", body.getOrDefault("paginas", "Clásicos")).trim();
                int numeroExtra = parseInt(body.getOrDefault("numeroExtra", body.getOrDefault("edicion", body.getOrDefault("edad", "1"))), 1);
                double precio = parseDouble(body.get("precio"), 25.0);
                int stock = parseInt(body.get("stock"), 5);

                if (titulo.length() < 2 || autorOMarca.length() < 2) {
                    enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"Título y Autor/Marca son obligatorios.\"}");
                    return;
                }
                PublicacionLSP creada = controller.procesarRegistroPublicacionFactory(
                    tipo, titulo, autorOMarca, editorialOFecha, materiaOExtra, numeroExtra, precio, stock);
                if (creada != null) {
                    enviarRespuesta(ex, 200, "{\"success\":true,\"tipo\":" + JsonUtil.str(creada.getTipo())
                        + ",\"fichaTecnica\":" + JsonUtil.str(creada.getFichaTecnica())
                        + ",\"message\":" + JsonUtil.str(creada.getTipo() + " registrado con Factory Method y LSP") + "}");
                } else {
                    enviarRespuesta(ex, 500, "{\"success\":false,\"message\":\"Error registrando publicación\"}");
                }
            }
        }
    }

    // =========================================================================
    // 3. COTIZACIÓN Y COMPRA (BRIDGE + OCP + DECORATOR + GRASP CREATOR + ISP + SRP)
    // =========================================================================
    static class CotizarComprarHandler implements HttpHandler {
        private final boolean ejecutarCompra;
        CotizarComprarHandler(boolean ejecutarCompra) { this.ejecutarCompra = ejecutarCompra; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
                enviarRespuesta(ex, 405, "{\"success\":false,\"message\":\"Use POST\"}");
                return;
            }
            Map<String, String> body = parseBody(ex);
            List<int[]> items = new ArrayList<>();
            if (body.containsKey("items")) {
                for (Map<String, String> item : JsonUtil.parseObjectArray(body.get("items"))) {
                    int id = parseInt(item.get("id"), 0);
                    int qty = parseInt(item.get("cantidad"), 1);
                    if (id > 0) items.add(new int[]{id, Math.max(1, qty)});
                }
            }
            if (items.isEmpty()) {
                int id = parseInt(body.get("id"), 0);
                int qty = parseInt(body.get("cantidad"), 1);
                if (id > 0) items.add(new int[]{id, Math.max(1, qty)});
            }

            String cliente = body.getOrDefault("cliente", "Cliente Web");
            String formato = body.getOrDefault("formato", "fisico");
            String descuento = body.getOrDefault("descuento", "ninguno");
            boolean regalo = Boolean.parseBoolean(body.getOrDefault("regalo", "false"));
            boolean garantia = Boolean.parseBoolean(body.getOrDefault("garantia", "false"));
            boolean envioExpress = Boolean.parseBoolean(body.getOrDefault("envioExpress", "false"));

            LibreriaFacade.ResultadoCompra r = controller.procesarCotizacionOCompra(
                items, cliente, formato, descuento, regalo, garantia, envioExpress, ejecutarCompra);

            if (!r.exito) {
                enviarRespuesta(ex, 400, "{\"success\":false,\"message\":" + JsonUtil.str(r.mensaje) + "}");
                return;
            }
            String json = String.format(Locale.US,
                "{\"success\":true,\"message\":%s,\"numeroBoleta\":%s,\"fechaEmision\":%s,\"cliente\":%s,\"lineas\":%s,"
                + "\"subtotalBruto\":%.2f,\"descuento\":%.2f,"
                + "\"descripcionDescuento\":%s,\"costoExtras\":%.2f,\"descripcionExtras\":%s,"
                + "\"subtotalNeto\":%.2f,\"igv\":%.2f,\"total\":%.2f,\"formato\":%s,"
                + "\"detalleEntrega\":%s,\"comprobante\":%s,\"urlDescargaDigital\":%s,\"tamanoDigital\":%s}",
                JsonUtil.str(r.mensaje), JsonUtil.str(r.numeroBoleta), JsonUtil.str(r.fechaEmision),
                JsonUtil.str(r.clienteNombre), JsonUtil.stringArray(r.lineasDetalle),
                r.subtotalBruto, r.descuentoAplicado,
                JsonUtil.str(r.descripcionDescuento), r.costoExtras, JsonUtil.str(r.descripcionExtras),
                r.subtotalNeto, r.igv, r.totalFinal, JsonUtil.str(r.formatoNombre),
                JsonUtil.str(r.detalleEntrega), JsonUtil.str(r.comprobante == null ? "" : r.comprobante),
                JsonUtil.str(r.urlDescargaDigital), JsonUtil.str(r.tamanoDigital));
            enviarRespuesta(ex, 200, json);
        }
    }

    // Descarga simulada de E-Book (ISP IDigital)
    static class DescargaDigitalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
            String archivo = q.getOrDefault("archivo", "libro_el_saber.epub");
            String contenido = "========================================\n"
                + "  LIBRERÍA VIRTUAL EL SABER — E-BOOK\n"
                + "========================================\n"
                + "Archivo digital: " + archivo + "\n"
                + "Licencia emitida mediante SOLID ISP (IDigital) y GoF Bridge (FormatoDigitalEbook).\n"
                + "¡Disfruta tu lectura digital!\n";
            byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            ex.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + archivo.replace("\"", "") + ".txt\"");
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        }
    }

    // =========================================================================
    // 4. PRÉSTAMOS, DEVOLUCIONES Y MORA (ISP IPrestable + GRASP Polimorfismo)
    // =========================================================================
    static class PrestamosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            String method = ex.getRequestMethod().toUpperCase();
            if (method.equals("GET")) {
                List<EjemplarBibliotecaISP> lista = controller.procesarListadoPrestamos();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    EjemplarBibliotecaISP p = lista.get(i);
                    if (i > 0) sb.append(",");
                    sb.append(String.format(Locale.US,
                        "{\"idPrestamo\":%d,\"idPublicacion\":%d,\"titulo\":%s,\"tipoRecurso\":%s,"
                        + "\"dni\":%s,\"cliente\":%s,\"fechaPrestamo\":%s,\"fechaLimite\":%s,"
                        + "\"diasPlazo\":%d,\"moraPorDia\":%.2f,\"activo\":%s}",
                        p.getIdPrestamo(), p.getIdPublicacion(), JsonUtil.str(p.getTitulo()),
                        JsonUtil.str(p.getTipoRecurso()), JsonUtil.str(p.getDniCliente()),
                        JsonUtil.str(p.getNombreCliente()), JsonUtil.str(p.getFechaPrestamoStr()),
                        JsonUtil.str(p.getFechaLimiteStr()), p.getDiasPlazo(), p.getMoraPorDia(), p.estaPrestado()));
                }
                sb.append("]");
                enviarRespuesta(ex, 200, sb.toString());
            } else if (method.equals("POST")) {
                Map<String, String> body = parseBody(ex);
                int idLibro = parseInt(body.get("idLibro"), 0);
                String tipoRecurso = body.getOrDefault("tipoRecurso", "libro");
                String dni = body.getOrDefault("dni", "").trim();
                String cliente = body.getOrDefault("cliente", "Lector Biblioteca").trim();

                if (idLibro <= 0 || dni.length() < 6) {
                    enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"Ingresa un DNI válido (mínimo 6 caracteres) y un libro válido.\"}");
                    return;
                }
                EjemplarBibliotecaISP prestamo = controller.procesarPrestamo(idLibro, tipoRecurso, dni, cliente);
                if (prestamo != null) {
                    enviarRespuesta(ex, 200, String.format(Locale.US,
                        "{\"success\":true,\"idPrestamo\":%d,\"fechaLimite\":%s,\"diasPlazo\":%d,\"moraPorDia\":%.2f,\"message\":%s}",
                        prestamo.getIdPrestamo(), JsonUtil.str(prestamo.getFechaLimiteStr()),
                        prestamo.getDiasPlazo(), prestamo.getMoraPorDia(),
                        JsonUtil.str("Préstamo #" + prestamo.getIdPrestamo() + " registrado. Devolver hasta " + prestamo.getFechaLimiteStr())));
                } else {
                    enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"No se pudo realizar el préstamo (ejemplar agotado o reservado según State).\"}");
                }
            }
        }
    }

    static class DevolverPrestamoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> body = parseBody(ex);
            int idPrestamo = parseInt(body.get("idPrestamo"), 0);
            int diasRetraso = parseInt(body.get("diasRetraso"), 0);
            EjemplarBibliotecaISP devuelto = controller.procesarDevolucion(idPrestamo, diasRetraso);
            if (devuelto != null) {
                double mora = devuelto.calcularMoraEstimada(diasRetraso);
                enviarRespuesta(ex, 200, String.format(Locale.US,
                    "{\"success\":true,\"mora\":%.2f,\"message\":%s}",
                    mora, JsonUtil.str("Ejemplar '" + devuelto.getTitulo() + "' devuelto. Stock repuesto y observadores notificados. Mora: S/ "
                        + String.format(Locale.US, "%.2f", mora))));
            } else {
                enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"Préstamo no encontrado o ya devuelto.\"}");
            }
        }
    }

    static class MoraPolimorficaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
            String tipo = q.getOrDefault("tipo", "libro");
            int dias = parseInt(q.get("dias"), 0);
            IRecursoBiblioteca rec = IRecursoBiblioteca.paraTipo(tipo);
            double mora = controller.procesarCalculoMoraPolimorfica(tipo, dias);
            enviarRespuesta(ex, 200, String.format(Locale.US,
                "{\"tipo\":%s,\"diasPlazoMaximo\":%d,\"moraPorDia\":%.2f,\"diasRetraso\":%d,\"moraTotal\":%.2f}",
                JsonUtil.str(rec.getCategoriaTipo()), rec.getDiasPrestamoMaximo(), rec.calcularMoraPorDia(), dias, mora));
        }
    }

    // =========================================================================
    // 5. STATE (RESERVAS) & OBSERVER (ALERTAS DE STOCK)
    // =========================================================================
    static class ReservarHandler implements HttpHandler {
        private final boolean liberar;
        ReservarHandler(boolean liberar) { this.liberar = liberar; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> body = parseBody(ex);
            int id = parseInt(body.get("id"), 0);
            String cliente = body.getOrDefault("cliente", "Lector Web");
            ContextoPublicacion ctx = liberar
                ? controller.procesarLiberarReserva(id)
                : controller.procesarReserva(id, cliente);
            if (ctx != null) {
                enviarRespuesta(ex, 200, "{\"success\":true,\"estado\":" + JsonUtil.str(ctx.getEstado().getNombreEstado())
                    + ",\"reservadoPor\":" + JsonUtil.str(ctx.getReservadoPor())
                    + ",\"message\":" + JsonUtil.str((liberar ? "Reserva liberada → " : "Ejemplar reservado → ") + ctx.getEstado().getNombreEstado()) + "}");
            } else {
                enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"Libro no encontrado.\"}");
            }
        }
    }

    static class SuscribirHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            StockObservable obs = controller.getFacade().getStockObservable();
            if (ex.getRequestMethod().equalsIgnoreCase("GET")) {
                String json = "{\"totalSuscriptores\":" + obs.getTotalSuscriptores()
                    + ",\"suscripciones\":" + JsonUtil.stringArray(obs.getResumenSuscripciones())
                    + ",\"notificaciones\":" + JsonUtil.stringArray(obs.getNotificacionesEnviadas()) + "}";
                enviarRespuesta(ex, 200, json);
                return;
            }
            Map<String, String> body = parseBody(ex);
            String email = body.getOrDefault("email", "").trim();
            String titulo = body.getOrDefault("titulo", "").trim();
            if (email.isEmpty() || !email.contains("@")) {
                enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"Ingresa un correo electrónico válido.\"}");
                return;
            }
            controller.procesarSuscripcionStock(email, titulo);
            enviarRespuesta(ex, 200, "{\"success\":true,\"totalSuscriptores\":" + obs.getTotalSuscriptores()
                + ",\"message\":\"¡Suscripción confirmada (Observer)! Te avisaremos automáticamente cuando se reponga stock.\"}");
        }
    }

    // =========================================================================
    // 6. PROTOTYPE, BUILDER+DIRECTOR, ABSTRACT FACTORY
    // =========================================================================
    static class ClonarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> body = parseBody(ex);
            String plantilla = body.getOrDefault("plantilla", "").trim();
            LibroPrototype clon;
            if (!plantilla.isEmpty()) {
                clon = controller.procesarClonDesdePlantilla(plantilla);
                if (clon == null) {
                    enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"Plantilla Prototype no encontrada\"}");
                    return;
                }
            } else {
                clon = controller.procesarClonacion(
                    body.getOrDefault("autor", ""),
                    body.getOrDefault("titulo", ""),
                    body.getOrDefault("editorial", ""),
                    body.getOrDefault("materia", "Clásicos"),
                    parseDouble(body.get("precio"), 35.0),
                    parseInt(body.get("stock"), 5),
                    body.getOrDefault("edicion", "2da Edición")
                );
            }
            String json = String.format(Locale.US,
                "{\"success\":true,\"titulo\":%s,\"autor\":%s,\"editorial\":%s,\"materia\":%s,\"precio\":%.2f,\"stock\":%d,\"info\":%s}",
                JsonUtil.str(clon.getTitulo()), JsonUtil.str(clon.getAutor()),
                JsonUtil.str(clon.getEditorial()), JsonUtil.str(clon.getMateria()),
                clon.getPrecio(), clon.getStock(), JsonUtil.str(clon.getInfo()));
            enviarRespuesta(ex, 200, json);
        }
    }

    static class PrototiposHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, LibroPrototype> plantillas = controller.listarPlantillasPrototype();
            StringBuilder sb = new StringBuilder("[");
            int i = 0;
            for (Map.Entry<String, LibroPrototype> e : plantillas.entrySet()) {
                LibroPrototype p = e.getValue();
                if (i++ > 0) sb.append(",");
                sb.append(String.format(Locale.US,
                    "{\"clave\":%s,\"titulo\":%s,\"autor\":%s,\"editorial\":%s,\"materia\":%s,\"precio\":%.2f,\"stock\":%d}",
                    JsonUtil.str(e.getKey()), JsonUtil.str(p.getTitulo()), JsonUtil.str(p.getAutor()),
                    JsonUtil.str(p.getEditorial()), JsonUtil.str(p.getMateria()), p.getPrecio(), p.getStock()));
            }
            sb.append("]");
            enviarRespuesta(ex, 200, sb.toString());
        }
    }

    static class BuilderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> body = parseBody(ex);
            LibroSRP creado = controller.procesarBuilder(
                body.getOrDefault("preset", "personalizado"),
                body.getOrDefault("titulo", "Libro Edición Especial"),
                body.getOrDefault("autor", "Autor Destacado"),
                body.getOrDefault("editorial", "Edición El Saber"),
                body.getOrDefault("materia", "Filosofía, Educación, Ciencia y Tecnología"),
                parseDouble(body.get("precio"), 48.0),
                parseInt(body.get("stock"), 10),
                body.getOrDefault("isbn", ""),
                Boolean.parseBoolean(body.getOrDefault("tapaDura", "false")),
                Boolean.parseBoolean(body.getOrDefault("prologo", "false")),
                Boolean.parseBoolean(body.getOrDefault("ilustrado", "false"))
            );
            if (creado != null) {
                enviarRespuesta(ex, 200, String.format(Locale.US,
                    "{\"success\":true,\"id\":%d,\"titulo\":%s,\"editorial\":%s,\"precio\":%.2f,\"message\":%s}",
                    creado.getId(), JsonUtil.str(creado.getTitulo()), JsonUtil.str(creado.getEditorial()),
                    creado.getPrecio(), JsonUtil.str("Libro construido con Builder + DirectorPublicacion: " + creado.getTitulo())));
            } else {
                enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"Verifique los datos ingresados en el Asistente Builder.\"}");
            }
        }
    }

    static class EditorialHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> body = parseBody(ex);
            String editorial = body.getOrDefault("editorial", "planeta");
            String[] res = controller.procesarImportacionEditorial(editorial);
            String msg = "Pack importado de " + res[0] + ":\n• " + res[1] + "\n• " + res[2];
            enviarRespuesta(ex, 200, "{\"success\":true,\"editorial\":" + JsonUtil.str(res[0])
                + ",\"libro\":" + JsonUtil.str(res[1]) + ",\"revista\":" + JsonUtil.str(res[2])
                + ",\"message\":" + JsonUtil.str(msg) + "}");
        }
    }

    // =========================================================================
    // 7. COMMAND (UNDO / REDO / STACK) & MEMENTO (HISTORIAL SNAPSHOTS)
    // =========================================================================
    static class UndoRedoHandler implements HttpHandler {
        private final boolean esUndo;
        UndoRedoHandler(boolean esUndo) { this.esUndo = esUndo; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            boolean ok = esUndo ? controller.procesarUndo() : controller.procesarRedo();
            CommandInvoker inv = controller.getCommandInvoker();
            if (ok) {
                String msg = esUndo
                    ? "Operación deshecha con éxito (Command Undo)"
                    : "Operación rehecha con éxito (Command Redo)";
                enviarRespuesta(ex, 200, "{\"success\":true,\"undoCount\":" + inv.getTamanoUndo()
                    + ",\"redoCount\":" + inv.getTamanoRedo() + ",\"message\":" + JsonUtil.str(msg) + "}");
            } else {
                String msg = esUndo
                    ? "No hay operaciones previas para deshacer"
                    : "No hay operaciones deshechas para rehacer";
                enviarRespuesta(ex, 400, "{\"success\":false,\"undoCount\":" + inv.getTamanoUndo()
                    + ",\"redoCount\":" + inv.getTamanoRedo() + ",\"message\":" + JsonUtil.str(msg) + "}");
            }
        }
    }

    static class ComandosEstadoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            CommandInvoker inv = controller.getCommandInvoker();
            String json = "{\"undoCount\":" + inv.getTamanoUndo()
                + ",\"redoCount\":" + inv.getTamanoRedo()
                + ",\"proximoUndo\":" + JsonUtil.str(inv.getProximaDescripcionUndo())
                + ",\"proximoRedo\":" + JsonUtil.str(inv.getProximaDescripcionRedo())
                + ",\"historial\":" + JsonUtil.stringArray(inv.getHistorialUndo()) + "}";
            enviarRespuesta(ex, 200, json);
        }
    }

    static class MementoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            String method = ex.getRequestMethod().toUpperCase();
            if (method.equals("POST")) {
                Map<String, String> body = parseBody(ex);
                boolean ok = controller.procesarGuardarMemento(
                    body.getOrDefault("autor", ""),
                    body.getOrDefault("titulo", ""),
                    body.getOrDefault("editorial", ""),
                    body.getOrDefault("materia", ""),
                    parseDouble(body.get("precio"), 0.0),
                    parseInt(body.get("stock"), 0)
                );
                int total = controller.getCaretaker().getCantidadSnapshots();
                enviarRespuesta(ex, 200, "{\"success\":true,\"guardado\":" + ok + ",\"totalSnapshots\":" + total
                    + ",\"message\":\"Snapshot almacenado en Caretaker (Memento)\"}");
            } else if (method.equals("GET")) {
                Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
                if ("1".equals(q.get("list"))) {
                    List<LibroMemento> snaps = controller.getCaretaker().listarSnapshots();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < snaps.size(); i++) {
                        LibroMemento m = snaps.get(i);
                        if (i > 0) sb.append(",");
                        sb.append(String.format(Locale.US,
                            "{\"indice\":%d,\"titulo\":%s,\"autor\":%s,\"editorial\":%s,\"materia\":%s,\"precio\":%.2f,\"stock\":%d,\"timestamp\":%d}",
                            i, JsonUtil.str(m.getTitulo()), JsonUtil.str(m.getAutor()), JsonUtil.str(m.getEditorial()),
                            JsonUtil.str(m.getMateria()), m.getPrecio(), m.getStock(), m.getTimestamp()));
                    }
                    sb.append("]");
                    enviarRespuesta(ex, 200, sb.toString());
                    return;
                }
                int idx = parseInt(q.get("indice"), -1);
                LibroMemento m = controller.procesarRestaurarMemento(idx);
                if (m != null) {
                    String json = String.format(Locale.US,
                        "{\"success\":true,\"titulo\":%s,\"autor\":%s,\"editorial\":%s,\"materia\":%s,\"precio\":%.2f,\"stock\":%d,\"restantes\":%d}",
                        JsonUtil.str(m.getTitulo()), JsonUtil.str(m.getAutor()), JsonUtil.str(m.getEditorial()),
                        JsonUtil.str(m.getMateria()), m.getPrecio(), m.getStock(), controller.getCaretaker().getCantidadSnapshots());
                    enviarRespuesta(ex, 200, json);
                } else {
                    enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"No hay snapshots guardados en el historial Memento\"}");
                }
            }
        }
    }

    // =========================================================================
    // 8. COMPOSITE, PROXY, EXPERTO, OCP, ANTIPATRONES, ARQUITECTURA, AUDITORÍA
    // =========================================================================
    static class CompositeArbolHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            CategoriaComposite raiz = controller.procesarArbolComposite();
            enviarRespuesta(ex, 200, raiz.toJson());
        }
    }

    static class ProxyPortadaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
            String titulo = q.getOrDefault("titulo", "Libro");
            ProxyCoverService proxy = controller.getFacade().getProxyPortadas();
            boolean hitAntes = proxy.estaEnCache(titulo);
            String portadaJson = controller.procesarPortadaProxy(titulo);
            String json = String.format(Locale.US,
                "{\"titulo\":%s,\"desdeCache\":%s,\"portada\":%s,\"cacheHits\":%d,\"cacheMisses\":%d,\"tamanoCache\":%d,\"hitRate\":%.1f}",
                JsonUtil.str(titulo), hitAntes, portadaJson,
                proxy.getCacheHits(), proxy.getCacheMisses(), proxy.getTamanoCache(), proxy.getHitRatePorcentaje());
            enviarRespuesta(ex, 200, json);
        }
    }

    static class ExpertoMetricasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            List<PublicacionExperto> analisis = controller.procesarMetricasExperto(3);
            double valorTotal = 0.0;
            int totalUnidades = 0;
            int agotados = 0;
            int stockBajo = 0;
            StringBuilder alertas = new StringBuilder("[");
            int countAlertas = 0;
            for (PublicacionExperto pe : analisis) {
                valorTotal += pe.calcularValorTotalInventario();
                totalUnidades += pe.getStockActual();
                if (pe.getStockActual() == 0) agotados++;
                if (pe.requiereReabastecimiento()) {
                    if (pe.getStockActual() > 0) stockBajo++;
                    if (countAlertas++ > 0) alertas.append(",");
                    alertas.append(String.format(Locale.US,
                        "{\"titulo\":%s,\"stock\":%d,\"precioBase\":%.2f,\"precioConIgv\":%.2f,\"valorInventario\":%.2f,\"nivel\":%s}",
                        JsonUtil.str(pe.getTitulo()), pe.getStockActual(), pe.getPrecioBase(),
                        pe.calcularPrecioConIGV(), pe.calcularValorTotalInventario(), JsonUtil.str(pe.getNivelAlerta())));
                }
            }
            alertas.append("]");
            String json = String.format(Locale.US,
                "{\"totalTitulos\":%d,\"totalUnidades\":%d,\"valorTotalInventario\":%.2f,\"agotados\":%d,\"stockBajo\":%d,\"alertas\":%s}",
                analisis.size(), totalUnidades, valorTotal, agotados, stockBajo, alertas.toString());
            enviarRespuesta(ex, 200, json);
        }
    }

    static class DescuentosOcpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            StringBuilder sb = new StringBuilder("[");
            int i = 0;
            for (Map.Entry<String, IDescuentoOCP> e : RegistroDescuentosOCP.listarTodas().entrySet()) {
                if (i++ > 0) sb.append(",");
                sb.append("{\"clave\":").append(JsonUtil.str(e.getKey()))
                  .append(",\"descripcion\":").append(JsonUtil.str(e.getValue().getDescripcion())).append("}");
            }
            sb.append("]");
            enviarRespuesta(ex, 200, sb.toString());
        }
    }

    static class AntipatronesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            enviarRespuesta(ex, 200, controller.procesarReporteAntipatrones());
        }
    }

    static class ArquitecturaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            LibreriaFacade f = controller.getFacade();
            ProxyCoverService proxy = f.getProxyPortadas();
            CommandInvoker inv = controller.getCommandInvoker();
            String json = String.format(Locale.US,
                "{\"singletonEstado\":%s,\"singletonHash\":%d,\"singletonConsultas\":%d,\"motorDIP\":%s,"
                + "\"proxyHits\":%d,\"proxyMisses\":%d,\"proxyHitRate\":%.1f,\"proxyTamano\":%d,"
                + "\"observerSuscriptores\":%d,\"observerNotificaciones\":%d,"
                + "\"commandUndo\":%d,\"commandRedo\":%d,\"mementoSnapshots\":%d,"
                + "\"prestamosActivos\":%d,\"auditoriaEventos\":%d}",
                JsonUtil.str(f.getConexionSingleton().getResumenEstado()),
                f.getConexionSingleton().getHashInstancia(),
                f.getConexionSingleton().getConsultasEjecutadas(),
                JsonUtil.str(f.getMotorPersistenciaDIP()),
                proxy.getCacheHits(), proxy.getCacheMisses(), proxy.getHitRatePorcentaje(), proxy.getTamanoCache(),
                f.getStockObservable().getTotalSuscriptores(), f.getStockObservable().getNotificacionesEnviadas().size(),
                inv.getTamanoUndo(), inv.getTamanoRedo(), controller.getCaretaker().getCantidadSnapshots(),
                (int) f.listarPrestamos().stream().filter(EjemplarBibliotecaISP::estaPrestado).count(),
                AuditoriaTransaccionesService.getTotalEventos());
            enviarRespuesta(ex, 200, json);
        }
    }

    static class AuditoriaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (prepararCors(ex)) return;
            enviarRespuesta(ex, 200, JsonUtil.stringArray(AuditoriaTransaccionesService.obtenerLogs()));
        }
    }

    // =========================================================================
    // HELPERS HTTP & JSON
    // =========================================================================
    private static boolean prepararCors(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private static void enviarRespuesta(HttpExchange ex, int codigo, String respuesta) throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (body.startsWith("{") && body.endsWith("}")) {
                return JsonUtil.parseObject(body);
            }
            return parseQuery(body);
        }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) return params;
        for (String param : query.split("&")) {
            String[] par = param.split("=", 2);
            if (par.length == 2) {
                params.put(
                    URLDecoder.decode(par[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(par[1], StandardCharsets.UTF_8)
                );
            }
        }
        return params;
    }

    private static LibroSRP parseLibro(Map<String, String> body, int id) {
        return new LibroSRP(
            id,
            body.getOrDefault("autor", "").trim(),
            body.getOrDefault("titulo", "").trim(),
            body.getOrDefault("editorial", "").trim(),
            body.getOrDefault("materia", "Clásicos").trim(),
            parseDouble(body.get("precio"), -1.0),
            parseInt(body.get("stock"), -1)
        );
    }

    private static double parseDouble(String v, double def) {
        if (v == null || v.isBlank()) return def;
        try { return Double.parseDouble(v.trim().replace(",", ".")); } catch (Exception e) { return def; }
    }

    private static int parseInt(String v, int def) {
        if (v == null || v.isBlank()) return def;
        try { return Integer.parseInt(v.trim()); } catch (Exception e) { return def; }
    }

    private static String librosConEstadoToJson(List<LibroSRP> libros) {
        LibreriaFacade f = controller.getFacade();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < libros.size(); i++) {
            LibroSRP l = libros.get(i);
            ContextoPublicacion ctx = f.obtenerContextoEstado(l);
            PublicacionExperto experto = new PublicacionExperto(l.getTitulo(), l.getPrecio(), l.getStock(), 3);
            IRecursoBiblioteca regla = IRecursoBiblioteca.paraTipo(
                l.getMateria() != null && l.getMateria().toLowerCase().contains("cuento") ? "cuento" : "libro");
            if (i > 0) sb.append(",");
            sb.append(String.format(Locale.US,
                "{\"id\":%d,\"autor\":%s,\"titulo\":%s,\"editorial\":%s,\"materia\":%s,\"precio\":%.2f,\"precioConIgv\":%.2f,"
                + "\"stock\":%d,\"estado\":%s,\"estadoColor\":%s,\"permitePrestamo\":%s,\"permiteReserva\":%s,"
                + "\"reservadoPor\":%s,\"alertaStock\":%s,\"diasPrestamo\":%d,\"moraPorDia\":%.2f,\"tipoRecurso\":%s}",
                l.getId(), JsonUtil.str(l.getAutor()), JsonUtil.str(l.getTitulo()),
                JsonUtil.str(l.getEditorial()), JsonUtil.str(l.getMateria()),
                l.getPrecio(), experto.calcularPrecioConIGV(), l.getStock(),
                JsonUtil.str(ctx.getEstado().getNombreEstado()),
                JsonUtil.str(ctx.getEstado().getColorHex()),
                ctx.getEstado().permitirPrestamo(),
                ctx.getEstado().permitirReserva(),
                JsonUtil.str(ctx.getReservadoPor()),
                JsonUtil.str(experto.getNivelAlerta()),
                regla.getDiasPrestamoMaximo(),
                regla.calcularMoraPorDia(),
                JsonUtil.str(regla.getCategoriaTipo())
            ));
        }
        sb.append("]");
        return sb.toString();
    }
}
