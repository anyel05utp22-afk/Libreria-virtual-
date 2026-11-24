package patrones.grasp.controlador;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import patrones.antipatrones.AntipatronesLibreria;
import patrones.comportamiento.command.CommandInvoker;
import patrones.comportamiento.command.EliminarLibroCommand;
import patrones.comportamiento.command.ModificarLibroCommand;
import patrones.comportamiento.command.RegistrarLibroCommand;
import patrones.comportamiento.memento.CaretakerHistorial;
import patrones.comportamiento.memento.LibroMemento;
import patrones.comportamiento.memento.LibroOriginator;
import patrones.comportamiento.state.ContextoPublicacion;
import patrones.creacionales.abstractfactory.AlfaguaraEditorialFactory;
import patrones.creacionales.abstractfactory.EditorialAbstractFactory;
import patrones.creacionales.abstractfactory.NormaEditorialFactory;
import patrones.creacionales.abstractfactory.PlanetaEditorialFactory;
import patrones.creacionales.builder.DirectorPublicacion;
import patrones.creacionales.builder.LibroBuilder;
import patrones.creacionales.prototype.LibroPrototype;
import patrones.creacionales.prototype.RegistroPrototipos;
import patrones.estructurales.composite.CategoriaComposite;
import patrones.estructurales.facade.LibreriaFacade;
import patrones.grasp.experto.PublicacionExperto;
import patrones.grasp.fabricacionpura.AuditoriaTransaccionesService;
import patrones.grasp.polimorfismo.IRecursoBiblioteca;
import patrones.solid.isp.EjemplarBibliotecaISP;
import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.PublicacionLSP;
import patrones.solid.lsp.RevistaLSP;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 16: Patrón GRASP - Controlador (Controller)
 * Primer objeto más allá de la capa de transporte HTTP / UI que recibe, coordina
 * y valida todas las operaciones del sistema, delegando en LibreriaFacade y en los
 * patrones de comportamiento y creación con bajo acoplamiento y alta cohesión.
 */
public class LibreriaGRASPController {
    private final LibreriaFacade facade;
    private final CommandInvoker commandInvoker;
    private final CaretakerHistorial caretaker;
    private final LibroOriginator originator;
    private final DirectorPublicacion directorBuilder;

    public LibreriaGRASPController() {
        this.facade = new LibreriaFacade();
        this.commandInvoker = new CommandInvoker();
        this.caretaker = new CaretakerHistorial();
        this.originator = new LibroOriginator();
        this.directorBuilder = new DirectorPublicacion();
    }

    public LibreriaFacade getFacade() { return facade; }
    public CommandInvoker getCommandInvoker() { return commandInvoker; }
    public CaretakerHistorial getCaretaker() { return caretaker; }

    // =========================================================================
    // CRUD + VALIDACIÓN SRP + COMMAND (UNDO/REDO)
    // =========================================================================
    public List<LibroSRP> procesarListadoCatalogo() {
        return facade.listarLibros();
    }

    public List<String> validarAntesDeGuardar(LibroSRP libro) {
        return facade.validarLibro(libro);
    }

    public boolean procesarRegistro(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        LibroSRP nuevo = new LibroSRP(0, autor, titulo, editorial, materia, precio, stock);
        if (!facade.validarLibro(nuevo).isEmpty()) return false;
        RegistrarLibroCommand cmd = new RegistrarLibroCommand(facade, nuevo);
        boolean ok = commandInvoker.ejecutarComando(cmd);
        if (ok) {
            AuditoriaTransaccionesService.registrarConPatron("GRASP+COMMAND+SRP", "REGISTRAR_LIBRO", "Admin",
                "#" + cmd.getIdGenerado() + " '" + titulo + "' (stock: " + stock + ")");
        }
        return ok;
    }

    public boolean procesarModificacion(int id, String autor, String titulo, String editorial, String materia, double precio, int stock) {
        if (id <= 0) return false;
        LibroSRP anterior = facade.buscarLibroPorId(id);
        if (anterior == null) return false;
        LibroSRP nuevo = new LibroSRP(id, autor, titulo, editorial, materia, precio, stock);
        if (!facade.validarLibro(nuevo).isEmpty()) return false;

        ModificarLibroCommand cmd = new ModificarLibroCommand(facade, anterior, nuevo);
        boolean ok = commandInvoker.ejecutarComando(cmd);
        if (ok) {
            AuditoriaTransaccionesService.registrarConPatron("GRASP+COMMAND", "MODIFICAR_LIBRO", "Admin",
                "ID #" + id + ": '" + titulo + "' (stock " + anterior.getStock() + " → " + stock + ")");
        }
        return ok;
    }

    public boolean procesarEliminacion(int id) {
        if (id <= 0) return false;
        LibroSRP real = facade.buscarLibroPorId(id);
        if (real == null) return false;
        EliminarLibroCommand cmd = new EliminarLibroCommand(facade, real);
        boolean ok = commandInvoker.ejecutarComando(cmd);
        if (ok) {
            AuditoriaTransaccionesService.registrarConPatron("GRASP+COMMAND", "ELIMINAR_LIBRO", "Admin",
                "ID #" + id + ": '" + real.getTitulo() + "' (respaldado para Undo)");
        }
        return ok;
    }

    public boolean procesarUndo() {
        String desc = commandInvoker.getProximaDescripcionUndo();
        boolean ok = commandInvoker.deshacer();
        if (ok) {
            AuditoriaTransaccionesService.registrarConPatron("COMMAND", "UNDO", "Admin", "Revertido: " + desc);
        }
        return ok;
    }

    public boolean procesarRedo() {
        String desc = commandInvoker.getProximaDescripcionRedo();
        boolean ok = commandInvoker.rehacer();
        if (ok) {
            AuditoriaTransaccionesService.registrarConPatron("COMMAND", "REDO", "Admin", "Reaplicado: " + desc);
        }
        return ok;
    }

    // =========================================================================
    // PROTOTYPE (CLONAR FICHA + PLANTILLAS)
    // =========================================================================
    public LibroPrototype procesarClonacion(String autor, String titulo, String editorial,
                                            String materia, double precio, int stock, String etiquetaEdicion) {
        LibroPrototype prototipo = new LibroPrototype(autor, titulo, editorial, materia, precio, stock);
        LibroPrototype clon = (etiquetaEdicion == null || etiquetaEdicion.isBlank())
            ? (LibroPrototype) prototipo.clonar()
            : prototipo.clonarConEdicion(etiquetaEdicion);
        AuditoriaTransaccionesService.registrarConPatron("PROTOTYPE", "CLONAR_FICHA", "Admin", clon.getInfo());
        return clon;
    }

    public LibroPrototype procesarClonDesdePlantilla(String clavePlantilla) {
        LibroPrototype clon = RegistroPrototipos.obtenerClon(clavePlantilla);
        if (clon != null) {
            AuditoriaTransaccionesService.registrarConPatron("PROTOTYPE", "INSTANCIAR_PLANTILLA", "Admin",
                "Plantilla '" + clavePlantilla + "' → " + clon.getTitulo());
        }
        return clon;
    }

    public Map<String, LibroPrototype> listarPlantillasPrototype() {
        return RegistroPrototipos.listarPlantillas();
    }

    // =========================================================================
    // BUILDER + DIRECTOR PUBLICACION
    // =========================================================================
    public LibroSRP procesarBuilder(String preset, String titulo, String autor, String editorial,
                                    String materia, double precio, int stock,
                                    String isbn, boolean tapaDura, boolean prologo, boolean ilustrado) {
        LibroBuilder builder = new LibroBuilder();
        LibroSRP construido;
        String p = (preset == null ? "personalizado" : preset.trim().toLowerCase());

        switch (p) {
            case "coleccionista":
                construido = directorBuilder.construirEdicionColeccionista(builder, titulo, autor, editorial, materia, precio, stock);
                break;
            case "bolsillo":
                construido = directorBuilder.construirEdicionBolsillo(builder, titulo, autor, editorial, materia, precio, stock);
                break;
            case "academico":
                construido = directorBuilder.construirManualAcademico(builder, titulo, autor, editorial, precio, stock);
                break;
            default:
                construido = builder
                    .setTitulo(titulo)
                    .setAutor(autor)
                    .setEditorial(editorial)
                    .setMateria(materia)
                    .setPrecio(precio)
                    .setStock(stock)
                    .setIsbn(isbn)
                    .setTapaDura(tapaDura)
                    .setConPrologo(prologo)
                    .setIlustrado(ilustrado)
                    .build();
                break;
        }

        RegistrarLibroCommand cmd = new RegistrarLibroCommand(facade, construido);
        if (commandInvoker.ejecutarComando(cmd)) {
            AuditoriaTransaccionesService.registrarConPatron("BUILDER+DIRECTOR", "CONSTRUIR_PUBLICACION", "Admin",
                "Preset=" + p + " | " + construido.getTitulo() + " (S/ " + String.format(Locale.US, "%.2f", construido.getPrecio()) + ")");
            return construido;
        }
        return null;
    }

    // =========================================================================
    // ABSTRACT FACTORY (IMPORTAR FAMILIA COMPLETA: LIBRO + REVISTA)
    // =========================================================================
    public String[] procesarImportacionEditorial(String claveEditorial) {
        String tipo = claveEditorial == null ? "planeta" : claveEditorial.trim().toLowerCase();
        EditorialAbstractFactory factory;
        if (tipo.equals("norma")) {
            factory = new NormaEditorialFactory();
        } else if (tipo.equals("alfaguara")) {
            factory = new AlfaguaraEditorialFactory();
        } else {
            factory = new PlanetaEditorialFactory();
        }

        LibroLSP libro = factory.crearLibroDestacado();
        RevistaLSP revista = factory.crearRevistaDestacada();

        LibroSRP libroSrp = new LibroSRP(0, libro.getAutor(), libro.getTitulo(),
            libro.getEditorial(), libro.getMateria(), libro.getPrecio(), libro.getStock());
        RegistrarLibroCommand cmd = new RegistrarLibroCommand(facade, libroSrp);
        commandInvoker.ejecutarComando(cmd);
        facade.registrarRevistaLSP(revista);

        AuditoriaTransaccionesService.registrarConPatron("ABSTRACT_FACTORY", "IMPORTAR_PACK_EDITORIAL", "Admin",
            factory.getNombreEditorial() + " → Libro: '" + libro.getTitulo() + "' + Revista: '" + revista.getTitulo() + "'");

        return new String[]{
            factory.getNombreEditorial(),
            libro.getFichaTecnica(),
            revista.getFichaTecnica()
        };
    }

    // =========================================================================
    // FACTORY METHOD + LSP (LIBROS, CUENTOS, REVISTAS)
    // =========================================================================
    public List<PublicacionLSP> procesarListadoPublicacionesLSP() {
        return facade.listarTodasPublicacionesLSP();
    }

    public PublicacionLSP procesarRegistroPublicacionFactory(String tipo, String titulo, String autorOMarca,
                                                             String editorialOFecha, String materiaOExtra,
                                                             int numeroExtra, double precio, int stock) {
        PublicacionLSP pub = facade.registrarPublicacionConFactory(tipo, titulo, autorOMarca,
            editorialOFecha, materiaOExtra, numeroExtra, precio, stock);
        if (pub != null) {
            AuditoriaTransaccionesService.registrarConPatron("FACTORY_METHOD+LSP", "REGISTRAR_" + pub.getTipo().toUpperCase(),
                "Admin", pub.getFichaTecnica());
        }
        return pub;
    }

    // =========================================================================
    // MEMENTO (SNAPSHOTS DE FORMULARIO)
    // =========================================================================
    public boolean procesarGuardarMemento(String autor, String titulo, String editorial,
                                          String materia, double precio, int stock) {
        originator.setEstado(autor, titulo, editorial, materia, precio, stock);
        boolean guardado = caretaker.agregarSnapshot(originator.guardarSnapshot());
        if (guardado) {
            AuditoriaTransaccionesService.registrarConPatron("MEMENTO", "GUARDAR_SNAPSHOT", "Admin",
                "Snapshot #" + caretaker.getCantidadSnapshots() + " ('" + titulo + "')");
        }
        return guardado;
    }

    public LibroMemento procesarRestaurarMemento(int indiceOpcional) {
        LibroMemento m = (indiceOpcional >= 0)
            ? caretaker.obtenerPorIndice(indiceOpcional)
            : caretaker.obtenerUltimo();
        if (m != null) {
            originator.restaurar(m);
            AuditoriaTransaccionesService.registrarConPatron("MEMENTO", "RESTAURAR_SNAPSHOT", "Admin",
                "Recuperado estado: '" + m.getTitulo() + "'");
        }
        return m;
    }

    // =========================================================================
    // VENTAS, COTIZACIÓN, PRÉSTAMOS, RESERVAS, OBSERVER, COMPOSITE, PROXY, EXPERTO
    // =========================================================================
    public LibreriaFacade.ResultadoCompra procesarCotizacionOCompra(List<int[]> items, String cliente,
            String formato, String descuento, boolean regalo, boolean garantia, boolean envioExpress, boolean comprar) {
        return facade.cotizarOComprar(items, cliente, formato, descuento, regalo, garantia, envioExpress, comprar);
    }

    public EjemplarBibliotecaISP procesarPrestamo(int idLibro, String tipoRecurso, String dni, String nombre) {
        return facade.solicitarPrestamo(idLibro, tipoRecurso, dni, nombre);
    }

    public EjemplarBibliotecaISP procesarDevolucion(int idPrestamo, int diasRetraso) {
        return facade.devolverPrestamo(idPrestamo, diasRetraso);
    }

    public List<EjemplarBibliotecaISP> procesarListadoPrestamos() {
        return facade.listarPrestamos();
    }

    public double procesarCalculoMoraPolimorfica(String tipoRecurso, int diasRetraso) {
        IRecursoBiblioteca recurso = IRecursoBiblioteca.paraTipo(tipoRecurso);
        return recurso.calcularMoraTotal(diasRetraso);
    }

    public ContextoPublicacion procesarReserva(int idLibro, String cliente) {
        return facade.reservarLibro(idLibro, cliente);
    }

    public ContextoPublicacion procesarLiberarReserva(int idLibro) {
        return facade.liberarReserva(idLibro);
    }

    public void procesarSuscripcionStock(String email, String tituloLibro) {
        facade.suscribirAlertaStock(email, tituloLibro);
    }

    public CategoriaComposite procesarArbolComposite() {
        return facade.construirArbolCatalogo();
    }

    public String procesarPortadaProxy(String titulo) {
        return facade.obtenerPortadaConProxy(titulo);
    }

    public List<PublicacionExperto> procesarMetricasExperto(int umbral) {
        return facade.evaluarInventarioConExperto(umbral);
    }

    public String procesarReporteAntipatrones() {
        return AntipatronesLibreria.generarReporteJson(
            facade.getConexionSingleton().estaConectado(),
            facade.getMotorPersistenciaDIP(),
            facade.getProxyPortadas().getCacheHits(),
            AuditoriaTransaccionesService.getTotalEventos()
        );
    }
}
