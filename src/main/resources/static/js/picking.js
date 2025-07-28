// static/js/picking.js

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("generateOrderBtn").addEventListener("click", createRandomOrder);
    document.getElementById("runPickingV1Btn").addEventListener("click", runPickingOptimization);
    fetchWaitingOrders();
});

let currentOrderId = null;

async function createRandomOrder() {
    const res = await fetch('/api/orders/random', { method: 'POST' });
    if (!res.ok) {
        alert('❌ 주문 생성 실패');
        return;
    }
    const data = await res.json();
    currentOrderId = data.orderId;
    document.getElementById("runPickingV1Btn").disabled = false;
    fetchWaitingOrders(); // 리스트 갱신
}

async function fetchWaitingOrders() {
    const res = await fetch('/api/orders/waiting');
    const data = await res.json();

    const listEl = document.getElementById('orderList');
    listEl.innerHTML = '';

    if (data.length === 0) {
        listEl.innerHTML = '<li class="order-card">현재 피킹 대기 중인 주문이 없습니다.</li>';
        return;
    }

    data.forEach(order => {
        const li = document.createElement('li');
        li.className = 'order-card';

        li.innerHTML = `
            <div><strong>🧾 주문번호:</strong> ${order.orderId}</div>
            <div><strong>📅 생성일시:</strong> ${formatDate(order.createdAt)}</div>
            <div><strong>📦 품목:</strong> ${order.items.map(i => `${i.productName}(${i.quantity})`).join(', ')}</div>
        `;
        listEl.appendChild(li);
    });
}

async function runPickingOptimization() {
    if (!currentOrderId) return;
    const res = await fetch(`/api/picking/v1?orderId=${currentOrderId}`);
    const data = await res.json();
    document.getElementById("pickingResult").innerText = `✅ 총 소요 시간: ${data.totalTime}초`;
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
