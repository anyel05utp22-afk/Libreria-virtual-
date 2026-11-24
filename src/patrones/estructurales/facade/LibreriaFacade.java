package patrones.estructurales.facade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import patrones.comportamiento.observer.ClienteObserver;
import patrones.comportamiento.observer.StockObservable;
import patrones.comportamiento.state.ContextoPublicacion;
import patrones.creacionales.factory.PublicacionFactory;
import patrones.creacionales.singleton.ConexionSingleton;
import patrones.estructurales.adapter.LegacyConexionAdapter;
import patrones.estructurales.bridge.IFormatoBridge;
import patrones.estructurales.bridge.LibroBridge;
import patrones.estructurales.bridge.PublicacionBridge;
import patrones.estructurales.composite.CategoriaComposite;
import patrones.estructurales.composite.ItemCatalogoLeaf;
import patrones.estructurales.decorator.EmpaqueRegaloDecorator;
import patrones.estructurales.decorator.EnvioExpressDecorator;
import patrones.estructurales.decorator.GarantiaExtendidaDecorator;
import patrones.estructurales.decorator.IPublicacionComponent;
import patrones.estructurales.decorator.PublicacionBase;
import patrones.estructurales.proxy.ProxyCoverService;
import patrones.grasp.creador.VentaItemCreator;
import patrones.grasp.experto.PublicacionExperto;
import patrones.grasp.fabricacionpura.AuditoriaTransaccionesService;
import patrones.grasp.polimorfismo.IRecursoBiblioteca;
import patrones.solid.dip.IRepositorioDIP;
import patrones.solid.dip.MemoriaLibroRepositorioDIP;
import patrones.solid.dip.MySqlLibroRepositorioDIP;
import patrones.solid.dip.ServicioPublicacionDIP;
import patrones.solid.isp.EbookDigitalISP;
import patrones.solid.isp.EjemplarBibliotecaISP;
import patrones.solid.isp.LibroFisicoISP;
import patrones.solid.lsp.CuentoLSP;
import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.PublicacionLSP;
import patrones.solid.lsp.RevistaLSP;
import patrones.solid.ocp.CalculadoraPrecioOCP;
import patrones.solid.ocp.IDescuentoOCP;
import patrones.solid.ocp.RegistroDescuentosOCP;
import patrones.solid.srp.LibroFacturaSRP;
import patrones.solid.srp.LibroSRP;
import patrones.solid.srp.LibroValidatorSRP;

/**
 * SEMANA 8: Patrón Estructural Facade
 * Interfaz unificada de alto nivel que orquesta los subsistemas de la Librería Virtual:
 * persistencia DIP (MySQL/Memoria + Adapter), validaciones y facturación SRP, descuentos OCP,
 * publicaciones LSP + Factory Method, préstamos/e-books ISP, formatos Bridge, servicios Decorator,
 * jerarquía Composite, caché de portadas Proxy, estados State, alertas Observer y métricas GRASP.
 */
public class LibreriaFacade {
    private final ConexionSingleton db;
    private final LegacyConexionAdapter legacyAdapter;
    private final ServicioPublicacionDIP<LibroSRP> servicioLibrosDIP;
    private final ProxyCoverService proxyPortadas;
    private final StockObservable stockObservable;

    // Estados State por ID de libro
    private final Map<Integer, ContextoPublicacion> estadosPublicacion = new ConcurrentHashMap<>();
    // Préstamos activos (ISP IPrestable + GRASP Polimorfismo)
    private final Map<Integer, EjemplarBibliotecaISP> prestamos = Collections.synchronizedMap(new LinkedHashMap<>());
    private final AtomicInteger secuenciaPrestamo = new AtomicInteger(1);
    // Cuentos y revistas en respaldo en memoria (si MySQL no está disponible)
    private final List<PublicacionLSP> publicacionesExtraMemoria = Collections.synchronizedList(new ArrayList<>());

    public LibreriaFacade() {
        this.db = ConexionSingleton.getInstancia();
        this.legacyAdapter = new LegacyConexionAdapter();
        IRepositorioDIP<LibroSRP> repo = db.estaConectado()
            ? new MySqlLibroRepositorioDIP()
            : new MemoriaLibroRepositorioDIP();
        this.servicioLibrosDIP = new ServicioPublicacionDIP<>(repo);
        this.proxyPortadas = new ProxyCoverService();
        this.stockObservable = new StockObservable();

        if (!db.estaConectado()) {
            publicacionesExtraMemoria.add(PublicacionFactory.obtenerFabrica("cuento")
                .crearPublicacion(1, "El Patito Feo", 18.50, 40, "Hans Christian Andersen", "Planeta Kids", 5, 32));
            publicacionesExtraMemoria.add(PublicacionFactory.obtenerFabrica("revista")
                .crearPublicacion(1, "Spider-Man #1", 15.00, 30, "Marvel Comics", 1, "2024-05-10"));
        }

        AuditoriaTransaccionesService.registrarConPatron("DIP + SINGLETON", "INICIO_SISTEMA", "Sistema",
            "Motor activo: " + servicioLibrosDIP.getMotorPersistencia() + " | " + db.getResumenEstado());
    }

    // =========================================================================
    // 1. GESTIÓN CRUD DE LIBROS (DIP + SRP + STATE + OBSERVER)
    // =========================================================================
    public List<LibroSRP> listarLibros() {
        List<LibroSRP> libros = servicioLibrosDIP.obtenerCatalogo();
        for (LibroSRP l : libros) {
            sincronizarEstado(l);
        }
        return libros;
    }

    public LibroSRP buscarLibroPorId(int id) {
        LibroSRP l = servicioLibrosDIP.buscarPorId(id);
        if (l != null) sincronizarEstado(l);
        return l;
    }

    public List<String> validarLibro(LibroSRP libro) {
        return LibroValidatorSRP.validarConErrores(libro);
    }

    public boolean registrarLibro(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        return registrarLibroRetornandoId(autor, titulo, editorial, materia, precio, stock) > 0;
    }

    public int registrarLibroRetornandoId(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        LibroSRP libro = new LibroSRP(0, autor, titulo, editorial, materia, precio, stock);
        if (!LibroValidatorSRP.validar(libro)) {
            return -1;
        }
        int id = servicioLibrosDIP.registrarRetornandoId(libro);
        if (id > 0) {
            libro.setId(id);
            sincronizarEstado(libro);
        }
        return id;
    }

    public boolean actualizarLibro(int id, String autor, String titulo, String editorial, String materia, double precio, int stock) {
        LibroSRP anterior = buscarLibroPorId(id);
        LibroSRP actualizado = new LibroSRP(id, autor, titulo, editorial, materia, precio, stock);
        if (!LibroValidatorSRP.validar(actualizado)) {
            return false;
        }
        boolean ok = servicioLibrosDIP.actualizar(actualizado);
        if (ok) {
            int stockPrevio = anterior != null ? anterior.getStock() : 0;
            ContextoPublicacion ctx = obtenerContextoEstado(actualizado);
            ctx.setTitulo(titulo);
            ctx.sincronizarStock(stock);
            if (stock > stockPrevio && stock > 0) {
                int notificados = stockObservable.notificarTodos(titulo, stock);
                if (notificados > 0) {
                    AuditoriaTransaccionesService.registrarConPatron("OBSERVER", "ALERTA_STOCK_ENVIADA", "Sistema",
                        notificados + " suscriptor(es) notificados por reposición de '" + titulo + "' (stock: " + stock + ")");
                }
            }
        }
        return ok;
    }

    public boolean eliminarLibro(int id) {
        boolean ok = servicioLibrosDIP.darDeBaja(id);
        if (ok) {
            estadosPublicacion.remove(id);
        }
        return ok;
    }

    // =========================================================================
    // 2. PUBLICACIONES POLIMÓRFICAS (LSP + FACTORY METHOD + ADAPTER CRC/CRR)
    // =========================================================================
    public List<PublicacionLSP> listarTodasPublicacionesLSP() {
        List<PublicacionLSP> resultado = new ArrayList<>();
        PublicacionFactory fabLibro = PublicacionFactory.obtenerFabrica("libro");
        for (LibroSRP l : listarLibros()) {
            resultado.add(fabLibro.crearPublicacion(l.getId(), l.getTitulo(), l.getPrecio(), l.getStock(),
                l.getAutor(), l.getEditorial(), l.getMateria()));
        }

        if (db.estaConectado()) {
            Connection con = db.getConexion();
            if (con != null) {
                PublicacionFactory fabCuento = PublicacionFactory.obtenerFabrica("cuento");
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_cuento, autor, titulo, editorial, edad_recomendada, numero_paginas, precio, stock FROM cuento ORDER BY id_cuento ASC");
                     ResultSet rs = ps.executeQuery()) {
                    db.registrarConsulta();
                    while (rs.next()) {
                        resultado.add(fabCuento.crearPublicacion(
                            rs.getInt("id_cuento"), rs.getString("titulo"), rs.getDouble("precio"), rs.getInt("stock"),
                            rs.getString("autor"), rs.getString("editorial"), rs.getInt("edad_recomendada"), rs.getInt("numero_paginas")));
                    }
                } catch (Exception e) {
                    System.err.println("[Facade - LSP] Aviso leyendo cuentos: " + e.getMessage());
                }

                PublicacionFactory fabRevista = PublicacionFactory.obtenerFabrica("revista");
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_revista, marca, titulo, edicion, fecha_publicacion, precio, stock FROM revista ORDER BY id_revista ASC");
                     ResultSet rs = ps.executeQuery()) {
                    db.registrarConsulta();
                    while (rs.next()) {
                        resultado.add(fabRevista.crearPublicacion(
                            rs.getInt("id_revista"), rs.getString("titulo"), rs.getDouble("precio"), rs.getInt("stock"),
                            rs.getString("marca"), rs.getInt("edicion"), rs.getString("fecha_publicacion")));
                    }
                } catch (Exception e) {
                    System.err.println("[Facade - LSP] Aviso leyendo revistas: " + e.getMessage());
                }
            }
        } else {
            resultado.addAll(publicacionesExtraMemoria);
        }
        return resultado;
    }

    public PublicacionLSP registrarPublicacionConFactory(String tipo, String titulo, String autorOMarca,
                                                         String editorialOFecha, String materiaOExtra,
                                                         int numeroExtra, double precio, int stock) {
        PublicacionFactory fabrica = PublicacionFactory.obtenerFabrica(tipo);
        String t = tipo == null ? "libro" : tipo.trim().toLowerCase();

        if (t.equals("cuento")) {
            int edad = Math.max(3, numeroExtra > 0 ? numeroExtra : 7);
            int paginas = 48;
            try { paginas = Integer.parseInt(materiaOExtra); } catch (Exception ignored) {}
            CuentoLSP cuento = (CuentoLSP) fabrica.registrarEnInventario(0, titulo, precio, stock,
                autorOMarca, editorialOFecha, edad, paginas);
            if (db.estaConectado()) {
                legacyAdapter.insertarCuento(cuento);
            } else {
                publicacionesExtraMemoria.add(cuento);
            }
            return cuento;
        } else if (t.equals("revista")) {
            int edicion = Math.max(1, numeroExtra > 0 ? numeroExtra : 1);
            String fecha = (editorialOFecha == null || editorialOFecha.isBlank()) ? "2026-01-01" : editorialOFecha;
            RevistaLSP revista = (RevistaLSP) fabrica.registrarEnInventario(0, titulo, precio, stock,
                autorOMarca, edicion, fecha);
            if (db.estaConectado()) {
                legacyAdapter.insertarRevista(revista);
            } else {
                publicacionesExtraMemoria.add(revista);
            }
            return revista;
        } else {
            String materia = (materiaOExtra == null || materiaOExtra.isBlank()) ? "Clásicos" : materiaOExtra;
            int id = registrarLibroRetornandoId(autorOMarca, titulo, editorialOFecha, materia, precio, stock);
            return fabrica.registrarEnInventario(id, titulo, precio, stock, autorOMarca, editorialOFecha, materia);
        }
    }

    public boolean registrarRevistaLSP(RevistaLSP revista) {
        if (revista == null) return false;
        if (db.estaConectado()) {
            return legacyAdapter.insertarRevista(revista);
        }
        publicacionesExtraMemoria.add(revista);
        return true;
    }

    // =========================================================================
    // 3. COTIZACIÓN Y COMPRA (BRIDGE + OCP + DECORATOR + GRASP CREATOR + ISP + SRP)
    // =========================================================================
    public static class ResultadoCompra {
        public boolean exito;
        public String mensaje;
        public String numeroBoleta = "";
        public String fechaEmision = "";
        public String clienteNombre = "Cliente Web";
        public List<String> lineasDetalle = new ArrayList<>();
        public double subtotalBruto;
        public double descuentoAplicado;
        public String descripcionDescuento;
        public double costoExtras;
        public String descripcionExtras;
        public double subtotalNeto;
        public double igv;
        public double totalFinal;
        public String formatoNombre;
        public String detalleEntrega;
        public String comprobante;
        public String urlDescargaDigital;
        public String tamanoDigital;
    }

    public ResultadoCompra cotizarOComprar(List<int[]> itemsIdCantidad, String cliente, String formatoClave,
                                           String descuentoClave, boolean regalo, boolean garantia,
                                           boolean envioExpress, boolean ejecutarCompra) {
        ResultadoCompra res = new ResultadoCompra();
        IFormatoBridge formato = PublicacionBridge.resolverFormato(formatoClave);
        VentaItemCreator.OrdenVenta orden = VentaItemCreator.crearOrden();

        List<LibroSRP> librosComprados = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        StringBuilder detalleEntregas = new StringBuilder();
        EbookDigitalISP primerEbook = null;

        for (int[] par : itemsIdCantidad) {
            int id = par[0];
            int cant = Math.max(1, par[1]);
            LibroSRP libro = buscarLibroPorId(id);
            if (libro == null) {
                res.exito = false;
                res.mensaje = "No se encontró el libro con ID #" + id;
                return res;
            }

            // Bridge: desacopla publicación del formato de entrega y calcula precio ajustado
            PublicacionBridge bridge = new LibroBridge(formato, libro.getTitulo(), libro.getAutor(),
                libro.getEditorial(), libro.getPrecio());

            if (bridge.requiereStockFisico()) {
                // ISP: IVendible (LibroFisicoISP) valida disponibilidad física
                LibroFisicoISP fisico = new LibroFisicoISP(libro.getTitulo(), libro.getPrecio(), libro.getStock());
                if (fisico.getStockDisponible() < cant) {
                    res.exito = false;
                    res.mensaje = "Stock físico insuficiente para '" + libro.getTitulo()
                        + "' (Disponible: " + libro.getStock() + ", Solicitado: " + cant + ").";
                    return res;
                }
            } else if (primerEbook == null) {
                // ISP: IDigital (EbookDigitalISP) provee enlace y metadatos de descarga
                primerEbook = new EbookDigitalISP(libro.getId(), libro.getTitulo(), libro.getPrecio());
            }

            // GRASP Creator: la orden crea sus líneas de venta
            orden.agregarItem(libro, cant, bridge.getPrecioFinal());
            librosComprados.add(libro);
            cantidades.add(cant);
            if (detalleEntregas.length() > 0) detalleEntregas.append(" | ");
            detalleEntregas.append(bridge.getDetalleEntrega());
        }

        if (orden.getLineas().isEmpty()) {
            res.exito = false;
            res.mensaje = "No se enviaron libros para procesar.";
            return res;
        }

        double subtotalBruto = orden.calcularSubtotalBruto();
        // OCP: estrategia de descuento sin modificar CalculadoraPrecioOCP
        IDescuentoOCP estrategia = RegistroDescuentosOCP.obtener(descuentoClave);
        double precioConDescuento = CalculadoraPrecioOCP.calcularPrecioFinal(subtotalBruto, estrategia);
        double montoDescuento = Math.max(0.0, subtotalBruto - precioConDescuento);

        // Decorator: envolver servicios adicionales dinámicamente
        IPublicacionComponent componente = new PublicacionBase("Pedido Librería El Saber", precioConDescuento);
        List<String> extrasNombres = new ArrayList<>();
        if (regalo) {
            componente = new EmpaqueRegaloDecorator(componente);
            extrasNombres.add("Empaque Regalo");
        }
        if (garantia) {
            componente = new GarantiaExtendidaDecorator(componente);
            extrasNombres.add("Garantía 30d");
        }
        if (envioExpress) {
            componente = new EnvioExpressDecorator(componente);
            extrasNombres.add("Envío Express");
        }

        double subtotalNeto = componente.getPrecioFinal();
        double montoExtras = Math.max(0.0, subtotalNeto - precioConDescuento);
        double igv = subtotalNeto * 0.18;
        double totalConIgv = subtotalNeto + igv;

        // Si es compra real, descontar stock físico y actualizar máquina de estados State
        if (ejecutarCompra) {
            if (formato.consumeStockFisico()) {
                for (int i = 0; i < librosComprados.size(); i++) {
                    LibroSRP l = librosComprados.get(i);
                    int cant = cantidades.get(i);
                    int nuevoStock = Math.max(0, l.getStock() - cant);
                    actualizarLibro(l.getId(), l.getAutor(), l.getTitulo(), l.getEditorial(), l.getMateria(), l.getPrecio(), nuevoStock);
                }
            } else if (primerEbook != null) {
                primerEbook.abrirLectorDigital();
            }

            String descExtras = extrasNombres.isEmpty() ? "Ninguno" : String.join(" + ", extrasNombres);
            res.numeroBoleta = LibroFacturaSRP.generarNumeroBoleta();
            res.fechaEmision = LibroFacturaSRP.obtenerFechaActual();
            res.clienteNombre = (cliente == null || cliente.isBlank()) ? "Cliente Web" : cliente.trim();
            res.lineasDetalle = orden.getDescripcionesLineas();
            res.comprobante = LibroFacturaSRP.generarComprobanteDetallado(
                res.clienteNombre,
                res.lineasDetalle,
                formato.getNombreFormato(),
                estrategia.getDescripcion(),
                montoDescuento,
                descExtras,
                montoExtras,
                subtotalNeto
            );

            AuditoriaTransaccionesService.registrarConPatron("SRP+OCP+BRIDGE+DECORATOR", "VENTA_FACTURADA",
                (cliente == null || cliente.isBlank() ? "ClienteWeb" : cliente),
                orden.getTotalUnidades() + " ítem(s) | " + formato.getNombreFormato() + " | Total c/IGV: S/ "
                    + String.format(Locale.US, "%.2f", totalConIgv));
        }

        res.exito = true;
        res.mensaje = ejecutarCompra ? "Compra facturada exitosamente." : "Cotización calculada.";
        res.subtotalBruto = subtotalBruto;
        res.descuentoAplicado = montoDescuento;
        res.descripcionDescuento = estrategia.getDescripcion();
        res.costoExtras = montoExtras;
        res.descripcionExtras = extrasNombres.isEmpty() ? "Ninguno" : String.join(" + ", extrasNombres);
        res.subtotalNeto = subtotalNeto;
        res.igv = igv;
        res.totalFinal = totalConIgv;
        res.formatoNombre = formato.getNombreFormato();
        res.detalleEntrega = detalleEntregas.toString();
        if (primerEbook != null) {
            res.urlDescargaDigital = primerEbook.getUrlDescarga();
            res.tamanoDigital = primerEbook.getTamanoLegible();
        } else {
            res.urlDescargaDigital = "";
            res.tamanoDigital = "";
        }
        return res;
    }

    // =========================================================================
    // 4. PRÉSTAMOS Y DEVOLUCIONES (ISP IPrestable + GRASP Polimorfismo + STATE)
    // =========================================================================
    public EjemplarBibliotecaISP solicitarPrestamo(int idLibro, String tipoRecurso, String dniCliente, String nombreCliente) {
        LibroSRP libro = buscarLibroPorId(idLibro);
        if (libro == null) return null;
        ContextoPublicacion ctx = obtenerContextoEstado(libro);
        if (!ctx.getEstado().permitirPrestamo() || libro.getStock() <= 0) {
            return null;
        }
        IRecursoBiblioteca recurso = IRecursoBiblioteca.paraTipo(tipoRecurso);
        int idPrestamo = secuenciaPrestamo.getAndIncrement();
        EjemplarBibliotecaISP ejemplar = new EjemplarBibliotecaISP(
            idPrestamo, libro.getId(), libro.getTitulo(),
            recurso.getCategoriaTipo(), recurso.getDiasPrestamoMaximo(), recurso.calcularMoraPorDia()
        );
        if (!ejemplar.prestarConNombre(dniCliente, nombreCliente)) {
            return null;
        }

        // State transition + actualizar stock en repositorio
        ctx.prestar();
        actualizarLibro(libro.getId(), libro.getAutor(), libro.getTitulo(), libro.getEditorial(),
            libro.getMateria(), libro.getPrecio(), Math.max(0, libro.getStock() - 1));
        prestamos.put(idPrestamo, ejemplar);

        AuditoriaTransaccionesService.registrarConPatron("ISP+STATE+POLIMORFISMO", "PRESTAMO_REGISTRADO",
            ejemplar.getNombreCliente() + " (DNI " + ejemplar.getDniCliente() + ")",
            "'" + libro.getTitulo() + "' (" + recurso.getCategoriaTipo() + ", plazo " + recurso.getDiasPrestamoMaximo() + " días)");
        return ejemplar;
    }

    public EjemplarBibliotecaISP devolverPrestamo(int idPrestamo, int diasRetraso) {
        EjemplarBibliotecaISP ejemplar = prestamos.get(idPrestamo);
        if (ejemplar == null || !ejemplar.estaPrestado()) return null;

        ejemplar.devolver();
        LibroSRP libro = buscarLibroPorId(ejemplar.getIdPublicacion());
        if (libro != null) {
            ContextoPublicacion ctx = obtenerContextoEstado(libro);
            ctx.devolver();
            actualizarLibro(libro.getId(), libro.getAutor(), libro.getTitulo(), libro.getEditorial(),
                libro.getMateria(), libro.getPrecio(), libro.getStock() + 1);
        }

        double mora = ejemplar.calcularMoraEstimada(diasRetraso);
        AuditoriaTransaccionesService.registrarConPatron("ISP+STATE+OBSERVER", "PRESTAMO_DEVUELTO",
            ejemplar.getNombreCliente(),
            "Préstamo #" + idPrestamo + " ('" + ejemplar.getTitulo() + "') devuelto. Mora: S/ " + String.format(Locale.US, "%.2f", mora));
        return ejemplar;
    }

    public List<EjemplarBibliotecaISP> listarPrestamos() {
        return new ArrayList<>(prestamos.values());
    }

    // =========================================================================
    // 5. STATE (RESERVAR / LIBERAR) & OBSERVER (ALERTAS DE STOCK)
    // =========================================================================
    public ContextoPublicacion obtenerContextoEstado(LibroSRP libro) {
        ContextoPublicacion ctx = estadosPublicacion.computeIfAbsent(
            libro.getId(), id -> new ContextoPublicacion(libro.getTitulo(), libro.getStock()));
        ctx.setTitulo(libro.getTitulo());
        ctx.sincronizarStock(libro.getStock());
        return ctx;
    }

    private void sincronizarEstado(LibroSRP libro) {
        obtenerContextoEstado(libro);
    }

    public ContextoPublicacion reservarLibro(int idLibro, String cliente) {
        LibroSRP libro = buscarLibroPorId(idLibro);
        if (libro == null) return null;
        ContextoPublicacion ctx = obtenerContextoEstado(libro);
        ctx.reservar(cliente);
        AuditoriaTransaccionesService.registrarConPatron("STATE", "RESERVA_ACTIVA",
            ctx.getReservadoPor(), "Ejemplar '" + libro.getTitulo() + "' cambiado a EstadoReservado");
        return ctx;
    }

    public ContextoPublicacion liberarReserva(int idLibro) {
        LibroSRP libro = buscarLibroPorId(idLibro);
        if (libro == null) return null;
        ContextoPublicacion ctx = obtenerContextoEstado(libro);
        ctx.liberarReserva();
        AuditoriaTransaccionesService.registrarConPatron("STATE", "RESERVA_LIBERADA",
            "Admin", "Ejemplar '" + libro.getTitulo() + "' → " + ctx.getEstado().getNombreEstado());
        return ctx;
    }

    public void suscribirAlertaStock(String email, String tituloLibro) {
        stockObservable.suscribirParaLibro(tituloLibro, new ClienteObserver(email, email));
        AuditoriaTransaccionesService.registrarConPatron("OBSERVER", "SUSCRIPCION_STOCK", email,
            "Alerta registrada para: " + (tituloLibro == null || tituloLibro.isBlank() ? "Todo el catálogo" : tituloLibro));
    }

    public StockObservable getStockObservable() {
        return stockObservable;
    }

    // =========================================================================
    // 6. COMPOSITE (ÁRBOL), PROXY (PORTADAS) Y GRASP EXPERTO (MÉTRICAS STOCK)
    // =========================================================================
    public CategoriaComposite construirArbolCatalogo() {
        CategoriaComposite raiz = new CategoriaComposite("Librería Virtual El Saber");
        Map<String, CategoriaComposite> categorias = new LinkedHashMap<>();

        for (LibroSRP l : listarLibros()) {
            String mat = (l.getMateria() == null || l.getMateria().isBlank()) ? "General" : l.getMateria();
            CategoriaComposite cat = categorias.computeIfAbsent(mat, CategoriaComposite::new);
            cat.agregar(new ItemCatalogoLeaf(l.getId(), l.getTitulo(), l.getAutor(), l.getPrecio(), l.getStock()));
        }
        for (CategoriaComposite c : categorias.values()) {
            raiz.agregar(c);
        }
        return raiz;
    }

    public String obtenerPortadaConProxy(String titulo) {
        return proxyPortadas.cargarPortada(titulo);
    }

    public ProxyCoverService getProxyPortadas() {
        return proxyPortadas;
    }

    public List<PublicacionExperto> evaluarInventarioConExperto(int umbralStockBajo) {
        List<PublicacionExperto> analisis = new ArrayList<>();
        for (LibroSRP l : listarLibros()) {
            analisis.add(new PublicacionExperto(l.getTitulo(), l.getPrecio(), l.getStock(), umbralStockBajo));
        }
        return analisis;
    }

    public ConexionSingleton getConexionSingleton() {
        return db;
    }

    public String getMotorPersistenciaDIP() {
        return servicioLibrosDIP.getMotorPersistencia();
    }
}
