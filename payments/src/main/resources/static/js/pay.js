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
            alert("결제에 실패했습니다.");
        }
    });
}
