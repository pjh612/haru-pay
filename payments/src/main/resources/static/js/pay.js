$(document).ready(function () {
    $('#submitPay').on("click", function () {
        requestPay();
    });
})

const requestPay = () => {
    $.ajax({
        type: "post",
        url: "/api/payment/request",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "paymentRequestId": paymentId,
        }),
        success: function (data) {
            window.opener.postMessage(data, "*");
        },
        error: function (e) {
            window.opener.postMessage({
                status: "FAILED",
                errorCode: e.status || "PAYMENT_REQUEST_FAILED",
                message: e.responseJSON?.message || e.statusText || "결제 요청에 실패했습니다.",
                orderId: orderId,
                paymentId: paymentId,
            }, "*");
        }
    });
}
