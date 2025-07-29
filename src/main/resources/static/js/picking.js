//// static/js/picking.js
//
//document.addEventListener("DOMContentLoaded", () => {
//    document.getElementById("generateOrderBtn").addEventListener("click", createRandomOrder);
//    document.getElementById("runPickingV1Btn").addEventListener("click", runPickingOptimization);
//    fetchWaitingOrders();
//});
//
//let currentOrderId = null;
//
//async function createRandomOrder() {
//    const res = await fetch('/api/orders/random', { method: 'POST' });
//    if (!res.ok) {
//        alert('❌ 주문 생성 실패');
//        return;
//    }
//    const data = await res.json();
//    currentOrderId = data.orderId;
//    document.getElementById("runPickingV1Btn").disabled = false;
//    fetchWaitingOrders(); // 리스트 갱신
//}
//
//async function fetchWaitingOrders() {
//    const res = await fetch('/api/orders/waiting');
//    const data = await res.json();
//
//    const listEl = document.getElementById('orderList');
//    listEl.innerHTML = '';
//
//    if (data.length === 0) {
//        listEl.innerHTML = '<li class="order-card">현재 피킹 대기 중인 주문이 없습니다.</li>';
//        return;
//    }
//
//    data.forEach(order => {
//        const li = document.createElement('li');
//        li.className = 'order-card';
//
//        li.innerHTML = `
//            <div><strong>🧾 주문번호:</strong> ${order.orderId}</div>
//            <div><strong>📅 생성일시:</strong> ${formatDate(order.createdAt)}</div>
//            <div><strong>📦 품목:</strong> ${order.items.map(i => `${i.productName}(${i.quantity})`).join(', ')}</div>
//        `;
//        listEl.appendChild(li);
//    });
//}
//
//function formatDate(dateStr) {
//    const date = new Date(dateStr);
//    return date.toLocaleString('ko-KR', {
//        year: 'numeric',
//        month: '2-digit',
//        day: '2-digit',
//        hour: '2-digit',
//        minute: '2-digit'
//    });
//}
//async function runPickingOptimization() {
//    const body = { algorithm: "greedy" };
//
//    const res = await fetch("/api/picking", {
//        method: "POST",
//        headers: {
//            "Content-Type": "application/json"
//        },
//        body: JSON.stringify(body)
//    });
//
//    if (!res.ok) {
//        alert("❌ 피킹 최적화 실패");
//        return;
//    }
//
//    const data = await res.json();
//
//    const resultBox = document.getElementById("pickingResult");
//    resultBox.innerHTML = `
//        <p><strong>✅ 알고리즘:</strong> ${data.algorithm}</p>
//        <p><strong>📦 총 품목 수:</strong> ${data.totalItems}</p>
//        <p><strong>📏 총 이동 거리:</strong> ${data.totalDistance}</p>
//        <p><strong>⏱️ 총 소요 시간:</strong> ${data.totalTime}초</p>
//        <table>
//            <thead>
//                <tr>
//                    <th>상품명</th>
//                    <th>수량</th>
//                    <th>위치</th>
//                </tr>
//            </thead>
//            <tbody>
//                ${data.items.map(item => `
//                    <tr>
//                        <td>${item.productName}</td>
//                        <td>${item.quantity}</td>
//                        <td>${item.locationCode}</td>
//                    </tr>
//                `).join('')}
//            </tbody>
//        </table>
//    `;
//}
//


// static/js/picking.js

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("generateOrderBtn").addEventListener("click", createRandomOrder);
    document.getElementById("compareSimulateBtn").addEventListener("click", runSimulationCompare);
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
    document.getElementById("compareSimulateBtn").disabled = false;
    fetchWaitingOrders(); // 리스트 갱신
}

async function fetchWaitingOrders() {
    const res = await fetch('/api/orders/waiting');
    const data = await res.json();

    const listEl = document.getElementById('orderList');
    listEl.innerHTML = '';

    if (data.length === 0) {
        listEl.innerHTML = '<li class="order-card">현재 피킹 대기 중인 주문이 없습니다.</li>';
        document.getElementById("compareSimulateBtn").disabled = true;
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

async function runSimulationCompare() {
    const algorithms = ["fifo", "greedy", "tps"];
    const results = [];

    for (let algo of algorithms) {
        const res = await fetch("/api/picking/simulate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ algorithm: algo })
        });

        if (res.ok) {
            results.push(await res.json());
        }
    }

    const resultBox = document.getElementById("pickingResult");
    if (results.length === 0) {
        resultBox.innerHTML = "<p>❌ 시뮬레이션 결과 없음</p>";
        return;
    }

    resultBox.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>알고리즘</th>
                    <th>총 품목 수</th>
                    <th>총 이동 거리</th>
                    <th>총 소요 시간(초)</th>
                </tr>
            </thead>
            <tbody>
                ${results.map(r => `
                    <tr>
                        <td>${r.algorithm}</td>
                        <td>${r.totalItems}</td>
                        <td>${r.totalDistance}</td>
                        <td>${r.totalTime}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}
