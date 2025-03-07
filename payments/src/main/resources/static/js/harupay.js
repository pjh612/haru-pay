class HaruPaySDK {
    constructor({clientId, successUrl, failureUrl}) {
        if (!clientId) {
            throw new Error("clientId는 필수입니다.");
        }
        this.clientId = clientId;
        this.baseUrl = 'http://payments:8071';
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
    }

    // 결제창 열기
    open(options) {
        const {paymentId, productName, amount, onSuccess, onError} = options;
        const paymentUrl = `${this.baseUrl}/pay/${paymentId}`;
        const popup = window.open(
            paymentUrl,
            "paymentWindow",
            "width=500,height=700,scrollbars=no,resizable=no"
        );

        window.addEventListener("message", (event) => {
            const data = event.data;
            const url = new URL(this.successUrl);
            url.searchParams.append('orderId', data.orderId);
            url.searchParams.append('paymentId', data.requestId);
            url.searchParams.append('requestPrice', data.requestPrice);
            popup.location.href = url.toString();
        });
    }
}

const HaruPay = {
    create: (config) => new HaruPaySDK(config),
};