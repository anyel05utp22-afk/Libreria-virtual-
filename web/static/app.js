// ============================================================================
// LIBRERÍA VIRTUAL EL SABER — FRONTEND INTEGRAL (SEMANAS 1 A 17)
// Conectado al servidor Java (localhost:8084) vía GRASP Controller y Facade
// ============================================================================

let todosLosLibros = [];
let libroSeleccionado = null;
let descuentosOcp = [];
let carrito = [];
let mementoTimer = null;
let seccionEnfocada = null;

const PALETA_PORTADAS = {
    amor: [['#be123c', '#9f1239'], ['#047857', '#065f46'], ['#1e293b', '#0f172a'], ['#4338ca', '#3730a3'], ['#e11d48', '#be123c'], ['#334155', '#1e293b']],
    vacaciones: [['#ca8a04', '#a16207'], ['#db2777', '#be185d'], ['#0f766e', '#115e59'], ['#0369a1', '#075985'], ['#0284c7', '#0369a1'], ['#b91c1c', '#991b1b']],
    clasicos: [['#0d9488', '#0f766e'], ['#65a30d', '#4d7c0f'], ['#9333ea', '#7e22ce'], ['#c026d3', '#a21caf'], ['#0284c7', '#0369a1'], ['#ea580c', '#c2410c'], ['#475569', '#334155']],
    filosofia: [['#d97706', '#b45309'], ['#1d4ed8', '#1e40af'], ['#c2410c', '#9a3412'], ['#059669', '#047857'], ['#b91c1c', '#991b1b'], ['#dc2626', '#b91c1c'], ['#7c3aed', '#6d28d9']]
};

document.addEventListener('DOMContentLoaded', () => {
    cargarDescuentosOcp();
    cargarTodoInicial();
});

async function cargarTodoInicial() {
    await cargarLibros();
    cargarHemerotecaLsp();
    cargarEstadoComandos();
    cargarConteoMemento();
    cargarMetricasExperto();
    cargarArbolComposite();
    calcularMoraPolimorfica();
}

// ============================================================================
// 1. CATÁLOGO PRINCIPAL, BUSCADOR Y HEMEROTECA LSP
// ============================================================================
async function cargarLibros() {
    try {
        const res = await fetch('/api/libros');
        const data = await res.json();
        if (Array.isArray(data)) {
            todosLosLibros = data;
            renderizarCatalogo(todosLosLibros);
            renderizarTablaInventario(todosLosLibros);
        }
    } catch (err) {
        console.error('Error cargando libros:', err);
        mostrarToast('No se pudo conectar al servidor Java en localhost:8084', true);
    }
}

async function cargarDescuentosOcp() {
    try {
        const res = await fetch('/api/descuentos');
        descuentosOcp = await res.json();
        const opcionesHtml = descuentosOcp.map(d => `<option value="${d.clave}">${d.descripcion}</option>`).join('');
        ['selDescuento', 'selCartDescuento'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.innerHTML = opcionesHtml;
        });
    } catch (err) {
        console.error('Error cargando descuentos OCP:', err);
    }
}

async function cargarHemerotecaLsp() {
    const cont = document.getElementById('row-hemeroteca');
    if (!cont) return;
    try {
        const res = await fetch('/api/publicaciones');
        const pubs = await res.json();
        const extras = pubs.filter(p => p.tipo !== 'Libro');
        if (extras.length === 0) {
            cont.innerHTML = '<div class="empty-hint">Usa el botón "🏭 Cuento/Revista" en Gestión CRUD para registrar cuentos y revistas con Factory Method.</div>';
            return;
        }
        cont.innerHTML = extras.map(p => {
            const cls = p.tipo.toLowerCase();
            return `
                <div class="lsp-card">
                    <div class="lsp-card-head">
                        <span class="lsp-badge ${cls}">${p.tipo}</span>
                        <strong style="font-size:12px;">S/ ${Number(p.precio).toFixed(2)}</strong>
                    </div>
                    <div class="lsp-card-title">${escapeHtml(p.titulo)}</div>
                    <div class="lsp-card-meta">${escapeHtml(p.autorOMarca)} • ${escapeHtml(p.extra)}</div>
                    <div class="lsp-card-meta">Stock: ${p.stock} unid. • Con IGV: S/ ${Number(p.precioConIgv).toFixed(2)}</div>
                    <div class="lsp-card-rule">Préstamo máx.: ${p.diasPrestamo} días • Mora: S/ ${Number(p.moraDia).toFixed(2)}/día</div>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('Error cargando publicaciones LSP:', err);
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
        (l.materia || '').toLowerCase().includes(q) ||
        (l.editorial || '').toLowerCase().includes(q)
    );
    renderizarCatalogo(filtrados);
}

function enfocarSeccion(secId) {
    const bloques = ['sec-amor', 'sec-vacaciones', 'sec-clasicos', 'sec-filosofia', 'sec-hemeroteca'];
    const titulos = {
        'sec-amor': 'Recomendados de amor',
        'sec-vacaciones': 'Novelas y cuentos para las vacaciones',
        'sec-clasicos': 'Clásicos',
        'sec-filosofia': 'Filosofía, Educación, Ciencia y Tecnología'
    };
    if (seccionEnfocada === secId) {
        limpiarFiltroSeccion();
        return;
    }
    seccionEnfocada = secId;
    bloques.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = (id === secId) ? 'flex' : 'none';
    });
    const banner = document.getElementById('bannerFiltroCategoria');
    document.getElementById('txtFiltroCategoria').textContent = `Sección enfocada: ${titulos[secId] || secId}`;
    banner.style.display = 'flex';
}

function limpiarFiltroSeccion() {
    seccionEnfocada = null;
    ['sec-amor', 'sec-vacaciones', 'sec-clasicos', 'sec-filosofia', 'sec-hemeroteca'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = 'flex';
    });
    document.getElementById('bannerFiltroCategoria').style.display = 'none';
}

function renderizarCatalogo(libros) {
    const secAmor = document.getElementById('row-amor');
    const secVacaciones = document.getElementById('row-vacaciones');
    const secClasicos = document.getElementById('row-clasicos');
    const secFilo = document.getElementById('row-filosofia');
    if (!secAmor) return;

    secAmor.innerHTML = '';
    secVacaciones.innerHTML = '';
    secClasicos.innerHTML = '';
    secFilo.innerHTML = '';

    let iA = 0, iV = 0, iC = 0, iF = 0;
    libros.forEach(l => {
        const mat = (l.materia || '').toLowerCase();
        if (mat.includes('amor') || mat.includes('romance')) {
            secAmor.appendChild(crearCardLibro(l, 'amor', iA++));
        } else if (mat.includes('cuento') || mat.includes('vacaciones') || mat.includes('novela')) {
            secVacaciones.appendChild(crearCardLibro(l, 'vacaciones', iV++));
        } else if (mat.includes('filosof') || mat.includes('tecnolog') || mat.includes('ciencia') || mat.includes('educaci') || mat.includes('historia') || mat.includes('autoayuda')) {
            secFilo.appendChild(crearCardLibro(l, 'filosofia', iF++));
        } else {
            secClasicos.appendChild(crearCardLibro(l, 'clasicos', iC++));
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

    const estadoBackend = (l.estado || (l.stock > 0 ? 'Disponible' : 'Agotado'));
    let estadoClase = 'disponible';
    if (estadoBackend === 'Reservar') estadoClase = 'reservar';
    else if (estadoBackend === 'Agotado') estadoClase = 'agotado';

    div.innerHTML = `
        <div class="book-jacket" style="background: ${grad};">
            <span class="jacket-title">${escapeHtml(l.titulo)}</span>
            <span class="jacket-author">${escapeHtml(l.autor)}</span>
        </div>
        <div class="book-meta-below">
            <span class="meta-title" title="${escapeHtml(l.titulo)}">${escapeHtml(l.titulo)}</span>
            <span class="meta-author" title="${escapeHtml(l.autor)}">${escapeHtml(l.autor)}</span>
            <span class="meta-price">S/ ${Number(l.precio).toFixed(2)}</span>
            <span class="meta-status ${estadoClase}">${escapeHtml(estadoBackend)}</span>
        </div>
    `;
    return div;
}

// ============================================================================
// 2. MODAL DETALLE (PROXY + STATE + BRIDGE + OCP + DECORATOR + ISP + OBSERVER)
// ============================================================================
async function abrirModalCompra(libro) {
    libroSeleccionado = libro;
    document.getElementById('modalTitulo').textContent = libro.titulo;
    document.getElementById('modalDetalleTitulo').textContent = libro.titulo;
    document.getElementById('modalDetalleAutor').textContent = `Por ${libro.autor} • Editorial: ${libro.editorial}`;
    document.getElementById('modalDetalleMeta').textContent = `Categoría: ${libro.materia} • Base: S/ ${Number(libro.precio).toFixed(2)}`;
    document.getElementById('modalCoverTit').textContent = libro.titulo;

    // Reglas dinámicas desde GRASP Polimorfismo
    document.getElementById('modalReglasPrestamo').textContent =
        `${libro.tipoRecurso || 'Libro'}: plazo máximo ${libro.diasPrestamo || 14} días • Mora: S/ ${Number(libro.moraPorDia || 2.5).toFixed(2)}/día (GRASP Polimorfismo)`;

    const badgeEstado = document.getElementById('modalBadgeEstado');
    const badgeStock = document.getElementById('modalBadgeStock');
    const boxSub = document.getElementById('boxSuscripcion');
    const boxPrestamo = document.getElementById('boxPrestamoModal');
    const btnRes = document.getElementById('btnReservarEstado');
    const btnLib = document.getElementById('btnLiberarEstado');

    badgeStock.textContent = `Stock físico: ${libro.stock} unid.`;

    const estado = libro.estado || (libro.stock > 0 ? 'Disponible' : 'Agotado');
    if (estado === 'Disponible') {
        badgeEstado.style.background = '#dcfce7';
        badgeEstado.style.color = '#15803d';
        badgeEstado.textContent = 'Disponible (State)';
        boxSub.style.display = 'none';
        boxPrestamo.style.display = 'block';
        btnRes.style.display = 'inline-block';
        btnLib.style.display = 'none';
    } else if (estado === 'Reservar') {
        badgeEstado.style.background = '#ffedd5';
        badgeEstado.style.color = '#c2410c';
        badgeEstado.textContent = `Reservado${libro.reservadoPor ? ' (' + libro.reservadoPor + ')' : ''} (State)`;
        boxSub.style.display = 'block';
        boxPrestamo.style.display = 'none';
        btnRes.style.display = 'none';
        btnLib.style.display = 'inline-block';
    } else {
        badgeEstado.style.background = '#fee2e2';
        badgeEstado.style.color = '#b91c1c';
        badgeEstado.textContent = 'Agotado (State)';
        boxSub.style.display = 'block';
        boxPrestamo.style.display = 'none';
        btnRes.style.display = 'inline-block';
        btnLib.style.display = 'none';
    }

    // Reset opciones
    document.getElementById('txtCantidadCompra').value = '1';
    document.querySelector('input[name="formato"][value="fisico"]').checked = true;
    document.getElementById('selDescuento').value = 'ninguno';
    document.getElementById('chkRegalo').checked = false;
    document.getElementById('chkGarantia').checked = false;
    document.getElementById('chkExpress').checked = false;

    document.getElementById('modalCompra').classList.add('active');

    // Consultar Proxy de Portadas (Semana 11)
    const badgeProxy = document.getElementById('modalBadgeProxy');
    badgeProxy.textContent = 'Proxy: cargando...';
    try {
        const resP = await fetch(`/api/portada?titulo=${encodeURIComponent(libro.titulo)}`);
        const dataP = await resP.json();
        const coverEl = document.getElementById('modalCover');
        if (dataP.portada && dataP.portada.colorInicio) {
            coverEl.style.background = `linear-gradient(145deg, ${dataP.portada.colorInicio}, ${dataP.portada.colorFin})`;
        }
        badgeProxy.textContent = `Proxy: ${dataP.desdeCache ? 'CACHE HIT ⚡' : 'CACHE MISS'} (${dataP.hitRate}%)`;
    } catch (err) {
        badgeProxy.textContent = 'Proxy activo';
    }

    await actualizarCalculoModal();
}

function cerrarModal(id) {
    const el = document.getElementById(id || 'modalCompra');
    if (el) el.classList.remove('active');
    if (!id || id === 'modalCompra') libroSeleccionado = null;
}

async function actualizarCalculoModal() {
    if (!libroSeleccionado) return;
    const cantidad = Math.max(1, parseInt(document.getElementById('txtCantidadCompra').value || '1', 10));
    const formato = document.querySelector('input[name="formato"]:checked').value;
    const descuento = document.getElementById('selDescuento').value;
    const regalo = document.getElementById('chkRegalo').checked;
    const garantia = document.getElementById('chkGarantia').checked;
    const envioExpress = document.getElementById('chkExpress').checked;

    const btnComprar = document.getElementById('btnComprarModal');
    const esFisicoSinStock = (formato === 'fisico' && libroSeleccionado.stock < cantidad);
    btnComprar.disabled = esFisicoSinStock;
    btnComprar.textContent = esFisicoSinStock ? 'Sin stock físico (prueba E-Book)' : '💳 Comprar / Facturar (SRP)';

    try {
        const res = await fetch('/api/cotizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: libroSeleccionado.id,
                cantidad,
                formato,
                descuento,
                regalo,
                garantia,
                envioExpress
            })
        });
        const data = await res.json();
        if (data.success) {
            document.getElementById('modalPrecioTotal').textContent = `S/ ${Number(data.total).toFixed(2)}`;
            document.getElementById('modalPrecioDesglose').textContent =
                `Bruto: S/ ${Number(data.subtotalBruto).toFixed(2)} | Desc: -S/ ${Number(data.descuento).toFixed(2)} | Extras: +S/ ${Number(data.costoExtras).toFixed(2)} | IGV: S/ ${Number(data.igv).toFixed(2)}`;
        } else {
            document.getElementById('modalPrecioDesglose').textContent = data.message;
        }
    } catch (err) {
        console.error('Error cotizando:', err);
    }
}

async function confirmarCompraDirecta() {
    if (!libroSeleccionado) return;
    const cantidad = Math.max(1, parseInt(document.getElementById('txtCantidadCompra').value || '1', 10));
    const formato = document.querySelector('input[name="formato"]:checked').value;
    const descuento = document.getElementById('selDescuento').value;
    const regalo = document.getElementById('chkRegalo').checked;
    const garantia = document.getElementById('chkGarantia').checked;
    const envioExpress = document.getElementById('chkExpress').checked;

    try {
        const res = await fetch('/api/comprar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: libroSeleccionado.id,
                cantidad,
                cliente: 'Cliente Web',
                formato,
                descuento,
                regalo,
                garantia,
                envioExpress
            })
        });
        const data = await res.json();
        if (data.success) {
            cerrarModal('modalCompra');
            mostrarBoletaModal(data);
            mostrarToast('Venta procesada y stock actualizado');
            await cargarLibros();
            cargarMetricasExperto();
            cargarArbolComposite();
        } else {
            mostrarToast(data.message, true);
        }
    } catch (err) {
        mostrarToast('Error al procesar compra: ' + err, true);
    }
}

function mostrarBoletaModal(data) {
    document.getElementById('boletaNumero').textContent = data.numeroBoleta || 'B001-1001';
    document.getElementById('boletaFecha').textContent = data.fechaEmision || new Date().toLocaleString('es-PE');
    document.getElementById('boletaCliente').textContent = data.cliente || 'Cliente Web';
    document.getElementById('boletaFormato').textContent = data.formato || 'Impreso Físico';

    const ul = document.getElementById('boletaLineas');
    const lineas = Array.isArray(data.lineas) && data.lineas.length > 0
        ? data.lineas
        : ['Publicación adquirida'];
    ul.innerHTML = lineas.map(linea => `<li><span class="receipt-dot"></span><span>${escapeHtml(linea)}</span></li>`).join('');

    document.getElementById('boletaSubtotalBruto').textContent = `S/ ${Number(data.subtotalBruto || 0).toFixed(2)}`;
    const desc = Number(data.descuento || 0);
    const rowDesc = document.getElementById('rowBoletaDescuento');
    if (desc > 0) {
        rowDesc.style.display = 'flex';
        document.getElementById('boletaDescLabel').textContent = data.descripcionDescuento || 'Promoción (OCP)';
        document.getElementById('boletaDescuento').textContent = `-S/ ${desc.toFixed(2)}`;
    } else {
        rowDesc.style.display = 'none';
    }

    const extras = Number(data.costoExtras || 0);
    const rowExtras = document.getElementById('rowBoletaExtras');
    if (extras > 0) {
        rowExtras.style.display = 'flex';
        document.getElementById('boletaExtrasLabel').textContent = data.descripcionExtras || 'Servicios adicionales';
        document.getElementById('boletaExtras').textContent = `+S/ ${extras.toFixed(2)}`;
    } else {
        rowExtras.style.display = 'none';
    }

    document.getElementById('boletaIgv').textContent = `S/ ${Number(data.igv || 0).toFixed(2)}`;
    document.getElementById('boletaTotalFinal').textContent = `S/ ${Number(data.total || 0).toFixed(2)}`;

    const boxDig = document.getElementById('boxDescargaDigital');
    if (data.urlDescargaDigital) {
        boxDig.style.display = 'flex';
        document.getElementById('txtInfoDigital').textContent =
            `Tamaño: ${data.tamanoDigital || '2.4 MB'} • ${data.detalleEntrega || 'EPUB/PDF sin DRM'}`;
        document.getElementById('lnkDescargaDigital').href = data.urlDescargaDigital;
    } else {
        boxDig.style.display = 'none';
    }
    document.getElementById('modalBoleta').classList.add('active');
}

// Préstamo desde modal (ISP IPrestable + GRASP Polimorfismo)
async function solicitarPrestamoDesdeModal() {
    if (!libroSeleccionado) return;
    const cliente = document.getElementById('txtPrestamoCliente').value.trim() || 'Lector Biblioteca';
    const dni = document.getElementById('txtPrestamoDni').value.trim();
    if (dni.length < 6) {
        mostrarToast('Ingresa un DNI válido (mínimo 6 caracteres)', true);
        return;
    }
    const tipoRecurso = (libroSeleccionado.materia || '').toLowerCase().includes('cuento') ? 'cuento' : 'libro';
    try {
        const res = await fetch('/api/prestamos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idLibro: libroSeleccionado.id, tipoRecurso, dni, cliente })
        });
        const data = await res.json();
        if (data.success) {
            mostrarToast(data.message);
            document.getElementById('txtPrestamoDni').value = '';
            cerrarModal('modalCompra');
            await cargarLibros();
            cargarMetricasExperto();
        } else {
            mostrarToast(data.message, true);
        }
    } catch (err) {
        mostrarToast('Error registrando préstamo: ' + err, true);
    }
}

// Reservar / Liberar (Patrón State)
async function reservarEjemplarActual() {
    if (!libroSeleccionado) return;
    try {
        const res = await fetch('/api/reservar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: libroSeleccionado.id, cliente: 'Lector Web' })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        cerrarModal('modalCompra');
        await cargarLibros();
    } catch (err) {
        mostrarToast('Error reservando: ' + err, true);
    }
}

async function liberarReservaActual() {
    if (!libroSeleccionado) return;
    await liberarReservaPorId(libroSeleccionado.id);
    cerrarModal('modalCompra');
}

async function liberarReservaPorId(id) {
    try {
        const res = await fetch('/api/reservar/liberar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        await cargarLibros();
    } catch (err) {
        mostrarToast('Error liberando reserva: ' + err, true);
    }
}

// Observer suscripción
async function suscribirAlertaStock() {
    const email = document.getElementById('txtSubEmail').value.trim();
    if (!email || !email.includes('@')) {
        mostrarToast('Ingresa un correo electrónico válido', true);
        return;
    }
    try {
        const res = await fetch('/api/suscribir', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, titulo: libroSeleccionado ? libroSeleccionado.titulo : '' })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) document.getElementById('txtSubEmail').value = '';
    } catch (err) {
        mostrarToast('Error suscribiendo alerta: ' + err, true);
    }
}

// ============================================================================
// 3. CARRITO DE COMPRAS (GRASP CREADOR)
// ============================================================================
function agregarAlCarritoActual() {
    if (!libroSeleccionado) return;
    const cantidad = Math.max(1, parseInt(document.getElementById('txtCantidadCompra').value || '1', 10));
    const existente = carrito.find(item => item.id === libroSeleccionado.id);
    if (existente) {
        existente.cantidad += cantidad;
    } else {
        carrito.push({
            id: libroSeleccionado.id,
            titulo: libroSeleccionado.titulo,
            precio: libroSeleccionado.precio,
            cantidad
        });
    }
    actualizarBadgeCarrito();
    mostrarToast(`"${libroSeleccionado.titulo}" agregado al carrito`);
    cerrarModal('modalCompra');
}

function actualizarBadgeCarrito() {
    const total = carrito.reduce((sum, i) => sum + i.cantidad, 0);
    document.getElementById('cartCountBadge').textContent = total;
}

function abrirModalCarrito() {
    renderizarCarrito();
    document.getElementById('modalCarrito').classList.add('active');
    recalcularCarrito();
}

function renderizarCarrito() {
    const cont = document.getElementById('carritoItemsContainer');
    if (carrito.length === 0) {
        cont.innerHTML = '<div class="empty-hint">Tu carrito está vacío. Selecciona libros desde el Catálogo para crear líneas de venta (GRASP Creador).</div>';
        document.getElementById('btnCheckoutCarrito').disabled = true;
        document.getElementById('cartTotalFinal').textContent = 'S/ 0.00';
        document.getElementById('cartDesglose').textContent = 'Sin productos';
        return;
    }
    document.getElementById('btnCheckoutCarrito').disabled = false;
    cont.innerHTML = carrito.map((item, idx) => `
        <div class="cart-row">
            <div>
                <strong>${escapeHtml(item.titulo)}</strong>
                <div style="font-size:11px; color:#64748b;">S/ ${Number(item.precio).toFixed(2)} c/u</div>
            </div>
            <div class="cart-qty-ctrl">
                <button class="tool-btn" onclick="cambiarQtyCarrito(${idx}, -1)">-</button>
                <strong>${item.cantidad}</strong>
                <button class="tool-btn" onclick="cambiarQtyCarrito(${idx}, 1)">+</button>
                <button class="tool-btn" onclick="eliminarItemCarrito(${idx})">✕</button>
            </div>
        </div>
    `).join('');
}

function cambiarQtyCarrito(idx, delta) {
    carrito[idx].cantidad += delta;
    if (carrito[idx].cantidad <= 0) carrito.splice(idx, 1);
    actualizarBadgeCarrito();
    renderizarCarrito();
    recalcularCarrito();
}

function eliminarItemCarrito(idx) {
    carrito.splice(idx, 1);
    actualizarBadgeCarrito();
    renderizarCarrito();
    recalcularCarrito();
}

function vaciarCarrito() {
    carrito = [];
    actualizarBadgeCarrito();
    renderizarCarrito();
}

async function recalcularCarrito() {
    if (carrito.length === 0) return;
    const formato = document.querySelector('input[name="cartFormato"]:checked').value;
    const descuento = document.getElementById('selCartDescuento').value;
    const regalo = document.getElementById('chkCartRegalo').checked;
    const garantia = document.getElementById('chkCartGarantia').checked;
    const envioExpress = document.getElementById('chkCartExpress').checked;

    try {
        const res = await fetch('/api/cotizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                items: carrito.map(i => ({ id: i.id, cantidad: i.cantidad })),
                formato, descuento, regalo, garantia, envioExpress
            })
        });
        const data = await res.json();
        if (data.success) {
            document.getElementById('cartTotalFinal').textContent = `S/ ${Number(data.total).toFixed(2)}`;
            document.getElementById('cartDesglose').textContent =
                `Bruto: S/ ${Number(data.subtotalBruto).toFixed(2)} | Desc: -S/ ${Number(data.descuento).toFixed(2)} | Extras: +S/ ${Number(data.costoExtras).toFixed(2)} | IGV: S/ ${Number(data.igv).toFixed(2)}`;
        } else {
            document.getElementById('cartDesglose').textContent = data.message;
        }
    } catch (err) {
        console.error('Error cotizando carrito:', err);
    }
}

async function confirmarCompraCarrito() {
    if (carrito.length === 0) return;
    const cliente = document.getElementById('txtCartCliente').value.trim() || 'Cliente Web';
    const formato = document.querySelector('input[name="cartFormato"]:checked').value;
    const descuento = document.getElementById('selCartDescuento').value;
    const regalo = document.getElementById('chkCartRegalo').checked;
    const garantia = document.getElementById('chkCartGarantia').checked;
    const envioExpress = document.getElementById('chkCartExpress').checked;

    try {
        const res = await fetch('/api/comprar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                items: carrito.map(i => ({ id: i.id, cantidad: i.cantidad })),
                cliente, formato, descuento, regalo, garantia, envioExpress
            })
        });
        const data = await res.json();
        if (data.success) {
            vaciarCarrito();
            cerrarModal('modalCarrito');
            mostrarBoletaModal(data);
            mostrarToast('Carrito facturado con éxito (GRASP Creador + SRP)');
            await cargarLibros();
            cargarMetricasExperto();
            cargarArbolComposite();
        } else {
            mostrarToast(data.message, true);
        }
    } catch (err) {
        mostrarToast('Error facturando carrito: ' + err, true);
    }
}

// ============================================================================
// 4. GESTIÓN INVENTARIO CRUD + CREACIONALES + COMMAND + MEMENTO + COMPOSITE
// ============================================================================
function renderizarTablaInventario(libros) {
    const tbody = document.getElementById('tablaCuerpo');
    if (!tbody) return;
    tbody.innerHTML = '';

    libros.forEach(l => {
        const tr = document.createElement('tr');
        tr.onclick = (e) => {
            if (e.target.tagName !== 'BUTTON') seleccionarFilaParaEdicion(l);
        };

        const est = l.estado || (l.stock > 0 ? 'Disponible' : 'Agotado');
        const badgeColor = est === 'Disponible' ? '#15803d' : (est === 'Reservar' ? '#d97706' : '#dc2626');
        const btnReserva = est === 'Reservar'
            ? `<button class="tool-btn" style="padding:3px 7px;" onclick="liberarReservaPorId(${l.id})">🔓 Liberar</button>`
            : '';

        tr.innerHTML = `
            <td>#${l.id}</td>
            <td><strong>${escapeHtml(l.titulo)}</strong></td>
            <td>${escapeHtml(l.autor)}</td>
            <td>${escapeHtml(l.editorial)}</td>
            <td>${escapeHtml(l.materia)}</td>
            <td>S/ ${Number(l.precio).toFixed(2)} <small style="color:#64748b;">(${Number(l.precioConIgv || l.precio * 1.18).toFixed(2)})</small></td>
            <td><strong>${l.stock}</strong></td>
            <td><span style="color:${badgeColor}; font-weight:700;">${escapeHtml(est)}</span></td>
            <td style="display:flex; gap:4px;">
                <button class="tool-btn" style="padding:3px 7px;" onclick='seleccionarFilaParaEdicion(${JSON.stringify(l).replace(/'/g, "&apos;")})'>✏️ Editar</button>
                ${btnReserva}
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
    document.getElementById('btnSubmitForm').textContent = '✏️ Modificar Libro';
    guardarSnapshotMemento();
}

function limpiarFormulario() {
    document.getElementById('libroId').value = '';
    document.getElementById('crudForm').reset();
    document.getElementById('btnSubmitForm').textContent = '➕ Registrar Libro';
}

async function guardarLibro(e) {
    e.preventDefault();
    const id = document.getElementById('libroId').value;
    const body = {
        id,
        autor: document.getElementById('txtAutor').value.trim(),
        titulo: document.getElementById('txtTitulo').value.trim(),
        editorial: document.getElementById('txtEditorial').value.trim(),
        materia: document.getElementById('txtMateria').value.trim(),
        precio: document.getElementById('txtPrecio').value.trim(),
        stock: document.getElementById('txtStock').value.trim()
    };
    const metodo = id ? 'PUT' : 'POST';

    try {
        const res = await fetch('/api/libros', {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            limpiarFormulario();
            await cargarLibros();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error al guardar libro: ' + err, true);
    }
}

async function eliminarLibro() {
    const id = document.getElementById('libroId').value;
    if (!id) {
        mostrarToast('Selecciona un libro de la tabla para eliminar', true);
        return;
    }
    if (!confirm('¿Confirmas que deseas eliminar este libro? (Puedes revertirlo con Command Undo)')) return;

    try {
        const res = await fetch(`/api/libros?id=${id}`, { method: 'DELETE' });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            limpiarFormulario();
            await cargarLibros();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error eliminando libro: ' + err, true);
    }
}

// PROTOTYPE: Clonar fila actual o desde RegistroPrototipos
async function duplicarConPrototype() {
    const titulo = document.getElementById('txtTitulo').value.trim();
    if (!titulo) {
        mostrarToast('Selecciona un libro de la tabla para clonarlo con Prototype', true);
        return;
    }
    try {
        const res = await fetch('/api/clonar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                titulo,
                autor: document.getElementById('txtAutor').value,
                editorial: document.getElementById('txtEditorial').value,
                materia: document.getElementById('txtMateria').value,
                precio: document.getElementById('txtPrecio').value,
                stock: document.getElementById('txtStock').value,
                edicion: '2da Edición'
            })
        });
        const data = await res.json();
        if (data.success) {
            aplicarClonAlFormulario(data);
            mostrarToast('Ficha clonada en memoria con Patrón Prototype');
        }
    } catch (err) {
        mostrarToast('Error clonando: ' + err, true);
    }
}

async function abrirModalPlantillas() {
    const cont = document.getElementById('listaPlantillasPrototype');
    document.getElementById('modalPlantillas').classList.add('active');
    try {
        const res = await fetch('/api/prototipos');
        const lista = await res.json();
        cont.innerHTML = lista.map(p => `
            <button class="editorial-card" onclick="clonarPlantilla('${p.clave}')">
                <strong>${escapeHtml(p.titulo)}</strong>
                <span>${escapeHtml(p.autor)} • ${escapeHtml(p.editorial)} • ${escapeHtml(p.materia)} • S/ ${Number(p.precio).toFixed(2)}</span>
            </button>
        `).join('');
    } catch (err) {
        cont.innerHTML = '<div class="empty-hint">Error cargando plantillas.</div>';
    }
}

async function clonarPlantilla(clave) {
    try {
        const res = await fetch('/api/clonar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ plantilla: clave })
        });
        const data = await res.json();
        if (data.success) {
            aplicarClonAlFormulario(data);
            cerrarModal('modalPlantillas');
            mostrarToast('Plantilla clonada con RegistroPrototipos (Prototype)');
        }
    } catch (err) {
        mostrarToast('Error clonando plantilla: ' + err, true);
    }
}

function aplicarClonAlFormulario(data) {
    document.getElementById('libroId').value = '';
    document.getElementById('txtTitulo').value = data.titulo;
    document.getElementById('txtAutor').value = data.autor;
    document.getElementById('txtEditorial').value = data.editorial;
    document.getElementById('txtMateria').value = data.materia;
    document.getElementById('txtPrecio').value = data.precio;
    document.getElementById('txtStock').value = data.stock;
    document.getElementById('btnSubmitForm').textContent = '➕ Registrar Ficha Clonada';
}

// BUILDER + DIRECTOR
function abrirModalBuilder() {
    document.getElementById('modalBuilder').classList.add('active');
}

async function ejecutarBuilder(e) {
    e.preventDefault();
    try {
        const res = await fetch('/api/builder', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                preset: document.getElementById('selBuilderPreset').value,
                titulo: document.getElementById('bTitulo').value,
                autor: document.getElementById('bAutor').value,
                editorial: document.getElementById('bEditorial').value,
                materia: document.getElementById('bMateria').value,
                precio: document.getElementById('bPrecio').value,
                stock: document.getElementById('bStock').value,
                isbn: document.getElementById('bIsbn').value,
                tapaDura: document.getElementById('bTapaDura').checked,
                prologo: document.getElementById('bPrologo').checked,
                ilustrado: document.getElementById('bIlustrado').checked
            })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            cerrarModal('modalBuilder');
            await cargarLibros();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error en Builder: ' + err, true);
    }
}

// ABSTRACT FACTORY
function abrirModalEditorial() {
    document.getElementById('modalEditorial').classList.add('active');
}

async function importarPackEditorial(editorial) {
    try {
        const res = await fetch('/api/editorial', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ editorial })
        });
        const data = await res.json();
        mostrarToast(data.success ? `Pack ${data.editorial} importado (Libro + Revista)` : data.message, !data.success);
        if (data.success) {
            cerrarModal('modalEditorial');
            await cargarLibros();
            cargarHemerotecaLsp();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error en Abstract Factory: ' + err, true);
    }
}

// FACTORY METHOD (Cuento / Revista / Libro)
function abrirModalFactory() {
    actualizarCamposFactory();
    document.getElementById('modalFactory').classList.add('active');
}

function actualizarCamposFactory() {
    const tipo = document.getElementById('fTipo').value;
    if (tipo === 'revista') {
        document.getElementById('lblFAutor').textContent = 'Marca / Sello:';
        document.getElementById('lblFEditorial').textContent = 'Fecha Publicación:';
        document.getElementById('fEditorial').value = '2026-04-01';
        document.getElementById('lblFExtra').textContent = 'N° Edición:';
        document.getElementById('fNumeroExtra').value = '15';
        document.getElementById('lblFMateria').textContent = 'Categoría (referencia):';
        document.getElementById('fMateria').value = 'Ciencia y Cultura';
    } else if (tipo === 'cuento') {
        document.getElementById('lblFAutor').textContent = 'Autor:';
        document.getElementById('lblFEditorial').textContent = 'Editorial:';
        document.getElementById('fEditorial').value = 'Planeta Kids';
        document.getElementById('lblFExtra').textContent = 'Edad recomendada:';
        document.getElementById('fNumeroExtra').value = '7';
        document.getElementById('lblFMateria').textContent = 'Número de páginas:';
        document.getElementById('fMateria').value = '64';
    } else {
        document.getElementById('lblFAutor').textContent = 'Autor:';
        document.getElementById('lblFEditorial').textContent = 'Editorial:';
        document.getElementById('fEditorial').value = 'Alfaguara';
        document.getElementById('lblFExtra').textContent = 'Año / Ref.:';
        document.getElementById('fNumeroExtra').value = '2026';
        document.getElementById('lblFMateria').textContent = 'Categoría / Materia:';
        document.getElementById('fMateria').value = 'Clásicos';
    }
}

async function ejecutarFactoryMethod(e) {
    e.preventDefault();
    try {
        const res = await fetch('/api/publicaciones', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tipo: document.getElementById('fTipo').value,
                titulo: document.getElementById('fTitulo').value,
                autor: document.getElementById('fAutor').value,
                editorial: document.getElementById('fEditorial').value,
                numeroExtra: document.getElementById('fNumeroExtra').value,
                materia: document.getElementById('fMateria').value,
                precio: document.getElementById('fPrecio').value,
                stock: document.getElementById('fStock').value
            })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            cerrarModal('modalFactory');
            document.getElementById('fTitulo').value = '';
            document.getElementById('fAutor').value = '';
            await cargarLibros();
            cargarHemerotecaLsp();
        }
    } catch (err) {
        mostrarToast('Error en Factory Method: ' + err, true);
    }
}

// COMMAND: Undo & Redo
async function cargarEstadoComandos() {
    try {
        const res = await fetch('/api/comandos');
        const d = await res.json();
        const btnU = document.getElementById('btnUndo');
        const btnR = document.getElementById('btnRedo');
        if (btnU) {
            btnU.textContent = `↩️ Undo (${d.undoCount})`;
            btnU.title = d.proximoUndo ? `Deshacer: ${d.proximoUndo}` : 'Sin operaciones para deshacer';
        }
        if (btnR) {
            btnR.textContent = `↪️ Redo (${d.redoCount})`;
            btnR.title = d.proximoRedo ? `Rehacer: ${d.proximoRedo}` : 'Sin operaciones para rehacer';
        }
    } catch (err) {}
}

async function deshacerCommand() {
    try {
        const res = await fetch('/api/undo', { method: 'POST' });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            await cargarLibros();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error al deshacer: ' + err, true);
    }
}

async function rehacerCommand() {
    try {
        const res = await fetch('/api/redo', { method: 'POST' });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        if (data.success) {
            await cargarLibros();
            cargarEstadoComandos();
            cargarMetricasExperto();
            cargarArbolComposite();
        }
    } catch (err) {
        mostrarToast('Error al rehacer: ' + err, true);
    }
}

// MEMENTO: Guardar y Restaurar
function guardarSnapshotMemento() {
    clearTimeout(mementoTimer);
    mementoTimer = setTimeout(async () => {
        const titulo = document.getElementById('txtTitulo').value.trim();
        if (!titulo) return;
        try {
            const res = await fetch('/api/memento', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    autor: document.getElementById('txtAutor').value,
                    titulo,
                    editorial: document.getElementById('txtEditorial').value,
                    materia: document.getElementById('txtMateria').value,
                    precio: document.getElementById('txtPrecio').value || '0',
                    stock: document.getElementById('txtStock').value || '0'
                })
            });
            const data = await res.json();
            if (data.totalSnapshots !== undefined) {
                document.getElementById('btnMemento').textContent = `🕒 Memento (${data.totalSnapshots})`;
            }
        } catch (err) {}
    }, 600);
}

async function cargarConteoMemento() {
    try {
        const res = await fetch('/api/memento?list=1');
        const list = await res.json();
        if (Array.isArray(list)) {
            document.getElementById('btnMemento').textContent = `🕒 Memento (${list.length})`;
        }
    } catch (err) {}
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
            document.getElementById('btnMemento').textContent = `🕒 Memento (${data.restantes})`;
            mostrarToast('Snapshot restaurado con Patrón Memento');
        } else {
            mostrarToast(data.message, true);
        }
    } catch (err) {
        mostrarToast('Error restaurando Memento: ' + err, true);
    }
}

// GRASP Experto en Información (KPIs + alertas de reabastecimiento)
async function cargarMetricasExperto() {
    try {
        const res = await fetch('/api/inventario/metricas');
        const m = await res.json();
        document.getElementById('kpiTitulos').textContent = m.totalTitulos;
        document.getElementById('kpiUnidades').textContent = m.totalUnidades;
        document.getElementById('kpiValor').textContent = `S/ ${Number(m.valorTotalInventario).toFixed(2)}`;
        document.getElementById('kpiBajo').textContent = m.stockBajo;
        document.getElementById('kpiAgotados').textContent = m.agotados;

        const box = document.getElementById('boxAlertasExperto');
        const lista = document.getElementById('listaAlertasExperto');
        if (Array.isArray(m.alertas) && m.alertas.length > 0) {
            box.style.display = 'block';
            lista.innerHTML = m.alertas.slice(0, 12).map(a =>
                `<span class="alert-chip" title="Clic para buscar en tabla" onclick="buscarYEditarPorTitulo('${escapeJs(a.titulo)}')">${escapeHtml(a.titulo)} (${a.stock} unid. • ${a.nivel})</span>`
            ).join('');
        } else {
            box.style.display = 'none';
        }
    } catch (err) {}
}

function buscarYEditarPorTitulo(titulo) {
    const encontrado = todosLosLibros.find(l => l.titulo === titulo);
    if (encontrado) {
        seleccionarFilaParaEdicion(encontrado);
        document.getElementById('txtStock').focus();
        mostrarToast(`Editando "${titulo}" para reponer stock`);
    }
}

// COMPOSITE: Árbol Jerárquico del Catálogo
async function cargarArbolComposite() {
    const cont = document.getElementById('compositeTreeContainer');
    if (!cont) return;
    try {
        const res = await fetch('/api/catalogo/arbol');
        const raiz = await res.json();
        let html = `
            <details open>
                <summary>
                    <span>🏛️ ${escapeHtml(raiz.nombre)}</span>
                    <span class="tree-meta">${raiz.totalItems} libros • ${raiz.stockTotal} unidades • Valor total: S/ ${Number(raiz.valorInventario).toFixed(2)}</span>
                </summary>
        `;
        (raiz.hijos || []).forEach(cat => {
            html += `
                <details>
                    <summary>
                        <span>📁 ${escapeHtml(cat.nombre)}</span>
                        <span class="tree-meta">${cat.totalItems} títulos • ${cat.stockTotal} unid. • S/ ${Number(cat.valorInventario).toFixed(2)}</span>
                    </summary>
                    <ul class="composite-leaf-list">
                        ${(cat.hijos || []).map(item =>
                            `<li>📘 <strong>${escapeHtml(item.nombre)}</strong> — ${item.stock} unid. (S/ ${Number(item.valorInventario).toFixed(2)})</li>`
                        ).join('')}
                    </ul>
                </details>
            `;
        });
        html += '</details>';
        cont.innerHTML = html;
    } catch (err) {
        cont.innerHTML = '<div class="empty-hint">No se pudo cargar el árbol Composite.</div>';
    }
}

// ============================================================================
// 5. PRÉSTAMOS (ISP), MORA (GRASP POLIMORFISMO) Y ALERTAS (OBSERVER)
// ============================================================================
async function cargarVistaPrestamosYAlertas() {
    calcularMoraPolimorfica();
    await Promise.all([cargarTablaPrestamos(), cargarFeedObserver()]);
}

async function calcularMoraPolimorfica() {
    const tipo = document.getElementById('selTipoMora').value;
    const dias = Math.max(0, parseInt(document.getElementById('txtDiasRetraso').value || '0', 10));
    try {
        const res = await fetch(`/api/mora?tipo=${encodeURIComponent(tipo)}&dias=${dias}`);
        const d = await res.json();
        document.getElementById('boxResultadoMora').innerHTML =
            `<strong>${escapeHtml(d.tipo)}</strong> → Plazo máx.: ${d.diasPlazoMaximo} días | Tarifa: S/ ${Number(d.moraPorDia).toFixed(2)}/día | `
            + `Mora por ${d.diasRetraso} día(s) de retraso: <span style="color:#b91c1c;">S/ ${Number(d.moraTotal).toFixed(2)}</span>`;
    } catch (err) {}
}

async function cargarTablaPrestamos() {
    const tbody = document.getElementById('tablaPrestamos');
    try {
        const res = await fetch('/api/prestamos');
        const prestamos = await res.json();
        if (!Array.isArray(prestamos) || prestamos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align:center; padding:20px;">No hay préstamos registrados aún. Haz clic en cualquier libro disponible del Catálogo y solicita un préstamo.</td></tr>';
            return;
        }
        tbody.innerHTML = prestamos.map(p => `
            <tr>
                <td>#${p.idPrestamo}</td>
                <td><strong>${escapeHtml(p.titulo)}</strong></td>
                <td>${escapeHtml(p.tipoRecurso)}</td>
                <td>${escapeHtml(p.cliente)} <small>(${escapeHtml(p.dni)})</small></td>
                <td>${p.fechaPrestamo}</td>
                <td>${p.fechaLimite}</td>
                <td>S/ ${Number(p.moraPorDia).toFixed(2)}</td>
                <td><span style="font-weight:700; color:${p.activo ? '#0369a1' : '#15803d'};">${p.activo ? 'Prestado' : 'Devuelto'}</span></td>
                <td>
                    ${p.activo ? `<button class="tool-btn" onclick="devolverPrestamo(${p.idPrestamo})">↩️ Devolver</button>` : '—'}
                </td>
            </tr>
        `).join('');
    } catch (err) {}
}

async function devolverPrestamo(idPrestamo) {
    const diasStr = prompt('¿Cuántos días de retraso tuvo la devolución? (0 si es a tiempo):', '0');
    if (diasStr === null) return;
    const diasRetraso = Math.max(0, parseInt(diasStr || '0', 10));
    try {
        const res = await fetch('/api/prestamos/devolver', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPrestamo, diasRetraso })
        });
        const data = await res.json();
        mostrarToast(data.message, !data.success);
        await cargarLibros();
        await cargarVistaPrestamosYAlertas();
    } catch (err) {
        mostrarToast('Error devolviendo préstamo: ' + err, true);
    }
}

async function cargarFeedObserver() {
    try {
        const res = await fetch('/api/suscribir');
        const data = await res.json();
        document.getElementById('observerResumen').textContent =
            `Suscriptores activos: ${data.totalSuscriptores} | Notificaciones enviadas: ${(data.notificaciones || []).length}`;
        const feed = document.getElementById('observerNotificaciones');
        if (!data.notificaciones || data.notificaciones.length === 0) {
            const sus = (data.suscripciones || []).length > 0
                ? `<div class="empty-hint">Suscripciones en espera: ${data.suscripciones.map(escapeHtml).join(' • ')}</div>`
                : '<div class="empty-hint">Sin notificaciones aún. Suscríbete en un libro agotado y repón su stock en Gestión CRUD.</div>';
            feed.innerHTML = sus;
            return;
        }
        feed.innerHTML = data.notificaciones.map(n => `<div class="observer-item">${escapeHtml(n)}</div>`).join('');
    } catch (err) {}
}

// ============================================================================
// 6. NAVEGACIÓN Y UTILIDADES
// ============================================================================
function cambiarVista(vista) {
    document.querySelectorAll('.pill-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.content-view').forEach(v => v.classList.remove('active'));

    const mapa = {
        catalogo: ['btnTabCatalogo', 'vista-catalogo'],
        inventario: ['btnTabInventario', 'vista-inventario'],
        prestamos: ['btnTabPrestamos', 'vista-prestamos']
    };
    const destino = mapa[vista] || mapa.catalogo;
    document.getElementById(destino[0]).classList.add('active');
    document.getElementById(destino[1]).classList.add('active');

    if (vista === 'catalogo') {
        cargarLibros();
        cargarHemerotecaLsp();
    } else if (vista === 'inventario') {
        cargarLibros();
        cargarEstadoComandos();
        cargarConteoMemento();
        cargarMetricasExperto();
        cargarArbolComposite();
    } else if (vista === 'prestamos') {
        cargarVistaPrestamosYAlertas();
    }
}

function mostrarToast(mensaje, error = false) {
    const c = document.getElementById('toastContainer');
    const t = document.createElement('div');
    t.className = 'toast-pill';
    if (error) t.style.background = '#991b1b';
    t.innerHTML = `<span>${error ? '⚠️' : '✅'}</span> <span>${escapeHtml(mensaje)}</span>`;
    c.appendChild(t);
    setTimeout(() => {
        t.style.opacity = '0';
        setTimeout(() => t.remove(), 250);
    }, 3400);
}

function escapeHtml(s) {
    return String(s ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function escapeJs(s) {
    return String(s ?? '').replace(/\\/g, '\\\\').replace(/'/g, "\\'");
}
