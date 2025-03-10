class HaruPaySDK {
    constructor({clientId, successUrl, failureUrl}) {
        if (!clientId) {
            throw new Error("clientId는 필수입니다.");
        }
        this.clientId = clientId;
        this.baseUrl = 'http://payments:8071';
        this.successUrl = successUrl;
    }

    // 결제창 열기
    open(options) {
        const {orderId, productName, amount} = options;
        const self = this;
        let popup;
        $.ajax({
            type: "post",
            headers: {
                "X-PAY-CLIENT-ID": this.clientId
            },
            url: `${this.baseUrl}/api/payment/prepare`,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "orderId": orderId,
                "requestPrice": amount,
                "productName": productName
            }),
            success: function (data) {
                const paymentId = data.paymentId;
                const paymentUrl = `${self.baseUrl}/pay/${paymentId}`;
                popup = window.open(
                    paymentUrl,
                    "paymentWindow",
                    "width=500,height=700,scrollbars=no,resizable=no"
                );


            },
            error: function (e) {
                alert("결제에 실패했습니다.");
            }
        });

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