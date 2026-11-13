dame # 📚 Librería El Saber — Plataforma Web con Patrones de Diseño (POO)

Sistema integral de catálogo y gestión de librería desarrollado con **Backend en Java (JDK 17)**, persistencia en **MySQL 8.0** y **Frontend Web Moderno en HTML5/CSS3/JavaScript**, servido localmente en **`http://localhost:8084`**.

---

## 🎨 1. Diseño Web e Integración Visual (Basado en la Referencia)

1. **Fondo Difuminado en Toda la Plataforma**:
   - Se utiliza la fotografía de lomos de libros (`web/static/background_books.jpg`) con un filtro CSS de desenfoque (`filter: blur(16px) brightness(0.78)`) y superposición translúcida (`rgba(250, 250, 250, 0.40)`), logrando una atmósfera moderna y envolvente.
2. **Catálogo Estilo E-Reader ("Selecciones especiales")**:
   - Cabecera superior translúcida con flecha de retorno `←`, título principal *"Selecciones especiales"*, buscador en vivo `🔍` y pestañas de navegación (*Catálogo*, *Gestión CRUD*, *Auditoría*).
   - **4 Carruseles temáticos principales** (cada uno con al menos 6 a 7 títulos basados en la imagen de referencia):
     - **Recomendados de amor**: *El silencio entre tú y yo*, *El hechizo del agua*, *Luciérnagas*, *Todos los lugares que mantuvimos en secreto*, *Una historia del amor*, *Historias de diván*.
     - **Novelas y cuentos para las vacaciones**: *El duelo de honor*, *Una historia del amor*, *Cuentos reunidos*, *Aquí vivieron*, *365 días en Buenos Aires*, *Las cosas que perdimos en el fuego*.
     - **Clásicos**: *Cien años de soledad*, *En agosto nos vemos*, *Crónica de una muerte anunciada*, *Antología poética*, *Ficciones verdaderas*, *Bestiario*, *Cortázar de la A a la Z*.
     - **Filosofía, Educación, Ciencia y Tecnología**: *Imposible decir adiós*, *Visión nocturna*, *Mundo loco*, *Biblioteca Pública*, *Nexus*, *Tecnofeudalismo*, *Inteligencia artificial para pintores y artistas*.
   - **Tarjetas de Libro**:
     - Portada con degradados de color y textura de lomo.
     - Título a dos líneas con corte limpio.
     - Nombre del autor.
     - Badge de estado: 🟢 **Disponible** (si hay stock) o 🟠 **Reservar** (si el stock está agotado).
3. **Modal de Adquisición y Detalle Interactivo**:
   - Al pulsar cualquier libro, se abre una ventana modal interactiva donde se configuran formatos, envoltura de regalo, descuentos y facturación formal en tiempo real.
4. **Panel de Gestión de Inventario (CRUD Web)**:
   - Formulario para registrar, modificar y eliminar conectado a los procedimientos almacenados de MySQL (`CRL`, `Mlibro`, `Elibro`).
   - Acciones avanzadas integradas: Duplicar (Prototype), Asistente guiado (Builder), Importación de pack editorial (Abstract Factory), Deshacer (Command Undo) y Restauración de campos (Memento).
5. **Consola de Auditoría y Trazabilidad**:
   - Visor en vivo de eventos registrados mediante el patrón GRASP Fabricación Pura y análisis de antipatrones prevenidos.

---

## 🗺️ 2. Mapeo Completo: Temas del Sílabo (Semanas 1 a 17) vs Funciones del Proyecto

A continuación se detalla cómo cada tema y patrón del curso universitario está implementado en el backend y cómo se prueba orgánicamente en la aplicación web:

| Semana | Unidad y Tema | Patrón / Principio | Ubicación en el Código (Clases y Métodos) | Cómo Funciona y Cómo Probarlo en la Web (`localhost:8084`) |
| :---: | :--- | :--- | :--- | :--- |
| **Sem 1** | **Unidad 1: SOLID** | **Responsabilidad Única (SRP)** | [`patrones.solid.srp.LibroFacturaSRP`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/srp/LibroFacturaSRP.java)<br>Método: `generarComprobante()` | Haz clic en cualquier libro disponible y presiona **"💳 Comprar / Facturar"**. La clase `LibroFacturaSRP` asume exclusivamente la responsabilidad de formatear el ticket fiscal con desglose de IGV (18%), separada de la lógica de negocio y persistencia. |
| **Sem 2** | **Unidad 1: SOLID** | **Abierto/Cerrado (OCP)** | [`patrones.solid.ocp.CalculadoraPrecioOCP`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/ocp/CalculadoraPrecioOCP.java)<br>Interfaz: [`IDescuentoOCP`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/ocp/IDescuentoOCP.java) | En el modal de compra, cambia el selector de **Descuento / Promoción** (*Estudiante 15%*, *Feria del Libro 25%*). El sistema calcula el total extendiendo clases de descuento sin modificar el algoritmo central de la calculadora. |
| **Sem 2** | **Unidad 1: SOLID** | **Sustitución de Liskov (LSP)** | [`patrones.solid.lsp.PublicacionLSP`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/lsp/PublicacionLSP.java)<br>Subclases: `LibroLSP`, `RevistaLSP` | El catálogo web y la base de datos tratan de forma transparente instancias de libros o revistas mediante la clase base abstracta `PublicacionLSP` sin romper el comportamiento del sistema. |
| **Sem 3** | **Unidad 1: SOLID** | **Segregación de Interfaces (ISP)** | [`patrones.solid.isp.IPrestable`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/isp/IPrestable.java), `IVendible`, `IDigital`<br>Clase: `LibroFisicoISP` | Divide interfaces gigantes en contratos pequeños y específicos. Un libro físico implementa `IPrestable` y `IVendible`, mientras que una entrega digital implementa exclusivamente `IDigital`. |
| **Sem 3** | **Unidad 1: SOLID** | **Inversión de Dependencias (DIP)** | [`patrones.solid.dip.IRepositorioDIP`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/solid/dip/IRepositorioDIP.java)<br>Clase: `ServicioPublicacionDIP` | El servidor web de alto nivel no se acopla directamente al driver JDBC concreto; se comunica a través de la abstracción `IRepositorioDIP`, facilitando cambiar el motor de almacenamiento sin alterar la lógica de negocio. |
| **Sem 4** | **Unidad 1: Patrones GOF** | **Concepto y Clasificación GOF** | Estructura modular de paquetes:<br>`patrones.creacionales.*`<br>`patrones.estructurales.*`<br>`patrones.comportamiento.*` | Arquitectura dividida rigurosamente según los 3 propósitos fundamentales de la *Gang of Four* (Creación, Estructura y Comportamiento). |
| **Sem 6** | **Unidad 2: Creacionales** | **Singleton** | [`patrones.creacionales.singleton.ConexionSingleton`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/creacionales/singleton/ConexionSingleton.java)<br>Método: `getInstancia()` | Garantiza un único canal de conexión seguro a la base de datos MySQL `libreria_ElSaber`, reutilizado concurrentemente por todas las peticiones HTTP del servidor web. |
| **Sem 6** | **Unidad 2: Creacionales** | **Prototype** | [`patrones.creacionales.prototype.LibroPrototype`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/creacionales/prototype/LibroPrototype.java)<br>Método: `clonar()` | En la pestaña **"Gestión CRUD"**, selecciona un libro de la tabla y pulsa **"📑 Duplicar Ficha (Prototype)"**. Se clona el objeto en memoria como una nueva edición sin sobrecargar la base de datos. |
| **Sem 7** | **Unidad 2: Creacionales** | **Factory Method** | [`patrones.creacionales.factory.PublicacionFactory`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/creacionales/factory/PublicacionFactory.java)<br>Método: `crearPublicacion()` | Encapsula la instanciación de objetos. Al registrar un nuevo ítem, la fábrica determina si crear un `LibroProducto`, `CuentoProducto` o `RevistaProducto`. |
| **Sem 7** | **Unidad 2: Creacionales** | **Abstract Factory** | [`patrones.creacionales.abstractfactory.EditorialAbstractFactory`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/creacionales/abstractfactory/EditorialAbstractFactory.java)<br>Fábricas: `PlanetaEditorialFactory`, `NormaEditorialFactory` | En **"Gestión CRUD"**, pulsa **"📦 Importar Pack (Abstract Factory)"**. Genera familias completas y coherentes de publicaciones (libro premium + catálogo asociado) según la editorial seleccionada. |
| **Sem 7** | **Unidad 2: Creacionales** | **Builder** | [`patrones.creacionales.builder.LibroBuilder`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/creacionales/builder/LibroBuilder.java)<br>Clase: `DirectorPublicacion` | En **"Gestión CRUD"**, pulsa **"🔨 Asistente (Builder)"**. Permite construir libros paso a paso asignando ISBN, prólogo, tapa dura o ilustraciones antes de emitir la publicación final. |
| **Sem 8** | **Unidad 3: Estructurales** | **Adapter** | [`patrones.estructurales.adapter.LegacyConexionAdapter`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/adapter/LegacyConexionAdapter.java) | Convierte la interfaz del controlador legacy JDBC original (`ConexionLibro`) para que el nuevo servidor web REST opere sin alterar el código heredado. |
| **Sem 8** | **Unidad 3: Estructurales** | **Facade** | [`patrones.estructurales.facade.LibreriaFacade`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/facade/LibreriaFacade.java)<br>Métodos: `listarLibros()`, `registrarLibro()`, etc. | Proporciona una interfaz unificada de alto nivel que simplifica los subsistemas de conexión, consultas de libros y control de inventario hacia la capa web. |
| **Sem 9** | **Unidad 3: Estructurales** | **Decorator** | [`patrones.estructurales.decorator.EmpaqueRegaloDecorator`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/decorator/EmpaqueRegaloDecorator.java)<br>Clase: `GarantiaExtendidaDecorator` | En el modal de compra, activa los checkboxes **"🎁 Empaque de Regalo (+S/ 5.00)"** y/o **"🛡️ Garantía de Reposición (+S/ 3.50)"**. El precio final se decora en tiempo real sin modificar la entidad base. |
| **Sem 9** | **Unidad 3: Estructurales** | **Composite** | [`patrones.estructurales.composite.CategoriaComposite`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/composite/CategoriaComposite.java) | Modela la jerarquía del catálogo en árbol: la raíz contiene categorías (*Amor*, *Clásicos*, *Vacaciones*, *Filosofía*) que a su vez contienen hojas individuales (libros). |
| **Sem 11** | **Unidad 3: Estructurales** | **Proxy** | [`patrones.estructurales.proxy.ProxyCoverService`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/proxy/ProxyCoverService.java) | Actúa como intermediario con memoria caché: intercepta las peticiones de portadas y metadatos para optimizar el rendimiento y reducir la latencia de carga en la web. |
| **Sem 11** | **Unidad 3: Estructurales** | **Bridge** | [`patrones.estructurales.bridge.PublicacionBridge`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/estructurales/bridge/PublicacionBridge.java)<br>Implementaciones: `FormatoFisico`, `FormatoDigitalEbook` | En el modal de compra, selecciona entre **"Impreso Físico (+S/ 0.00)"** o **"E-Book Digital (-20% OFF)"**. Desacopla la abstracción de la publicación de su medio físico de entrega. |
| **Sem 12** | **Unidad 4: Comportamiento** | **State** | [`patrones.comportamiento.state.ContextoPublicacion`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/comportamiento/state/ContextoPublicacion.java)<br>Estados: `EstadoDisponible`, `EstadoAgotado` | Determina el comportamiento de la tarjeta: si hay stock, el estado es *Disponible* (badge verde y botón de compra activo); si el stock es 0, pasa automáticamente a *Agotado* (badge naranja **Reservar** y formulario de aviso por correo). |
| **Sem 12** | **Unidad 4: Comportamiento** | **Observer** | [`patrones.comportamiento.observer.StockObservable`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/comportamiento/observer/StockObservable.java)<br>Observador: `ClienteObserver` | Pulsa cualquier libro con estado **Reservar** e introduce tu correo en **"🔔 Suscríbete para recibir alerta"**. Cuando se actualiza el stock desde el CRUD, el sujeto notifica automáticamente a los observadores registrados. |
| **Sem 13** | **Unidad 4: Comportamiento** | **Command** | [`patrones.comportamiento.command.CommandInvoker`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/comportamiento/command/CommandInvoker.java)<br>Comandos: `RegistrarLibroCommand`, `EliminarLibroCommand` | En la pestaña **"Gestión CRUD"**, registra o elimina un libro y luego pulsa **"↩️ Deshacer (Undo)"**. El invocador ejecuta el método `deshacer()` del comando registrado en la pila, restaurando la base de datos. |
| **Sem 13** | **Unidad 4: Comportamiento** | **Memento** | [`patrones.comportamiento.memento.CaretakerHistorial`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/comportamiento/memento/CaretakerHistorial.java)<br>Clase: `LibroOriginator` | Al escribir en cualquier campo del formulario CRUD, se guardan instantáneas automáticas. Si cometes un error o borras el texto, pulsa **"🕒 Restaurar (Memento)"** para recuperar el estado anterior. |
| **Sem 14** | **Unidad 4: Calidad** | **Antipatrones de Software** | [`patrones.antipatrones.AntipatronesLibreria`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/antipatrones/AntipatronesLibreria.java) | Abre la pestaña **"Auditoría"**. Muestra el análisis comparativo de cómo este proyecto erradicó antipatrones comunes como *God Object*, *Spaghetti Code*, *Hardcoded SQL* y *Lava Flow*. |
| **Sem 16** | **Unidad 5: GRASP** | **Controlador y Creador** | [`patrones.grasp.controlador.LibreriaGRASPController`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/grasp/controlador/LibreriaGRASPController.java)<br>Clase: `VentaItemCreator` | El controlador GRASP actúa como primer receptor de los eventos del sistema web, coordinando las acciones sin saturar la vista. El creador asigna la responsabilidad de instanciar líneas de venta al carrito que las contiene. |
| **Sem 17** | **Unidad 5: GRASP** | **Experto, Fabricación Pura y Polimorfismo** | [`patrones.grasp.experto.PublicacionExperto`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/grasp/experto/PublicacionExperto.java)<br>[`patrones.grasp.fabricacionpura.AuditoriaTransaccionesService`](file:///c:/tareas/8vo/diseño%20de%20patrones/proyecto/ProyectoPoo/src/patrones/grasp/fabricacionpura/AuditoriaTransaccionesService.java) | - **Experto**: Quien tiene la información (precio y materia) calcula el impuesto.<br>- **Fabricación Pura**: `AuditoriaTransaccionesService` gestiona el log de seguridad sin ensuciar la entidad Libro.<br>- **Polimorfismo**: Interfaz `IRecursoBiblioteca` con cálculo polimórfico de moras según el tipo de recurso. |

---

## ⚡ 3. Instrucciones de Ejecución Rápida

### Opción A (Recomendada en Visual Studio Code):
1. Abre el proyecto en VS Code.
2. Presiona la tecla **`F5`** (iniciará la configuración *"Ejecutar Servidor Web Libreria"* de `.vscode/launch.json`).
3. Abre tu navegador en:
   👉 **`http://localhost:8084`**

### Opción B (Desde la Terminal de Windows):
Ejecuta el script incluido en la raíz del proyecto:
```powershell
.\ejecutar.bat
```
o
```powershell
.\ejecutar.ps1
```

---

## 🗄️ 4. Base de Datos MySQL (Procedimientos Almacenados)

El proyecto utiliza la base de datos `libreria_ElSaber` en MySQL (puerto 3306), con soporte dual de contraseñas (`root` y `admin`):
- Procedimiento `CRL`: Consulta y registro de libros.
- Procedimiento `Mlibro`: Modificación y actualización de libros existentes.
- Procedimiento `Elibro`: Eliminación de registros por código.
- Tablas: `libro`, `cuento`, `revista`, `cliente`, `producto`.
