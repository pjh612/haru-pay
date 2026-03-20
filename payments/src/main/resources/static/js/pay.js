$(document).ready(function () {
    $('#submitPay').on("click", function () {
        requestPay();
    });
})

const extractProblemType = (responseJson) => {
    const defaultType = "internal-error";
    const type = responseJson?.errorType || responseJson?.type;
    if (!type) {
        return defaultType;
    }

    if (typeof type !== 'string') {
        return defaultType;
    }

    if (type.includes('/')) {
        const segments = type.split('/').filter(Boolean);
        return segments.length > 0 ? segments[segments.length - 1] : defaultType;
    }

    return type;
}

const extractProblemMessage = (jqXHR) => {
    if (jqXHR?.responseJSON?.detail) {
        return jqXHR.responseJSON.detail;
    }
    if (jqXHR?.responseJSON?.message) {
        return jqXHR.responseJSON.message;
    }
    return jqXHR?.statusText || "결제 요청에 실패했습니다.";
}

const requestPay = () => {
    $('#submitPay').prop('disabled', true);
    $.ajax({
        type: "post",
        url: "/api/payment/request",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "paymentRequestId": paymentId,
        }),
        success: function (data) {
            const params = new URLSearchParams({
                paymentId: paymentId,
                orderId: data.orderId || orderId,
                requestPrice: data.requestPrice,
                paymentStatus: data.paymentStatus,
                approvedAt: data.approvedAt || '',
                successUrl: successUrl,
                failureUrl: failureUrl,
            });
            window.location.href = '/pay/' + paymentId + '/payment-result?' + params.toString();
        },
        error: function (e) {
            $('#submitPay').prop('disabled', false);
            const errorCode = extractProblemType(e.responseJSON);
            const message = extractProblemMessage(e);

            if (failureUrl) {
                const dest = new URL(failureUrl, window.location.origin);
                dest.searchParams.set('errorCode', String(errorCode));
                dest.searchParams.set('message', message);
                if (orderId) {
                    dest.searchParams.set('orderId', orderId);
                }
                if (paymentId) {
                    dest.searchParams.set('paymentId', paymentId);
                }
                window.location.href = dest.toString();
                return;
            }

            alert(message);
        }
    });
}
