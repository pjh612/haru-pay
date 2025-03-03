$(document).ready(function () {
    subscribe();
})


const subscribe = () => {
    const alarm = new EventSource(`/api/payment-result/subscribe?paymentId=` + paymentId);
    alarm.addEventListener('connect', e => {
    })

    alarm.onerror = (e) => {
    }

    alarm.onmessage = (e) => {
        window.opener.postMessage(e.data, "*");
    }
}