// Catálogo inicial con los 26 libros exactos de la imagen de referencia (4 categorías)
const LIBROS_DEFAULT = [
    // 1. Recomendados de amor (6 libros)
    { id: 101, autor: "Ana González", titulo: "El silencio entre tú y yo", editorial: "Planeta", materia: "Recomendados de amor", precio: 38.00, stock: 5 },
    { id: 102, autor: "Florencia Bonelli", titulo: "El hechizo del agua", editorial: "Suma", materia: "Recomendados de amor", precio: 45.00, stock: 0 },
    { id: 103, autor: "Gioconda Belli", titulo: "Luciérnagas", editorial: "Seix Barral", materia: "Recomendados de amor", precio: 32.00, stock: 0 },
    { id: 104, autor: "Inma Rubiales", titulo: "Todos los lugares que mantuvimos en secreto", editorial: "Planeta", materia: "Recomendados de amor", precio: 40.00, stock: 0 },
    { id: 105, autor: "Pedro Saborido", titulo: "Una historia del amor", editorial: "Planeta", materia: "Recomendados de amor", precio: 35.00, stock: 0 },
    { id: 106, autor: "Gabriel Rolón", titulo: "Historias de diván", editorial: "Planeta", materia: "Recomendados de amor", precio: 48.00, stock: 4 },

    // 2. Novelas y cuentos para las vacaciones (6 libros)
    { id: 201, autor: "Varios Autores", titulo: "El duelo de honor", editorial: "Seix Barral", materia: "Novelas y cuentos para las vacaciones", precio: 28.00, stock: 0 },
    { id: 202, autor: "Pedro Saborido", titulo: "Una historia del amor", editorial: "Planeta", materia: "Novelas y cuentos para las vacaciones", precio: 35.00, stock: 0 },
    { id: 203, autor: "Liliana Heker", titulo: "Cuentos reunidos", editorial: "Alfaguara", materia: "Novelas y cuentos para las vacaciones", precio: 30.00, stock: 6 },
    { id: 204, autor: "Manuel Mujica Láinez", titulo: "Aquí vivieron", editorial: "Sudamericana", materia: "Novelas y cuentos para las vacaciones", precio: 34.00, stock: 0 },
    { id: 205, autor: "Diego M. Zigiotto", titulo: "365 días en Buenos Aires", editorial: "Ediciones B", materia: "Novelas y cuentos para las vacaciones", precio: 42.00, stock: 0 },
    { id: 206, autor: "Mariana Enríquez", titulo: "Las cosas que perdimos en el fuego", editorial: "Anagrama", materia: "Novelas y cuentos para las vacaciones", precio: 36.00, stock: 0 },

    // 3. Clásicos (7 libros)
    { id: 301, autor: "Gabriel García Márquez", titulo: "Cien años de soledad", editorial: "Sudamericana", materia: "Clásicos", precio: 45.00, stock: 0 },
    { id: 302, autor: "Gabriel García Márquez", titulo: "En agosto nos vemos", editorial: "Random House", materia: "Clásicos", precio: 49.00, stock: 0 },
    { id: 303, autor: "Gabriel García Márquez", titulo: "Crónica de una muerte anunciada", editorial: "Debolsillo", materia: "Clásicos", precio: 35.00, stock: 8 },
    { id: 304, autor: "Alfonsina Storni", titulo: "Antología poética", editorial: "Losada", materia: "Clásicos", precio: 29.00, stock: 3 },
    { id: 305, autor: "Tomás Eloy Martínez", titulo: "Ficciones verdaderas", editorial: "Planeta", materia: "Clásicos", precio: 38.00, stock: 5 },
    { id: 306, autor: "Julio Cortázar", titulo: "Bestiario", editorial: "Alfaguara", materia: "Clásicos", precio: 42.00, stock: 7 },
    { id: 307, autor: "Julio Cortázar", titulo: "Cortázar de la A a la Z", editorial: "Alfaguara", materia: "Clásicos", precio: 52.00, stock: 4 },

    // 4. Filosofía, Educación, Ciencia y Tecnología (7 libros)
    { id: 401, autor: "Han Kang", titulo: "Imposible decir adiós", editorial: "Random House", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 46.00, stock: 4 },
    { id: 402, autor: "Mariana Enríquez", titulo: "Visión nocturna", editorial: "Ampersand", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 38.00, stock: 6 },
    { id: 403, autor: "Slavoj Žižek", titulo: "Mundo loco", editorial: "Akal", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 44.00, stock: 0 },
    { id: 404, autor: "Ali Smith", titulo: "Biblioteca Pública", editorial: "Nórdica", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 36.00, stock: 5 },
    { id: 405, autor: "Yuval Noah Harari", titulo: "Nexus", editorial: "Debate", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 65.00, stock: 10 },
    { id: 406, autor: "Yanis Varoufakis", titulo: "Tecnofeudalismo", editorial: "Deusto", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 54.00, stock: 4 },
    { id: 407, autor: "Ra-Ma Editorial", titulo: "Inteligencia artificial para pintores y artistas", editorial: "Ra-Ma", materia: "Filosofía, Educación, Ciencia y Tecnología", precio: 59.00, stock: 3 }
];

let todosLosLibros = [...LIBROS_DEFAULT];
let libroSeleccionado = null;
let mementoTimer = null;

// Paletas de color por categoría para recrear las portadas de la Imagen 2
const PALETA_PORTADAS = {
    amor: [
        ['#be123c', '#9f1239'],
        ['#047857', '#065f46'],
        ['#1e293b', '#0f172a'],
        ['#4338ca', '#3730a3'],
        ['#e11d48', '#be123c'],
        ['#334155', '#1e293b']
    ],
    vacaciones: [
        ['#ca8a04', '#a16207'],
        ['#db2777', '#be185d'],
        ['#0f766e', '#115e59'],
        ['#0369a1', '#075985'],
        ['#0284c7', '#0369a1'],
        ['#b91c1c', '#991b1b']
    ],
    clasicos: [
        ['#0d9488', '#0f766e'],
        ['#65a30d', '#4d7c0f'],
        ['#9333ea', '#7e22ce'],
        ['#c026d3', '#a21caf'],
        ['#0284c7', '#0369a1'],
        ['#ea580c', '#c2410c'],
        ['#475569', '#334155']
    ],
    filosofia: [
        ['#d97706', '#b45309'],
        ['#1d4ed8', '#1e40af'],
        ['#c2410c', '#9a3412'],
        ['#059669', '#047857'],
        ['#b91c1c', '#991b1b'],
        ['#dc2626', '#b91c1c'],
        ['#7c3aed', '#6d28d9']
    ]
};

document.addEventListener('DOMContentLoaded', () => {
    // Renderizado inmediato para que jamás aparezca vacío
    renderizarCatalogo(todosLosLibros);
    renderizarTablaInventario(todosLosLibros);

    // Carga asíncrona de datos desde el backend Java + MySQL
    cargarLibros();
    cargarAuditoria();
});

// ====================================================
// 1. CARGA Y FILTRADO DEL CATÁLOGO (DASHBOARD PRINCIPAL)
// ====================================================
async function cargarLibros() {
    try {
        const res = await fetch('/api/libros');
        const data = await res.json();
        if (Array.isArray(data) && data.length > 0) {
            // Unir sin duplicar ids
            const idsExistentes = new Set(data.map(d => d.id));
            const complementarios = LIBROS_DEFAULT.filter(d => !idsExistentes.has(d.id));
            todosLosLibros = [...data, ...complementarios];
        } else {
            todosLosLibros = [...LIBROS_DEFAULT];
        }
        renderizarCatalogo(todosLosLibros);
        renderizarTablaInventario(todosLosLibros);
    } catch (err) {
        console.warn('Conexión con backend en modo local:', err);
        todosLosLibros = [...LIBROS_DEFAULT];
        renderizarCatalogo(todosLosLibros);
        renderizarTablaInventario(todosLosLibros);
    }
}

function filtrarCatalogo() {
    const q = document.getElementById('searchInput').value.trim().toLowerCase();
    if (!q) {
        renderizarCatalogo(todosLosLibros);
        return;
    }
    const filtrados = todosLosLibros.filter(l =>
        (l.titulo || '').toLowerCase().includes(q) ||
        (l.autor || '').toLowerCase().includes(q) ||
        (l.materia || '').toLowerCase().includes(q)
    );
    renderizarCatalogo(filtrados);
}

function renderizarCatalogo(libros) {
    const secAmor = document.getElementById('row-amor');
    const secVacaciones = document.getElementById('row-vacaciones');
    const secClasicos = document.getElementById('row-clasicos');
    const secFilo = document.getElementById('row-filosofia');

    if (!secAmor || !secVacaciones || !secClasicos || !secFilo) return;

    secAmor.innerHTML = '';
    secVacaciones.innerHTML = '';
    secClasicos.innerHTML = '';
    secFilo.innerHTML = '';

    let idxAmor = 0, idxVac = 0, idxClas = 0, idxFilo = 0;

    libros.forEach(l => {
        const mat = (l.materia || '').toLowerCase();

        if (mat.includes('amor') || mat.includes('romance')) {
            secAmor.appendChild(crearCardLibro(l, 'amor', idxAmor++));
        } else if (mat.includes('cuento') || mat.includes('vacaciones') || mat.includes('novela')) {
            secVacaciones.appendChild(crearCardLibro(l, 'vacaciones', idxVac++));
        } else if (mat.includes('filosof') || mat.includes('tecnolog') || mat.includes('ciencia') || mat.includes('educaci')) {
            secFilo.appendChild(crearCardLibro(l, 'filosofia', idxFilo++));
        } else {
            secClasicos.appendChild(crearCardLibro(l, 'clasicos', idxClas++));
        }
    });
}

function crearCardLibro(l, categoria, idx) {
    const div = document.createElement('div');
    div.className = 'book-item-card';
    div.onclick = () => abrirModalCompra(l);

    const paleta = PALETA_PORTADAS[categoria] || PALETA_PORTADAS.clasicos;
    const colores = paleta[idx % paleta.length];
    const grad = `linear-gradient(145deg, ${colores[0]}, ${colores[1]})`;

    const disponible = l.stock > 0;
    const estadoTexto = disponible ? 'Disponible' : 'Reservar';
    const estadoClase = disponible ? 'disponible' : 'reservar';

    div.innerHTML = `
        <div class="book-jacket" style="background: ${grad};">
            <span class="jacket-title">${l.titulo}</span>
            <span class="jacket-author">${l.autor}</span>
        </div>
        <div class="book-meta-below">
            <span class="meta-title" title="${l.titulo}">${l.titulo}</span>
            <span class="meta-author" title="${l.autor}">${l.autor}</span>
            <span class="meta-status ${estadoClase}">${estadoTexto}</span>
        </div>
    `;
    return div;
}

// ====================================================
// 2. MODAL DE ADQUISICIÓN INTERACTIVO (PATRONES GOF)
// ====================================================
function abrirModalCompra(libro) {
    libroSeleccionado = libro;
    document.getElementById('modalTitulo').textContent = libro.titulo;
    document.getElementById('modalDetalleTitulo').textContent = libro.titulo;
    document.getElementById('modalDetalleAutor').textContent = `Por ${libro.autor} • Editorial: ${libro.editorial}`;
    document.getElementById('modalDetalleMeta').textContent = `Categoría: ${libro.materia}`;
    document.getElementById('modalCoverTit').textContent = libro.titulo;

    const badgeEstado = document.getElementById('modalBadgeEstado');
    const badgeStock = document.getElementById('modalBadgeStock');
    const boxSub = document.getElementById('boxSuscripcion');
    const btnComprar = document.getElementById('btnComprarModal');

    badgeStock.textContent = `Stock: ${libro.stock} unid.`;

    if (libro.stock > 0) {
        badgeEstado.className = 'pill-status';
        badgeEstado.style.background = '#dcfce7';
        badgeEstado.style.color = '#15803d';
        badgeEstado.textContent = 'Disponible (State)';
        boxSub.style.display = 'none';
        btnComprar.disabled = false;
        btnComprar.textContent = '💳 Comprar / Facturar (SRP)';
    } else {
        badgeEstado.className = 'pill-status';
        badgeEstado.style.background = '#ffedd5';
        badgeEstado.style.color = '#c2410c';
        badgeEstado.textContent = 'Agotado / Reservar (State)';
        boxSub.style.display = 'block';
        btnComprar.disabled = true;
        btnComprar.textContent = 'Ejemplar Agotado';
    }

    // Reset opciones
    document.querySelector('input[name="formato"][value="fisico"]').checked = true;
    document.getElementById('selDescuento').value = 'ninguno';
    document.getElementById('chkRegalo').checked = false;
    document.getElementById('chkGarantia').checked = false;

    actualizarCalculoModal();
    document.getElementById('modalCompra').classList.add('active');
}

function cerrarModal() {
    document.getElementById('modalCompra').classList.remove('active');
    libroSeleccionado = null;
}

function actualizarCalculoModal() {
    if (!libroSeleccionado) return;
    let total = parseFloat(libroSeleccionado.precio);

    // OCP: Descuento
    const desc = document.getElementById('selDescuento').value;
    if (desc === 'estudiante') total *= 0.85;
    else if (desc === 'temporada') total *= 0.75;

    // Decorator: Servicios
    if (document.getElementById('chkRegalo').checked) total += 5.00;
    if (document.getElementById('chkGarantia').checked) total += 3.50;

    document.getElementById('modalPrecioTotal').textContent = `S/ ${total.toFixed(2)}`;
}

async function confirmarCompra() {
    if (!libroSeleccionado) return;
    const formato = document.querySelector('input[name="formato"]:checked').value;
    const descuento = document.getElementById('selDescuento').value;
    const regalo = document.getElementById('chkRegalo').checked;
    const garantia = document.getElementById('chkGarantia').checked;

    try {
        const res = await fetch('/api/comprar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: libroSeleccionado.id,
                titulo: libroSeleccionado.titulo,
                autor: libroSeleccionado.autor,
                precio: libroSeleccionado.precio,
                formato,
                descuento,
                regalo,
                garantia
            })
        });
        const data = await res.json();
        if (data.success) {
            alert(`¡COMPRA REALIZADA CON ÉXITO!\n\n${data.comprobante}\n\nTotal Cobrado: S/ ${data.total.toFixed(2)}\nFormato: ${data.formato.toUpperCase()}\n\nEl comprobante formal fue emitido con SRP y auditado en el sistema.`);
            cerrarModal();
            cargarAuditoria();
        }
    } catch (err) {
        alert('Error al procesar compra: ' + err);
    }
}

async function suscribirAlertaStock() {
    const email = document.getElementById('txtSubEmail').value.trim();
    if (!email) {
        alert('Por favor ingresa un correo electrónico.');
        return;
    }
    try {
        const res = await fetch('/api/suscribir', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email,
                titulo: libroSeleccionado ? libroSeleccionado.titulo : ''
            })
        });
        const data = await res.json();
        alert(data.message);
        document.getElementById('txtSubEmail').value = '';
        cargarAuditoria();
    } catch (err) {
        alert('Error suscribiendo alerta: ' + err);
    }
}

// ====================================================
// 3. GESTIÓN INVENTARIO CRUD (PRESERVACIÓN TOTAL)
// ====================================================
function renderizarTablaInventario(libros) {
    const tbody = document.getElementById('tablaCuerpo');
    tbody.innerHTML = '';

    libros.forEach(l => {
        const tr = document.createElement('tr');
        tr.onclick = (e) => {
            if (e.target.tagName !== 'BUTTON') seleccionarFilaParaEdicion(l);
        };

        const disponible = l.stock > 0;
        const badgeColor = disponible ? 'color: #15803d; font-weight: 700;' : 'color: #d97706; font-weight: 700;';
        const badgeText = disponible ? 'Disponible' : 'Agotado';

        tr.innerHTML = `
            <td>#${l.id}</td>
            <td><strong>${l.titulo}</strong></td>
            <td>${l.autor}</td>
            <td>${l.editorial}</td>
            <td>${l.materia}</td>
            <td>S/ ${parseFloat(l.precio).toFixed(2)}</td>
            <td>${l.stock}</td>
            <td><span style="${badgeColor}">${badgeText}</span></td>
            <td>
                <button class="tool-btn" style="padding: 3px 8px;" onclick="seleccionarFilaParaEdicion(${JSON.stringify(l).replace(/"/g, '&quot;')})">✏️ Editar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function seleccionarFilaParaEdicion(l) {
    document.getElementById('libroId').value = l.id;
    document.getElementById('txtAutor').value = l.autor;
    document.getElementById('txtTitulo').value = l.titulo;
    document.getElementById('txtEditorial').value = l.editorial;
    document.getElementById('txtMateria').value = l.materia;
    document.getElementById('txtPrecio').value = l.precio;
    document.getElementById('txtStock').value = l.stock;
    document.getElementById('btnSubmitForm').textContent = '✏️ Modificar Producto';
    guardarSnapshotMemento();
}

function limpiarFormulario() {
    document.getElementById('libroId').value = '';
    document.getElementById('crudForm').reset();
    document.getElementById('btnSubmitForm').textContent = '➕ Registrar Producto';
}

async function guardarLibro(e) {
    e.preventDefault();
    const id = document.getElementById('libroId').value;
    const autor = document.getElementById('txtAutor').value.trim();
    const titulo = document.getElementById('txtTitulo').value.trim();
    const editorial = document.getElementById('txtEditorial').value.trim();
    const materia = document.getElementById('txtMateria').value.trim();
    const precio = document.getElementById('txtPrecio').value.trim();
    const stock = document.getElementById('txtStock').value.trim();

    const metodo = id ? 'PUT' : 'POST';
    const body = { id, autor, titulo, editorial, materia, precio, stock };

    try {
        const res = await fetch('/api/libros', {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const data = await res.json();
        if (data.success) {
            mostrarToast(data.message);
            limpiarFormulario();
            cargarLibros();
            cargarAuditoria();
        } else {
            alert(data.message);
        }
    } catch (err) {
        alert('Error al guardar libro: ' + err);
    }
}

async function eliminarLibro() {
    const id = document.getElementById('libroId').value;
    if (!id) {
        alert('Seleccione un libro de la tabla para eliminar.');
        return;
    }
    if (!confirm('¿Confirma que desea eliminar este libro del inventario?')) return;

    try {
        const res = await fetch(`/api/libros?id=${id}`, { method: 'DELETE' });
        const data = await res.json();
        if (data.success) {
            mostrarToast('Libro eliminado correctamente (Command activo)');
            limpiarFormulario();
            cargarLibros();
            cargarAuditoria();
        }
    } catch (err) {
        alert('Error eliminando libro: ' + err);
    }
}

// PROTOTYPE: Duplicar
async function duplicarConPrototype() {
    const titulo = document.getElementById('txtTitulo').value;
    if (!titulo) {
        alert('Selecciona un libro de la tabla para clonarlo como plantilla.');
        return;
    }
    const autor = document.getElementById('txtAutor').value;
    const editorial = document.getElementById('txtEditorial').value;
    const materia = document.getElementById('txtMateria').value;
    const precio = document.getElementById('txtPrecio').value;
    const stock = document.getElementById('txtStock').value;

    try {
        const res = await fetch('/api/clonar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ titulo, autor, editorial, materia, precio, stock })
        });
        const data = await res.json();
        if (data.success) {
            document.getElementById('libroId').value = '';
            document.getElementById('txtTitulo').value = data.titulo;
            document.getElementById('btnSubmitForm').textContent = '➕ Registrar Ficha Clonada';
            mostrarToast('Ficha clonada exitosamente con el Patrón Prototype');
        }
    } catch (err) {
        alert('Error en clonación: ' + err);
    }
}

// BUILDER: Asistente
async function abrirModalBuilder() {
    const titulo = prompt('Título del nuevo libro (Asistente Builder):', 'Patrones de Software Avanzados');
    if (!titulo) return;
    const autor = prompt('Autor:', 'Erich Gamma et al.');
    const editorial = prompt('Editorial:', 'Addison-Wesley');
    const precio = prompt('Precio (S/):', '65.00');
    const stock = prompt('Stock:', '12');

    try {
        const res = await fetch('/api/builder', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ titulo, autor, editorial, materia: 'Filosofía, Educación, Ciencia y Tecnología', precio, stock })
        });
        const data = await res.json();
        mostrarToast(data.message);
        cargarLibros();
        cargarAuditoria();
    } catch (err) {
        alert('Error con Builder: ' + err);
    }
}

// ABSTRACT FACTORY: Importar Pack
async function importarColeccionEditorial() {
    const opcion = prompt('¿Qué colección editorial deseas importar?\n1. Grupo Planeta\n2. Editorial Norma', '1');
    if (!opcion) return;
    const editorial = (opcion === '2') ? 'norma' : 'planeta';

    try {
        const res = await fetch('/api/editorial', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ editorial })
        });
        const data = await res.json();
        alert(data.message);
        cargarLibros();
        cargarAuditoria();
    } catch (err) {
        alert('Error en Abstract Factory: ' + err);
    }
}

// COMMAND: Undo
async function deshacerCommand() {
    try {
        const res = await fetch('/api/undo', { method: 'POST' });
        const data = await res.json();
        if (data.success) {
            mostrarToast(data.message);
            cargarLibros();
            cargarAuditoria();
        } else {
            alert(data.message);
        }
    } catch (err) {
        alert('Error al deshacer: ' + err);
    }
}

// MEMENTO: Guardar y Restaurar
function guardarSnapshotMemento() {
    clearTimeout(mementoTimer);
    mementoTimer = setTimeout(async () => {
        const autor = document.getElementById('txtAutor').value;
        const titulo = document.getElementById('txtTitulo').value;
        const editorial = document.getElementById('txtEditorial').value;
        const materia = document.getElementById('txtMateria').value;
        const precio = document.getElementById('txtPrecio').value || '0';
        const stock = document.getElementById('txtStock').value || '0';

        if (titulo) {
            fetch('/api/memento', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ autor, titulo, editorial, materia, precio, stock })
            });
        }
    }, 800);
}

async function restaurarMemento() {
    try {
        const res = await fetch('/api/memento');
        const data = await res.json();
        if (data.success) {
            document.getElementById('txtAutor').value = data.autor;
            document.getElementById('txtTitulo').value = data.titulo;
            document.getElementById('txtEditorial').value = data.editorial;
            document.getElementById('txtMateria').value = data.materia;
            document.getElementById('txtPrecio').value = data.precio;
            document.getElementById('txtStock').value = data.stock;
            mostrarToast('Snapshot restaurado con el Patrón Memento');
        } else {
            alert(data.message);
        }
    } catch (err) {
        alert('Error restaurando Memento: ' + err);
    }
}

// ====================================================
// 4. AUDITORIA Y SISTEMA
// ====================================================
async function cargarAuditoria() {
    try {
        const res = await fetch('/api/auditoria');
        const logs = await res.json();
        const contenedor = document.getElementById('auditLogs');
        contenedor.innerHTML = '';
        logs.forEach(log => {
            const div = document.createElement('div');
            div.className = 'log-line';
            div.textContent = log;
            contenedor.appendChild(div);
        });
    } catch (err) {
        console.error('Error cargando auditoria:', err);
    }
}

function cambiarVista(vista) {
    document.querySelectorAll('.pill-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.content-view').forEach(v => v.classList.remove('active'));

    if (vista === 'catalogo') {
        document.getElementById('btnTabCatalogo').classList.add('active');
        document.getElementById('vista-catalogo').classList.add('active');
    } else if (vista === 'inventario') {
        document.getElementById('btnTabInventario').classList.add('active');
        document.getElementById('vista-inventario').classList.add('active');
    } else if (vista === 'auditoria') {
        document.getElementById('btnTabAuditoria').classList.add('active');
        document.getElementById('vista-auditoria').classList.add('active');
        cargarAuditoria();
    }
}

function mostrarToast(mensaje, error = false) {
    const c = document.getElementById('toastContainer');
    const t = document.createElement('div');
    t.className = 'toast-pill';
    t.innerHTML = `<span>${error ? '⚠️' : '✅'}</span> <span>${mensaje}</span>`;
    c.appendChild(t);
    setTimeout(() => {
        t.style.opacity = '0';
        setTimeout(() => t.remove(), 250);
    }, 2800);
}
