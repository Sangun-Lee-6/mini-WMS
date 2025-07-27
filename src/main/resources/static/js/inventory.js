async function sendJsonForm(formId, url) {
    const form = document.getElementById(formId);
    const data = new FormData(form);
    const json = Object.fromEntries(data.entries());
    json.quantity = parseInt(json.quantity);  // 문자열 → 숫자

    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(json),
    });

    const text = await res.text();
    alert(text);
    window.location.reload(); // 새로고침으로 재고 반영
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('receiveForm').addEventListener('submit', function (e) {
        e.preventDefault();
        sendJsonForm('receiveForm', '/api/inventory/receive');
    });

    document.getElementById('releaseForm').addEventListener('submit', function (e) {
        e.preventDefault();
        sendJsonForm('releaseForm', '/api/inventory/release');
    });
});
