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
    }

    extractProblemType(problem) {
        const defaultType = "internal-error";
        const rawType = problem?.errorType || problem?.type;

        if (!rawType || typeof rawType !== "string") {
            return defaultType;
        }

        if (!rawType.includes("/")) {
            return rawType;
        }

        const segments = rawType.split("/").filter(Boolean);
        return segments.length > 0 ? segments[segments.length - 1] : defaultType;
    }

    extractProblemMessage(problem, fallback) {
        if (problem?.detail) {
            return String(problem.detail);
        }

        if (problem?.message) {
            return String(problem.message);
        }

        return fallback;
    }

    createSdkError(code, message, metadata = {}) {
        const error = new Error(message);
        error.code = code;
        error.problemType = metadata.type;
        error.problemTitle = metadata.title;
        error.problemStatus = metadata.status;
        error.problemDetail = metadata.detail;
        return error;
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
        }).then(async (response) => {
            if (!response.ok) {
                let problem;
                try {
                    problem = await response.json();
                } catch (_) {
                    problem = null;
                }

                const code = this.extractProblemType(problem);
                const message = this.extractProblemMessage(problem, "가결제 생성 요청에 실패했습니다.");
                throw this.createSdkError(code, message, {
                    type: problem?.type,
                    title: problem?.title,
                    status: problem?.status,
                    detail: problem?.detail,
                });
            }

            const data = await response.json();
            if (!data || !data.paymentId) {
                throw this.createSdkError("invalid-prepare-response", "가결제 생성 응답에 paymentId가 없습니다.");
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

        return this.popup;
    }

    openPaymentWindow(paymentId, popup) {
        const url = new URL(`${this.baseUrl}/pay/${paymentId}`);
        url.searchParams.set('successUrl', this.successUrl);
        url.searchParams.set('failureUrl', this.failureUrl);

        const paymentPopup = popup || this.ensurePopup();
        paymentPopup.location.href = url.toString();

        return paymentId;
    }

    validateOpenOptions(options) {
        if (!options) {
            throw new Error("결제 옵션이 필요합니다.");
        }

        if (!this.prepareUrl) {
            throw new Error("prepareUrl이 설정되지 않았습니다.");
        }

        if (!options.orderId || !options.productName || options.amount == null) {
            throw new Error("orderId, productName, amount는 필수입니다.");
        }
    }

    handleOpenError(error, popup) {
        const errorUrl = this.buildUrl(`${this.baseUrl}/error-page`, {
            code: error.code || "payment-prepare-failed",
            message: error.message,
            type: error.problemType,
            title: error.problemTitle,
            status: error.problemStatus,
            detail: error.problemDetail,
        });

        if (popup && !popup.closed) {
            popup.location.href = errorUrl;
            this.popup = null;
            return;
        }

        window.location.href = errorUrl;
    }

    // 결제창 열기
    open(options) {
        this.validateOpenOptions(options);

        let pendingPopup;

        try {
            pendingPopup = this.ensurePopup();
        } catch (error) {
            return Promise.reject(error);
        }

        return this.createPayment(options)
            .then((createdPaymentId) => this.openPaymentWindow(createdPaymentId, pendingPopup))
            .catch((error) => this.handleOpenError(error, pendingPopup));
    }
}

const HaruPay = {
    create: (config) => new HaruPaySDK(config),
};

window.HaruPay = HaruPay;
