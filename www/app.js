// FaiCalculer Core JavaScript Module
const STORAGE_KEY_CALCULOS = 'faicalculer_records';
const STORAGE_KEY_PRESTAMOS = 'faicalculer_prestamos';

let records = [];
let prestamos = [];
let activeTopTab = 'CALCULO';
let activeSubTab = 'REGISTRO';
let materialCount = 0;
let currentEditingId = null;

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    loadData();
    calculateQuickGold();
    calculateQuickDollar();
});

function loadData() {
    try {
        const rawCalculos = localStorage.getItem(STORAGE_KEY_CALCULOS);
        records = rawCalculos ? JSON.parse(rawCalculos) : getDemoCalculos();
        
        const rawPrestamos = localStorage.getItem(STORAGE_KEY_PRESTAMOS);
        prestamos = rawPrestamos ? JSON.parse(rawPrestamos) : getDemoPrestamos();
    } catch (e) {
        records = [];
        prestamos = [];
    }
    renderRegistros();
    renderPromedios();
    renderAcumulado();
    renderPrestamos();
}

function saveData() {
    localStorage.setItem(STORAGE_KEY_CALCULOS, JSON.stringify(records));
    localStorage.setItem(STORAGE_KEY_PRESTAMOS, JSON.stringify(prestamos));
}

// Navigation Handlers
function switchTopTab(tab) {
    activeTopTab = tab;
    document.querySelectorAll('.top-nav .nav-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(`btn-top-${tab.toLowerCase()}`).classList.add('active');

    const subtabsBar = document.getElementById('subtabs-bar');
    document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));

    if (tab === 'CALCULO') {
        subtabsBar.style.display = 'flex';
        switchSubTab(activeSubTab);
    } else {
        subtabsBar.style.display = 'none';
        document.getElementById(`view-${tab.toLowerCase()}`).classList.add('active');
    }
}

function switchSubTab(subtab) {
    activeSubTab = subtab;
    document.querySelectorAll('.subtab-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(`subtab-${subtab.toLowerCase()}`).classList.add('active');

    document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));
    document.getElementById(`view-${subtab.toLowerCase()}`).classList.add('active');

    if (subtab === 'REGISTRO') renderRegistros();
    if (subtab === 'PROMEDIO') renderPromedios();
    if (subtab === 'ACUMULADO') renderAcumulado();
    if (subtab === 'PRESTAMOS') renderPrestamos();
}

// Math calculation core (Matches Android MainActivity.java)
function computeMaterialValues(onza, leyMat, pct, tc, pesoSin, pesoFun) {
    onza = parseFloat(onza) || 0;
    leyMat = parseFloat(leyMat) || 0;
    pct = parseFloat(pct) || 0;
    tc = parseFloat(tc) || 0;
    pesoSin = parseFloat(pesoSin) || 0;
    pesoFun = parseFloat(pesoFun) || 0;

    const merma = (pesoSin > 0 && pesoFun > 0) ? Math.max(0, pesoSin - pesoFun) : 0;
    const pesoMat = pesoFun > 0 ? pesoFun : pesoSin;

    const precioUsdPreciso = (onza / 31.1034768) * (leyMat / 100.0) * (1.0 - (pct / 100.0));
    const precioUsd = Math.floor(precioUsdPreciso * 100.0) / 100.0;

    const precioSolesPreciso = precioUsdPreciso * tc;
    const precioSoles = Math.floor(precioSolesPreciso * 100.0) / 100.0;

    const precioTotPreciso = pesoMat * precioSoles;
    const precioTot = Math.floor(precioTotPreciso * 100.0) / 100.0;

    return {
        leyMat,
        pesoSin,
        pesoFun,
        merma,
        pesoMat,
        precioUsd,
        precioSoles,
        precioTot
    };
}

// Form Modal Handlers
function openFormModal(editRecord = null) {
    const modal = document.getElementById('modal-form');
    const container = document.getElementById('container-materials');
    container.innerHTML = '';
    materialCount = 0;

    if (editRecord) {
        currentEditingId = editRecord.id;
        document.getElementById('modal-form-title').innerText = 'Editar Cálculo';
        document.getElementById('form-cliente').value = editRecord.cliente || 'X';
        document.getElementById('form-onza').value = editRecord.onza || '';
        document.getElementById('form-ley-gen').value = editRecord.ley || '';
        document.getElementById('form-porcentaje').value = editRecord.porcentaje || '';
        document.getElementById('form-tc').value = editRecord.tc || '';
        document.getElementById('form-desc-motivo').value = editRecord.descuentoMotivo || '';
        document.getElementById('form-desc-monto').value = editRecord.descuentoMonto || '';

        if (editRecord.materiales && editRecord.materiales.length > 0) {
            editRecord.materiales.forEach(m => addMaterialCard(m));
        } else {
            addMaterialCard({
                ley: editRecord.ley,
                pesoSin: editRecord.pesoSinFundir,
                pesoFun: editRecord.pesoFundido
            });
        }
    } else {
        currentEditingId = null;
        document.getElementById('modal-form-title').innerText = 'Nuevo Cálculo de Oro';
        document.getElementById('form-cliente').value = 'X';
        document.getElementById('form-onza').value = '2650.00';
        document.getElementById('form-ley-gen').value = '90.0';
        document.getElementById('form-porcentaje').value = '1.5';
        document.getElementById('form-tc').value = '3.78';
        document.getElementById('form-desc-motivo').value = '';
        document.getElementById('form-desc-monto').value = '';

        addMaterialCard();
    }

    attachFormListeners();
    updateFormTotals();
    modal.classList.add('active');
}

function closeFormModal() {
    document.getElementById('modal-form').classList.remove('active');
}

function addMaterialCard(initialData = null) {
    materialCount++;
    const container = document.getElementById('container-materials');
    const leyGen = document.getElementById('form-ley-gen').value;

    const card = document.createElement('div');
    card.className = 'material-card-item';
    card.id = `mat-card-${materialCount}`;

    card.innerHTML = `
        <div class="material-card-top">
            <span class="material-card-title">Material #${materialCount}</span>
            <button class="btn-remove-mat" onclick="removeMaterialCard('${card.id}')">✕</button>
        </div>
        <div class="form-grid">
            <div class="form-group">
                <label>Ley del Material (%)</label>
                <input type="number" class="mat-ley" placeholder="${leyGen || '90.0'}" step="0.1" value="${initialData?.ley || ''}">
            </div>
            <div class="form-group">
                <label>Merma (Calculada)</label>
                <input type="text" class="mat-merma" value="0 g" readonly style="opacity: 0.7;">
            </div>
            <div class="form-group">
                <label>Peso Sin Fundir (g)</label>
                <input type="number" class="mat-peso-sin" placeholder="0.00" step="0.01" value="${initialData?.pesoSin || ''}">
            </div>
            <div class="form-group">
                <label>Peso Fundido (g)</label>
                <input type="number" class="mat-peso-fun" placeholder="0.00" step="0.01" value="${initialData?.pesoFun || ''}">
            </div>
        </div>
        <div style="text-align: right; font-size: 13px; font-weight: 700; color: #06D6A0;" class="mat-total-preview">
            Subtotal: S/. 0.00
        </div>
    `;

    container.appendChild(card);
    attachCardListeners(card);
    updateFormTotals();
}

function removeMaterialCard(cardId) {
    const container = document.getElementById('container-materials');
    if (container.children.length <= 1) {
        alert('Debe ingresar al menos 1 material');
        return;
    }
    const card = document.getElementById(cardId);
    if (card) card.remove();
    updateMaterialTitles();
    updateFormTotals();
}

function updateMaterialTitles() {
    const cards = document.querySelectorAll('.material-card-item');
    cards.forEach((card, idx) => {
        const title = card.querySelector('.material-card-title');
        if (title) title.innerText = `Material #${idx + 1}`;
    });
}

function attachFormListeners() {
    ['form-onza', 'form-ley-gen', 'form-porcentaje', 'form-tc', 'form-desc-monto'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.oninput = updateFormTotals;
    });
}

function attachCardListeners(card) {
    card.querySelectorAll('input').forEach(input => {
        input.oninput = updateFormTotals;
    });
}

function updateFormTotals() {
    const onza = parseFloat(document.getElementById('form-onza').value) || 0;
    const leyGen = parseFloat(document.getElementById('form-ley-gen').value) || 0;
    const pct = parseFloat(document.getElementById('form-porcentaje').value) || 0;
    const tc = parseFloat(document.getElementById('form-tc').value) || 0;
    const descMonto = parseFloat(document.getElementById('form-desc-monto').value) || 0;

    const basePrecisoUSD = (onza / 31.1034768) * (leyGen / 100.0) * (1.0 - (pct / 100.0));
    const baseUSD = Math.floor(basePrecisoUSD * 100.0) / 100.0;
    const baseSoles = Math.floor((basePrecisoUSD * tc) * 100.0) / 100.0;

    document.getElementById('preview-usd-g').innerText = `$${baseUSD.toFixed(2)}`;
    document.getElementById('preview-soles-g').innerText = `S/. ${baseSoles.toFixed(2)}`;

    let sumSubtotales = 0;
    const cards = document.querySelectorAll('.material-card-item');

    cards.forEach(card => {
        const leyMatVal = card.querySelector('.mat-ley').value;
        const leyMat = leyMatVal !== '' ? parseFloat(leyMatVal) : leyGen;
        const pesoSin = parseFloat(card.querySelector('.mat-peso-sin').value) || 0;
        const pesoFun = parseFloat(card.querySelector('.mat-peso-fun').value) || 0;

        const res = computeMaterialValues(onza, leyMat, pct, tc, pesoSin, pesoFun);

        card.querySelector('.mat-merma').value = `${res.merma.toFixed(2)} g`;
        card.querySelector('.mat-total-preview').innerText = `Subtotal: S/. ${res.precioTot.toFixed(2)}`;

        sumSubtotales += res.precioTot;
    });

    const pagoTotalFinal = Math.max(0, sumSubtotales - descMonto);
    document.getElementById('form-pago-total').innerText = `S/. ${pagoTotalFinal.toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function saveFormCalculo() {
    const cliente = document.getElementById('form-cliente').value.trim() || 'X';
    const onza = parseFloat(document.getElementById('form-onza').value) || 0;
    const leyGen = parseFloat(document.getElementById('form-ley-gen').value) || 0;
    const pct = parseFloat(document.getElementById('form-porcentaje').value) || 0;
    const tc = parseFloat(document.getElementById('form-tc').value) || 0;
    const descMot = document.getElementById('form-desc-motivo').value.trim();
    const descMonto = parseFloat(document.getElementById('form-desc-monto').value) || 0;

    const cards = document.querySelectorAll('.material-card-item');
    const materiales = [];
    let sumSubtotales = 0;
    let sumPeso = 0;
    let sumMerma = 0;

    cards.forEach(card => {
        const leyMatVal = card.querySelector('.mat-ley').value;
        const leyMat = leyMatVal !== '' ? parseFloat(leyMatVal) : leyGen;
        const pesoSin = parseFloat(card.querySelector('.mat-peso-sin').value) || 0;
        const pesoFun = parseFloat(card.querySelector('.mat-peso-fun').value) || 0;

        const res = computeMaterialValues(onza, leyMat, pct, tc, pesoSin, pesoFun);
        materiales.push(res);

        sumSubtotales += res.precioTot;
        sumPeso += res.pesoMat;
        sumMerma += res.merma;
    });

    const pagoTotal = Math.max(0, sumSubtotales - descMonto);
    const dateStr = new Date().toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric' });

    const newRecord = {
        id: currentEditingId || Date.now().toString(),
        cliente,
        fecha: dateStr,
        onza,
        ley: leyGen,
        porcentaje: pct,
        tc,
        descuentoMotivo: descMot,
        descuentoMonto: descMonto,
        pagoTotal,
        pesoTotal: sumPeso,
        mermaTotal: sumMerma,
        materiales
    };

    if (currentEditingId) {
        const idx = records.findIndex(r => r.id === currentEditingId);
        if (idx !== -1) records[idx] = newRecord;
    } else {
        records.unshift(newRecord);
    }

    saveData();
    closeFormModal();
    renderRegistros();
    renderAcumulado();
    renderPromedios();
}

function deleteRecord(id) {
    if (confirm('¿Eliminar este registro de cálculo?')) {
        records = records.filter(r => r.id !== id);
        saveData();
        renderRegistros();
        renderAcumulado();
        renderPromedios();
    }
}

// Render Functions
function renderRegistros() {
    const container = document.getElementById('list-registros');
    const filter = (document.getElementById('search-registro')?.value || '').toLowerCase();
    container.innerHTML = '';

    const filtered = records.filter(r => r.cliente.toLowerCase().includes(filter));

    if (filtered.length === 0) {
        container.innerHTML = '<div style="text-align:center; padding: 40px; color:#94A3B8;">No hay registros de cálculos</div>';
        return;
    }

    filtered.forEach(r => {
        const card = document.createElement('div');
        card.className = 'record-card';
        card.innerHTML = `
            <div class="card-top">
                <span class="client-name">${r.cliente}</span>
                <span class="record-date">${r.fecha}</span>
            </div>
            <div class="card-metrics">
                <div class="metric-item"><span>Onza:</span><strong>$${r.onza}</strong></div>
                <div class="metric-item"><span>Ley:</span><strong>${r.ley}%</strong></div>
                <div class="metric-item"><span>Peso Total:</span><strong>${(r.pesoTotal||0).toFixed(2)} g</strong></div>
                <div class="metric-item"><span>Materiales:</span><strong>${r.materiales ? r.materiales.length : 1} Lote(s)</strong></div>
            </div>
            <div class="card-bottom">
                <span class="total-pay">S/. ${(r.pagoTotal||0).toLocaleString('es-PE', {minimumFractionDigits:2})}</span>
                <div class="card-actions">
                    <button class="action-btn" onclick="openTicketModal('${r.id}')">🧾 Recibo</button>
                    <button class="action-btn" onclick='openFormModal(${JSON.stringify(r)})'>✏️ Edit</button>
                    <button class="action-btn" onclick="deleteRecord('${r.id}')">🗑️</button>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderPromedios() {
    const container = document.getElementById('container-promedios');
    const filter = (document.getElementById('search-promedio-cliente')?.value || '').toLowerCase();
    container.innerHTML = '';

    const groups = {};
    records.forEach(r => {
        const name = r.cliente.toUpperCase();
        if (!groups[name]) groups[name] = [];
        groups[name].push(r);
    });

    const clients = Object.keys(groups).filter(c => c.toLowerCase().includes(filter));

    if (clients.length === 0) {
        container.innerHTML = '<div style="text-align:center; padding: 40px; color:#94A3B8;">No hay promedios guardados</div>';
        return;
    }

    clients.forEach(c => {
        const list = groups[c];
        const count = list.length;
        const totalPago = list.reduce((acc, curr) => acc + (curr.pagoTotal || 0), 0);
        const totalPeso = list.reduce((acc, curr) => acc + (curr.pesoTotal || 0), 0);
        const avgPago = totalPago / count;
        const avgLey = list.reduce((acc, curr) => acc + (curr.ley || 0), 0) / count;

        const card = document.createElement('div');
        card.className = 'record-card';
        card.innerHTML = `
            <div class="card-top">
                <span class="client-name">👤 ${c}</span>
                <span class="record-date">${count} Registro(s)</span>
            </div>
            <div class="card-metrics">
                <div class="metric-item"><span>Prom. Ley:</span><strong>${avgLey.toFixed(1)}%</strong></div>
                <div class="metric-item"><span>Peso Total:</span><strong>${totalPeso.toFixed(2)} g</strong></div>
                <div class="metric-item"><span>Total Pagado:</span><strong>S/. ${totalPago.toFixed(2)}</strong></div>
                <div class="metric-item"><span>Prom. Pago:</span><strong>S/. ${avgPago.toFixed(2)}</strong></div>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderAcumulado() {
    const totalPago = records.reduce((acc, r) => acc + (r.pagoTotal || 0), 0);
    const totalPeso = records.reduce((acc, r) => acc + (r.pesoTotal || 0), 0);
    const totalMerma = records.reduce((acc, r) => acc + (r.mermaTotal || 0), 0);

    document.getElementById('tv-acumulado-total').innerText = `S/. ${totalPago.toLocaleString('es-PE', {minimumFractionDigits:2})}`;
    document.getElementById('tv-acumulado-peso').innerText = `${totalPeso.toFixed(2)} g`;
    document.getElementById('tv-acumulado-merma').innerText = `${totalMerma.toFixed(2)} g`;
}

// Prestamos
function openPrestamoModal() {
    document.getElementById('modal-prestamo').classList.add('active');
}
function closePrestamoModal() {
    document.getElementById('modal-prestamo').classList.remove('active');
}
function savePrestamo() {
    const cliente = document.getElementById('p-cliente').value.trim() || 'X';
    const monto = parseFloat(document.getElementById('p-monto').value) || 0;
    const detalle = document.getElementById('p-detalle').value.trim();

    if (monto <= 0) {
        alert('Ingrese un monto válido');
        return;
    }

    prestamos.unshift({
        id: Date.now().toString(),
        cliente,
        monto,
        detalle,
        fecha: new Date().toLocaleDateString('es-PE')
    });

    saveData();
    closePrestamoModal();
    renderPrestamos();
}

function renderPrestamos() {
    const container = document.getElementById('list-prestamos');
    const filter = (document.getElementById('search-prestamo')?.value || '').toLowerCase();
    container.innerHTML = '';

    const filtered = prestamos.filter(p => p.cliente.toLowerCase().includes(filter));

    if (filtered.length === 0) {
        container.innerHTML = '<div style="text-align:center; padding: 40px; color:#94A3B8;">No hay préstamos registrados</div>';
        return;
    }

    filtered.forEach(p => {
        const card = document.createElement('div');
        card.className = 'record-card';
        card.innerHTML = `
            <div class="card-top">
                <span class="client-name">🤝 ${p.cliente}</span>
                <span class="record-date">${p.fecha}</span>
            </div>
            <div class="card-metrics" style="grid-template-columns: 1fr;">
                <div class="metric-item"><span>Detalle:</span><strong>${p.detalle || 'Sin observaciones'}</strong></div>
            </div>
            <div class="card-bottom">
                <span class="total-pay" style="color: #FFB703;">S/. ${p.monto.toFixed(2)}</span>
                <button class="action-btn" onclick="deletePrestamo('${p.id}')">🗑️</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function deletePrestamo(id) {
    if (confirm('¿Eliminar registro de préstamo?')) {
        prestamos = prestamos.filter(p => p.id !== id);
        saveData();
        renderPrestamos();
    }
}

// Quick Calculators
function calculateQuickGold() {
    const onza = parseFloat(document.getElementById('quick-onza').value) || 0;
    const ley = parseFloat(document.getElementById('quick-ley').value) || 0;
    const desc = parseFloat(document.getElementById('quick-desc').value) || 0;
    const tc = parseFloat(document.getElementById('quick-tc').value) || 0;

    const usdPreciso = (onza / 31.1034768) * (ley / 100.0) * (1.0 - (desc / 100.0));
    const usd = Math.floor(usdPreciso * 100.0) / 100.0;
    const soles = Math.floor((usdPreciso * tc) * 100.0) / 100.0;

    document.getElementById('res-gold-usd').innerText = `$${usd.toFixed(2)}`;
    document.getElementById('res-gold-soles').innerText = `S/. ${soles.toFixed(2)}`;
}

function calculateQuickDollar() {
    const monto = parseFloat(document.getElementById('calc-usd-monto').value) || 0;
    const tc = parseFloat(document.getElementById('calc-tc-val').value) || 0;

    const soles = monto * tc;
    document.getElementById('res-dolar-soles').innerText = `S/. ${soles.toFixed(2)}`;
}

// Ticket Generation
function openTicketModal(id) {
    const r = records.find(rec => rec.id === id);
    if (!r) return;

    const paper = document.getElementById('ticket-content');
    paper.innerHTML = `
        <h2>*** FAICALCULER ***</h2>
        <p style="text-align:center;">RECIBO DE LIQUIDACIÓN DE ORO</p>
        <div class="ticket-divider"></div>
        <p><strong>FECHA:</strong> ${r.fecha}</p>
        <p><strong>CLIENTE:</strong> ${r.cliente}</p>
        <p><strong>ONZA:</strong> $${r.onza} | <strong>TC:</strong> ${r.tc}</p>
        <p><strong>LEY GEN:</strong> ${r.ley}% | <strong>DESC:</strong> ${r.porcentaje}%</p>
        <div class="ticket-divider"></div>
        <p style="font-weight:bold;">DETALLE MATERIALES:</p>
        ${(r.materiales || []).map((m, idx) => `
            <p>#${idx+1} Ley:${m.leyMat}% Peso:${m.pesoMat.toFixed(2)}g Merma:${m.merma.toFixed(2)}g</p>
            <p style="text-align:right;">Subtotal: S/. ${m.precioTot.toFixed(2)}</p>
        `).join('')}
        <div class="ticket-divider"></div>
        ${r.descuentoMonto > 0 ? `<p><strong>DESCUENTO (${r.descuentoMotivo||'Desc.'}):</strong> -S/. ${r.descuentoMonto.toFixed(2)}</p>` : ''}
        <p style="font-size: 14px; font-weight: bold; text-align: center; margin-top: 6px;">PAGO TOTAL: S/. ${r.pagoTotal.toFixed(2)}</p>
        <div class="ticket-divider"></div>
        <p style="text-align:center; font-size: 10px;">¡Gracias por su preferencia!</p>
    `;

    document.getElementById('modal-ticket').classList.add('active');
}

function closeTicketModal() {
    document.getElementById('modal-ticket').classList.remove('active');
}

function printTicket() {
    window.print();
}

// Demo Data
function getDemoCalculos() {
    return [
        {
            id: '1',
            cliente: 'JUAN PEREZ',
            fecha: new Date().toLocaleDateString('es-PE'),
            onza: 2650,
            ley: 90,
            porcentaje: 1.5,
            tc: 3.78,
            descuentoMotivo: '',
            descuentoMonto: 0,
            pagoTotal: 9245.50,
            pesoTotal: 34.50,
            mermaTotal: 0.80,
            materiales: [
                { leyMat: 90, pesoSin: 35.30, pesoFun: 34.50, merma: 0.80, pesoMat: 34.50, precioUsd: 70.82, precioSoles: 267.69, precioTot: 9245.50 }
            ]
        }
    ];
}

function getDemoPrestamos() {
    return [
        { id: '101', cliente: 'MARIA LOPEZ', monto: 500, detalle: 'Adelanto de material', fecha: new Date().toLocaleDateString('es-PE') }
    ];
}
