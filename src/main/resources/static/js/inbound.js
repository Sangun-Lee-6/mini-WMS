document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("inboundButton").addEventListener("click", createInbound);
    fetchInbounds();
});

function getRandomSupplier() {
    const suppliers = ['신세계푸드', '삼성물산', 'CJ대한통운', '롯데상사', '농심'];
    const randomIndex = Math.floor(Math.random() * suppliers.length);
    return suppliers[randomIndex];
}

function getRandomItems() {
    const availableProductIds = [1, 2, 3, 4, 5];
    const itemCount = Math.floor(Math.random() * 3) + 1; // 1~3개 품목
    const shuffled = [...availableProductIds].sort(() => 0.5 - Math.random());
    const selected = shuffled.slice(0, itemCount);

    return selected.map(productId => ({
        productId,
        quantity: Math.floor(Math.random() * 10) + 1
    }));
}

async function createInbound() {
    const requestBody = {
        warehouseId: 1,
        supplier: getRandomSupplier(),
        items: getRandomItems()
    };

    const res = await fetch('/api/inbound', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    });

    if (res.ok) {
        alert('✅ 입고 등록 완료');
        fetchInbounds();
    } else {
        const errorText = await res.text();
        alert('❌ 입고 등록 실패: ' + errorText);
    }
}

async function fetchInbounds() {
    const res = await fetch('/api/inbound');
    const data = await res.json();
    const listEl = document.getElementById('inboundList');
    listEl.innerHTML = '';

    data.forEach(i => {
        const li = document.createElement('li');
        li.innerHTML = `
            <strong>ID:</strong> ${i.id} |
            <strong>공급자:</strong> ${i.supplier} |
            <strong>입고일시:</strong> ${formatDate(i.inboundDate)} |
            <strong>아이템 수:</strong> ${i.itemCount} |
            <strong>창고:</strong> ${i.warehouseName} (${i.warehouseLocation})
        `;
        li.onclick = () => fetchDetail(i.id);
        listEl.appendChild(li);
    });
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

async function fetchDetail(id) {
    const res = await fetch(`/api/inbound/${id}`);
    const data = await res.json();

    const detail = `
        📦 <strong>입고 ID:</strong> ${data.id}<br>
        🏭 <strong>창고:</strong> ${data.warehouse.name} (${data.warehouse.location})<br>
        🚚 <strong>공급자:</strong> ${data.supplier}<br>
        📅 <strong>입고일:</strong> ${formatDate(data.inboundDate)}<br><br>
        📋 <strong>입고 품목 목록:</strong>
        <ul>
          ${data.items.map(item => `
            <li>
              🔹 <strong>${item.productName}</strong> (${item.category}) - 수량: ${item.quantity}
            </li>
          `).join('')}
        </ul>
    `;

    alert(detail);
}

