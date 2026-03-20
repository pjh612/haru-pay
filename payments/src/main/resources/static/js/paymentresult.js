$(document).ready(function () {
    const params = new URLSearchParams(window.location.search);
    const resolvedPaymentId = params.get('paymentId') || paymentId;
    const successUrl = params.get('successUrl') || '';
    const failureUrl = params.get('failureUrl') || '';

    if (!resolvedPaymentId) {
        showError('결제 결과를 불러올 수 없습니다.', failureUrl);
        return;
    }

    const data = {
        paymentId: resolvedPaymentId,
        orderId: params.get('orderId'),
        requestPrice: params.get('requestPrice'),
        paymentStatus: params.get('paymentStatus'),
        approvedAt: params.get('approvedAt'),
    };

    showSuccess(data);

    $('#confirmBtn').on('click', function () {
        if (successUrl) {
            const dest = new URL(successUrl);
            Object.entries(data).forEach(([k, v]) => {
                if (v != null && v !== '') dest.searchParams.set(k, v);
            });
            window.location.href = dest.toString();
        } else {
            window.close();
        }
    });
});

function showSuccess(data) {
    $('#result-price').text(Number(data.requestPrice).toLocaleString() + '원');
    $('#result-order').text(data.orderId || '-');
    $('#result-title').text('결제 요청 완료');
    $('#result-section').show();
    $('#confirmBtn').text('다음 단계 진행');
    $('#confirmBtn').show();
}

function showError(message, failureUrl) {
    $('#result-icon').text('✕');
    $('#result-title').text('결제 실패');
    $('#result-message').text(message).show();

    if (failureUrl) {
        $('#confirmBtn').text('닫기').show().on('click', function () {
            window.location.href = failureUrl;
        });
    }
}
