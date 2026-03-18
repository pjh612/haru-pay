class HaruPaySDK {
    constructor({checkoutUrl, prepareUrl, successUrl, failureUrl}) {
        if (!successUrl || !failureUrl) {
            throw new Error("successUrl과 failureUrl은 필수입니다.");
        }

        this.baseUrl = checkoutUrl || 'http://payments:8071';
        this.prepareUrl = prepareUrl;
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
        this.popup = null;
        this.handleMessage = this.handleMessage.bind(this);
    }

    getCheckoutOrigin() {
        return new URL(this.baseUrl, window.location.origin).origin;
    }

    buildUrl(baseUrl, params) {
        const url = new URL(baseUrl, window.location.origin);

        Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null && value !== '') {
                url.searchParams.append(key, String(value));
            }
        });

        return url.toString();
    }

    createPayment(options) {
        const {orderId, productName, amount} = options;

        if (!this.prepareUrl) {
            throw new Error("prepareUrl이 설정되지 않았습니다.");
        }

        if (!orderId || !productName || amount == null) {
            throw new Error("orderId, productName, amount는 가결제 생성에 필수입니다.");
        }

        return fetch(this.prepareUrl, {
            method: "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            body: JSON.stringify({
                "orderId": orderId,
                "requestPrice": amount,
                "productName": productName
            }),
        }).then(async function (response) {
            if (!response.ok) {
                throw new Error("가결제 생성 요청에 실패했습니다.");
            }

            const data = await response.json();
            if (!data || !data.paymentId) {
                throw new Error("가결제 생성 응답에 paymentId가 없습니다.");
            }

            return data.paymentId;
        });
    }

    ensurePopup() {
        if (this.popup && !this.popup.closed) {
            this.popup.close();
        }

        this.popup = window.open(
            'about:blank',
            "paymentWindow",
            "width=500,height=700,scrollbars=no,resizable=no"
        );

        if (!this.popup) {
            throw new Error("결제창을 열 수 없습니다.");
        }

        window.removeEventListener("message", this.handleMessage);
        window.addEventListener("message", this.handleMessage);

        return this.popup;
    }

    openPaymentWindow(paymentId, popup) {
        const paymentUrl = `${this.baseUrl}/pay/${paymentId}`;
        const paymentPopup = popup || this.ensurePopup();

        paymentPopup.location.href = paymentUrl;

        return paymentId;
    }

    validateOpenOptions(options) {
        if (!options) {
            throw new Error("결제 옵션이 필요합니다.");
        }

        const hasPaymentId = !!options.paymentId;
        const hasPrepareFields = options.orderId != null || options.productName != null || options.amount != null;

        if (hasPaymentId && hasPrepareFields) {
            throw new Error("paymentId 방식과 가결제 생성 옵션은 함께 사용할 수 없습니다.");
        }

        if (!hasPaymentId && !hasPrepareFields) {
            throw new Error("paymentId 또는 가결제 생성 정보가 필요합니다.");
        }
    }

    handleOpenError(error, popup, options) {
        const failureUrl = this.buildUrl(this.failureUrl, {
            errorCode: error.code || "SDK_ERROR",
            message: error.message || "결제에 실패했습니다.",
            orderId: options && options.orderId,
            paymentId: options && options.paymentId,
        });

        if (popup && !popup.closed) {
            popup.location.href = failureUrl;
            this.popup = null;
            return;
        }

        window.location.href = failureUrl;
    }

    handleMessage(event) {
        const data = event.data;

        if (event.origin !== this.getCheckoutOrigin()) {
            return;
        }

        if (!data) {
            return;
        }

        if (!this.popup || this.popup.closed || event.source !== this.popup) {
            return;
        }

        if (data.errorCode || data.status === "FAILED") {
            window.removeEventListener("message", this.handleMessage);
            this.popup.location.href = this.buildUrl(this.failureUrl, {
                errorCode: data.errorCode || "PAYMENT_REQUEST_FAILED",
                message: data.message || "결제 요청에 실패했습니다.",
                orderId: data.orderId,
                paymentId: data.paymentId,
            });
            this.popup = null;
            return;
        }

        if (!data.requestId) {
            return;
        }

        window.removeEventListener("message", this.handleMessage);
        this.popup.location.href = this.buildUrl(this.successUrl, {
            requestId: data.requestId,
            paymentId: data.requestId,
            orderId: data.orderId,
            requestPrice: data.requestPrice,
            paymentStatus: data.paymentStatus,
            approvedAt: data.approvedAt,
        });
        this.popup = null;
    }

    // 결제창 열기
    open(options) {
        this.validateOpenOptions(options);

        const paymentId = options.paymentId;

        if (paymentId) {
            return Promise.resolve(this.openPaymentWindow(paymentId));
        }

        let pendingPopup;

        try {
            pendingPopup = this.ensurePopup();
        } catch (error) {
            return Promise.reject(error);
        }

        return this.createPayment(options)
            .then((createdPaymentId) => this.openPaymentWindow(createdPaymentId, pendingPopup))
            .catch((error) => this.handleOpenError(error, pendingPopup, options));
    }
}

const HaruPay = {
    create: (config) => new HaruPaySDK(config),
};

window.HaruPay = HaruPay;
