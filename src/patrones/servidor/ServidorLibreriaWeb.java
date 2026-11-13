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
import java.util.Map;
import java.util.concurrent.Executors;
import patrones.comportamiento.command.CommandInvoker;
import patrones.comportamiento.command.EliminarLibroCommand;
import patrones.comportamiento.command.RegistrarLibroCommand;
import patrones.comportamiento.memento.CaretakerHistorial;
import patrones.comportamiento.memento.LibroMemento;
import patrones.comportamiento.memento.LibroOriginator;
import patrones.comportamiento.observer.ClienteObserver;
import patrones.comportamiento.observer.StockObservable;
import patrones.creacionales.abstractfactory.EditorialAbstractFactory;
import patrones.creacionales.abstractfactory.NormaEditorialFactory;
import patrones.creacionales.abstractfactory.PlanetaEditorialFactory;
import patrones.creacionales.builder.LibroBuilder;
import patrones.creacionales.prototype.LibroPrototype;
import patrones.estructurales.decorator.EmpaqueRegaloDecorator;
import patrones.estructurales.decorator.IPublicacionComponent;
import patrones.estructurales.decorator.PublicacionBase;
import patrones.estructurales.facade.LibreriaFacade;
import patrones.grasp.fabricacionpura.AuditoriaTransaccionesService;
import patrones.solid.lsp.LibroLSP;
import patrones.solid.ocp.CalculadoraPrecioOCP;
import patrones.solid.ocp.DescuentoEstudiante;
import patrones.solid.ocp.DescuentoTemporada;
import patrones.solid.ocp.IDescuentoOCP;
import patrones.solid.srp.LibroFacturaSRP;
import patrones.solid.srp.LibroSRP;

public class ServidorLibreriaWeb {
    public static final int PUERTO = 8084;
    private static final LibreriaFacade facade = new LibreriaFacade();
    private static final StockObservable stockObservable = new StockObservable();
    private static final CommandInvoker commandInvoker = new CommandInvoker();
    private static final CaretakerHistorial caretaker = new CaretakerHistorial();
    private static final LibroOriginator originator = new LibroOriginator();
    private static final List<String> logsAuditoria = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/libros", new LibrosHandler());
        server.createContext("/api/comprar", new ComprarHandler());
        server.createContext("/api/suscribir", new SuscribirHandler());
        server.createContext("/api/clonar", new ClonarHandler());
        server.createContext("/api/builder", new BuilderHandler());
        server.createContext("/api/editorial", new EditorialHandler());
        server.createContext("/api/undo", new UndoHandler());
        server.createContext("/api/memento", new MementoHandler());
        server.createContext("/api/auditoria", new AuditoriaHandler());

        System.out.println("=================================================");
        System.out.println("📚 SERVIDOR WEB LIBRERÍA EL SABER INICIADO");
        System.out.println("🌐 URL: http://localhost:" + PUERTO);
        System.out.println("=================================================");

        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PUERTO));
            }
        } catch (Exception ignored) {}

        server.start();
    }

    public static synchronized void registrarAuditoria(String op, String usuario, String detalles) {
        String log = String.format("[%s] Usuario: %s | Operación: %s | Detalles: %s",
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
            usuario, op, detalles);
        logsAuditoria.add(0, log);
        if (logsAuditoria.size() > 50) logsAuditoria.remove(logsAuditoria.size() - 1);
        AuditoriaTransaccionesService.registrarLog(op, usuario, detalles);
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            File f = new File("web" + path);
            if (!f.exists() || f.isDirectory()) {
                f = new File("web/index.html");
            }

            String mime = "text/html; charset=UTF-8";
            if (path.endsWith(".css")) mime = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) mime = "application/javascript; charset=UTF-8";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mime = "image/jpeg";
            else if (path.endsWith(".png")) mime = "image/png";

            ex.getResponseHeaders().set("Content-Type", mime);
            ex.sendResponseHeaders(200, f.length());
            try (OutputStream os = ex.getResponseBody(); FileInputStream fis = new FileInputStream(f)) {
                byte[] buf = new byte[8192];
                int c;
                while ((c = fis.read(buf)) != -1) os.write(buf, 0, c);
            }
        }
    }

    static class LibrosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            String method = ex.getRequestMethod().toUpperCase();
            if (method.equals("OPTIONS")) {
                ex.sendResponseHeaders(204, -1);
                return;
            }

            if (method.equals("GET")) {
                List<LibroSRP> libros = facade.listarLibros();
                libros = asegurarCatalogoCompleto(libros);
                String json = librosToJson(libros);
                enviarRespuesta(ex, 200, json);
            } else if (method.equals("POST")) {
                Map<String, String> body = parseBody(ex);
                String autor = body.getOrDefault("autor", "");
                String titulo = body.getOrDefault("titulo", "");
                String editorial = body.getOrDefault("editorial", "");
                String materia = body.getOrDefault("materia", "Clásicos");
                double precio = Double.parseDouble(body.getOrDefault("precio", "0").replace(",", "."));
                int stock = Integer.parseInt(body.getOrDefault("stock", "0"));

                LibroSRP nuevo = new LibroSRP(0, autor, titulo, editorial, materia, precio, stock);
                RegistrarLibroCommand cmd = new RegistrarLibroCommand(facade, nuevo);
                boolean ok = commandInvoker.ejecutarComando(cmd);
                if (ok) {
                    registrarAuditoria("REGISTRAR_LIBRO", "Admin", titulo);
                    enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro registrado correctamente\"}");
                } else {
                    enviarRespuesta(ex, 500, "{\"success\":false,\"message\":\"Error en base de datos\"}");
                }
            } else if (method.equals("PUT")) {
                Map<String, String> body = parseBody(ex);
                int id = Integer.parseInt(body.getOrDefault("id", "0"));
                String autor = body.getOrDefault("autor", "");
                String titulo = body.getOrDefault("titulo", "");
                String editorial = body.getOrDefault("editorial", "");
                String materia = body.getOrDefault("materia", "Clásicos");
                double precio = Double.parseDouble(body.getOrDefault("precio", "0").replace(",", "."));
                int stock = Integer.parseInt(body.getOrDefault("stock", "0"));

                boolean ok = facade.actualizarLibro(id, autor, titulo, editorial, materia, precio, stock);
                if (ok) {
                    registrarAuditoria("MODIFICAR_LIBRO", "Admin", "ID #" + id + ": " + titulo);
                    if (stock > 0) {
                        stockObservable.notificarTodos(titulo, stock);
                    }
                    enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro modificado exitosamente\"}");
                } else {
                    enviarRespuesta(ex, 500, "{\"success\":false,\"message\":\"Error al modificar\"}");
                }
            } else if (method.equals("DELETE")) {
                String query = ex.getRequestURI().getQuery();
                int id = 0;
                if (query != null && query.contains("id=")) {
                    id = Integer.parseInt(query.split("id=")[1].split("&")[0]);
                }
                if (id > 0) {
                    LibroSRP copia = new LibroSRP(id, "Autor", "Titulo", "Editorial", "Materia", 0, 0);
                    EliminarLibroCommand cmd = new EliminarLibroCommand(facade, copia);
                    boolean ok = commandInvoker.ejecutarComando(cmd);
                    if (ok) {
                        registrarAuditoria("ELIMINAR_LIBRO", "Admin", "ID #" + id);
                        enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro eliminado\"}");
                    } else {
                        enviarRespuesta(ex, 500, "{\"success\":false,\"message\":\"Error eliminando\"}");
                    }
                }
            }
        }
    }

    static class ComprarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            if (ex.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> body = parseBody(ex);
                String titulo = body.getOrDefault("titulo", "Publicación");
                String autor = body.getOrDefault("autor", "Autor");
                double precioBase = Double.parseDouble(body.getOrDefault("precio", "0").replace(",", "."));
                String formato = body.getOrDefault("formato", "fisico");
                String descTipo = body.getOrDefault("descuento", "ninguno");
                boolean regalo = Boolean.parseBoolean(body.getOrDefault("regalo", "false"));
                boolean garantia = Boolean.parseBoolean(body.getOrDefault("garantia", "false"));

                IDescuentoOCP estrategia = null;
                if (descTipo.equals("estudiante")) estrategia = new DescuentoEstudiante();
                else if (descTipo.equals("temporada")) estrategia = new DescuentoTemporada();
                double conDescuento = CalculadoraPrecioOCP.calcularPrecioFinal(precioBase, estrategia);

                IPublicacionComponent componente = new PublicacionBase(titulo, conDescuento);
                if (regalo) componente = new EmpaqueRegaloDecorator(componente);
                double total = componente.getPrecioFinal();
                if (garantia) total += 3.50;

                LibroSRP libroDummy = new LibroSRP(1, autor, titulo, "Editorial", "Materia", precioBase, 1);
                String comprobanteTexto = LibroFacturaSRP.generarComprobante(libroDummy, 1);

                registrarAuditoria("COMPRA_FACTURADA", "ClienteWeb", titulo + " (S/ " + String.format("%.2f", total) + ")");

                String json = String.format("{\"success\":true,\"total\":%.2f,\"comprobante\":\"%s\",\"formato\":\"%s\"}",
                    total, comprobanteTexto.replace("\n", "\\n").replace("\"", "\\\""), formato);
                enviarRespuesta(ex, 200, json);
            }
        }
    }

    static class ClonarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            Map<String, String> body = parseBody(ex);
            String titulo = body.getOrDefault("titulo", "");
            String autor = body.getOrDefault("autor", "");
            String editorial = body.getOrDefault("editorial", "");
            String materia = body.getOrDefault("materia", "");
            double precio = Double.parseDouble(body.getOrDefault("precio", "0").replace(",", "."));
            int stock = Integer.parseInt(body.getOrDefault("stock", "0"));

            LibroPrototype prototipo = new LibroPrototype(autor, titulo, editorial, materia, precio, stock);
            LibroPrototype clon = (LibroPrototype) prototipo.clonar();

            registrarAuditoria("PROTOTYPE_CLONADO", "Admin", clon.getTitulo());
            String json = String.format("{\"success\":true,\"titulo\":\"%s\",\"autor\":\"%s\",\"editorial\":\"%s\",\"materia\":\"%s\",\"precio\":%.2f,\"stock\":%d}",
                clon.getTitulo(), clon.getAutor(), clon.getEditorial(), clon.getMateria(), clon.getPrecio(), clon.getStock());
            enviarRespuesta(ex, 200, json);
        }
    }

    static class BuilderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            Map<String, String> body = parseBody(ex);

            LibroSRP b = new LibroBuilder()
                .setTitulo(body.getOrDefault("titulo", "Libro Especial"))
                .setAutor(body.getOrDefault("autor", "Autor Destacado"))
                .setEditorial(body.getOrDefault("editorial", "Edición Coleccionista"))
                .setMateria(body.getOrDefault("materia", "Clásicos"))
                .setPrecio(Double.parseDouble(body.getOrDefault("precio", "45.0").replace(",", ".")))
                .setStock(Integer.parseInt(body.getOrDefault("stock", "10")))
                .build();

            facade.registrarLibro(b.getAutor(), b.getTitulo(), b.getEditorial(), b.getMateria(), b.getPrecio(), b.getStock());
            registrarAuditoria("BUILDER_CREACION", "Admin", b.getTitulo());

            enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Libro construido con éxito con el patrón Builder\"}");
        }
    }

    static class EditorialHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            Map<String, String> body = parseBody(ex);
            String tipo = body.getOrDefault("editorial", "planeta");

            EditorialAbstractFactory factory = tipo.equalsIgnoreCase("norma")
                ? new NormaEditorialFactory()
                : new PlanetaEditorialFactory();

            LibroLSP libro = factory.crearLibroDestacado();
            facade.registrarLibro(libro.getAutor(), libro.getTitulo(), libro.getEditorial(), libro.getMateria(), libro.getPrecio(), libro.getStock());
            registrarAuditoria("ABSTRACT_FACTORY_IMPORT", "Admin", factory.getNombreEditorial() + ": " + libro.getTitulo());

            String json = String.format("{\"success\":true,\"message\":\"Colección importada con éxito de %s: %s\"}",
                factory.getNombreEditorial(), libro.getTitulo());
            enviarRespuesta(ex, 200, json);
        }
    }

    static class UndoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            boolean ok = commandInvoker.deshacer();
            if (ok) {
                registrarAuditoria("COMMAND_UNDO", "Admin", "Operación revertida");
                enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Última operación deshecha con éxito (Command Undo)\"}");
            } else {
                enviarRespuesta(ex, 400, "{\"success\":false,\"message\":\"No hay operaciones previas en el historial para deshacer\"}");
            }
        }
    }

    static class MementoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            String method = ex.getRequestMethod().toUpperCase();
            if (method.equals("POST")) {
                Map<String, String> body = parseBody(ex);
                originator.setEstado(
                    body.getOrDefault("autor", ""),
                    body.getOrDefault("titulo", ""),
                    body.getOrDefault("editorial", ""),
                    body.getOrDefault("materia", ""),
                    Double.parseDouble(body.getOrDefault("precio", "0").replace(",", ".")),
                    Integer.parseInt(body.getOrDefault("stock", "0"))
                );
                caretaker.agregarSnapshot(originator.guardarSnapshot());
                registrarAuditoria("MEMENTO_SNAPSHOT", "Admin", "Snapshot guardado");
                enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"Snapshot guardado en historial de Memento\"}");
            } else if (method.equals("GET")) {
                LibroMemento m = caretaker.obtenerUltimo();
                if (m != null) {
                    String json = String.format("{\"success\":true,\"titulo\":\"%s\",\"autor\":\"%s\",\"editorial\":\"%s\",\"materia\":\"%s\",\"precio\":%.2f,\"stock\":%d}",
                        m.getTitulo(), m.getAutor(), m.getEditorial(), m.getMateria(), m.getPrecio(), m.getStock());
                    enviarRespuesta(ex, 200, json);
                } else {
                    enviarRespuesta(ex, 404, "{\"success\":false,\"message\":\"No hay snapshots disponibles en el historial\"}");
                }
            }
        }
    }

    static class SuscribirHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            Map<String, String> body = parseBody(ex);
            String email = body.getOrDefault("email", "lector@correo.com");
            String titulo = body.getOrDefault("titulo", "");

            stockObservable.suscribir(new ClienteObserver(email, email));
            registrarAuditoria("OBSERVER_SUSCRIPCION", email, "Libro: " + titulo);

            enviarRespuesta(ex, 200, "{\"success\":true,\"message\":\"¡Suscripción confirmada! Te avisaremos cuando haya stock disponible.\"}");
        }
    }

    static class AuditoriaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < logsAuditoria.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(logsAuditoria.get(i).replace("\"", "\\\"")).append("\"");
            }
            sb.append("]");
            enviarRespuesta(ex, 200, sb.toString());
        }
    }

    private static void enviarRespuesta(HttpExchange ex, int codigo, String respuesta) throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseBody(HttpExchange ex) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (InputStream is = ex.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (body.startsWith("{") && body.endsWith("}")) {
                String contenido = body.substring(1, body.length() - 1);
                String[] pares = contenido.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String par : pares) {
                    String[] kv = par.split(":", 2);
                    if (kv.length == 2) {
                        String clave = kv[0].trim().replace("\"", "");
                        String valor = kv[1].trim().replace("\"", "");
                        params.put(clave, valor);
                    }
                }
            } else {
                for (String param : body.split("&")) {
                    String[] par = param.split("=");
                    if (par.length == 2) {
                        params.put(URLDecoder.decode(par[0], StandardCharsets.UTF_8),
                                   URLDecoder.decode(par[1], StandardCharsets.UTF_8));
                    }
                }
            }
        }
        return params;
    }

    /**
     * Garantiza un catálogo rico con al menos 4 a 6 ejemplos por sección idénticos a la Imagen de Referencia
     */
    private static List<LibroSRP> asegurarCatalogoCompleto(List<LibroSRP> lista) {
        List<LibroSRP> resultado = new ArrayList<>();
        if (lista != null) {
            resultado.addAll(lista);
        }

        // 1. Recomendados de amor (mínimo 6 ejemplos de la imagen)
        resultado.add(new LibroSRP(101, "Ana González", "El silencio entre tú y yo", "Planeta", "Recomendados de amor", 38.0, 5));
        resultado.add(new LibroSRP(102, "Florencia Bonelli", "El hechizo del agua", "Suma", "Recomendados de amor", 45.0, 0));
        resultado.add(new LibroSRP(103, "Gioconda Belli", "Luciérnagas", "Seix Barral", "Recomendados de amor", 32.0, 0));
        resultado.add(new LibroSRP(104, "Inma Rubiales", "Todos los lugares que mantuvimos en secreto", "Planeta", "Recomendados de amor", 40.0, 0));
        resultado.add(new LibroSRP(105, "Pedro Saborido", "Una historia del amor", "Planeta", "Recomendados de amor", 35.0, 0));
        resultado.add(new LibroSRP(106, "Gabriel Rolón", "Historias de diván", "Planeta", "Recomendados de amor", 48.0, 4));

        // 2. Novelas y cuentos para las vacaciones (mínimo 6 ejemplos de la imagen)
        resultado.add(new LibroSRP(201, "Varios Autores", "El duelo de honor", "Seix Barral", "Novelas y cuentos para las vacaciones", 28.0, 0));
        resultado.add(new LibroSRP(202, "Pedro Saborido", "Una historia del amor", "Planeta", "Novelas y cuentos para las vacaciones", 35.0, 0));
        resultado.add(new LibroSRP(203, "Liliana Heker", "Cuentos reunidos", "Alfaguara", "Novelas y cuentos para las vacaciones", 30.0, 6));
        resultado.add(new LibroSRP(204, "Manuel Mujica Láinez", "Aquí vivieron", "Sudamericana", "Novelas y cuentos para las vacaciones", 34.0, 0));
        resultado.add(new LibroSRP(205, "Diego M. Zigiotto", "365 días en Buenos Aires", "Ediciones B", "Novelas y cuentos para las vacaciones", 42.0, 0));
        resultado.add(new LibroSRP(206, "Mariana Enríquez", "Las cosas que perdimos en el fuego", "Anagrama", "Novelas y cuentos para las vacaciones", 36.0, 0));

        // 3. Clásicos (mínimo 7 ejemplos de la imagen)
        resultado.add(new LibroSRP(301, "Gabriel García Márquez", "Cien años de soledad", "Sudamericana", "Clásicos", 45.0, 0));
        resultado.add(new LibroSRP(302, "Gabriel García Márquez", "En agosto nos vemos", "Random House", "Clásicos", 49.0, 0));
        resultado.add(new LibroSRP(303, "Gabriel García Márquez", "Crónica de una muerte anunciada", "Debolsillo", "Clásicos", 35.0, 8));
        resultado.add(new LibroSRP(304, "Alfonsina Storni", "Antología poética", "Losada", "Clásicos", 29.0, 3));
        resultado.add(new LibroSRP(305, "Tomás Eloy Martínez", "Ficciones verdaderas", "Planeta", "Clásicos", 38.0, 5));
        resultado.add(new LibroSRP(306, "Julio Cortázar", "Bestiario", "Alfaguara", "Clásicos", 42.0, 7));
        resultado.add(new LibroSRP(307, "Julio Cortázar", "Cortázar de la A a la Z", "Alfaguara", "Clásicos", 52.0, 4));

        // 4. Filosofía, Educación, Ciencia y Tecnología (mínimo 7 ejemplos de la imagen)
        resultado.add(new LibroSRP(401, "Han Kang", "Imposible decir adiós", "Random House", "Filosofía, Educación, Ciencia y Tecnología", 46.0, 4));
        resultado.add(new LibroSRP(402, "Mariana Enríquez", "Visión nocturna", "Ampersand", "Filosofía, Educación, Ciencia y Tecnología", 38.0, 6));
        resultado.add(new LibroSRP(403, "Slavoj Žižek", "Mundo loco", "Akal", "Filosofía, Educación, Ciencia y Tecnología", 44.0, 0));
        resultado.add(new LibroSRP(404, "Ali Smith", "Biblioteca Pública", "Nórdica", "Filosofía, Educación, Ciencia y Tecnología", 36.0, 5));
        resultado.add(new LibroSRP(405, "Yuval Noah Harari", "Nexus", "Debate", "Filosofía, Educación, Ciencia y Tecnología", 65.0, 10));
        resultado.add(new LibroSRP(406, "Yanis Varoufakis", "Tecnofeudalismo", "Deusto", "Filosofía, Educación, Ciencia y Tecnología", 54.0, 4));
        resultado.add(new LibroSRP(407, "Ra-Ma Editorial", "Inteligencia artificial para pintores y artistas", "Ra-Ma", "Filosofía, Educación, Ciencia y Tecnología", 59.0, 3));

        return resultado;
    }

    private static String librosToJson(List<LibroSRP> libros) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < libros.size(); i++) {
            LibroSRP l = libros.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format("{\"id\":%d,\"autor\":\"%s\",\"titulo\":\"%s\",\"editorial\":\"%s\",\"materia\":\"%s\",\"precio\":%.2f,\"stock\":%d}",
                l.getId(),
                l.getAutor().replace("\"", "\\\""),
                l.getTitulo().replace("\"", "\\\""),
                l.getEditorial().replace("\"", "\\\""),
                l.getMateria().replace("\"", "\\\""),
                l.getPrecio(),
                l.getStock()));
        }
        sb.append("]");
        return sb.toString();
    }
}
