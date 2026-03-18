package com.haru.orchestrator.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "saga.recovery")
public class SagaRecoveryProperties {
    private boolean enabled = true;
    private Duration scanInterval = Duration.ofMinutes(1);
    private Duration paymentConfirmRequestTimeout = Duration.ofMinutes(2);
    private Duration decreaseMoneyTimeout = Duration.ofMinutes(2);
    private Duration confirmPaymentTimeout = Duration.ofMinutes(5);
    private Duration loadMoneyTimeout = Duration.ofMinutes(5);
    private Duration abortingTimeout = Duration.ofMinutes(5);
    private int maxRecoveryAttempts = 3;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(Duration scanInterval) {
        this.scanInterval = scanInterval;
    }

    public Duration getPaymentConfirmRequestTimeout() {
        return paymentConfirmRequestTimeout;
    }

    public void setPaymentConfirmRequestTimeout(Duration paymentConfirmRequestTimeout) {
        this.paymentConfirmRequestTimeout = paymentConfirmRequestTimeout;
    }

    public Duration getDecreaseMoneyTimeout() {
        return decreaseMoneyTimeout;
    }

    public void setDecreaseMoneyTimeout(Duration decreaseMoneyTimeout) {
        this.decreaseMoneyTimeout = decreaseMoneyTimeout;
    }

    public Duration getConfirmPaymentTimeout() {
        return confirmPaymentTimeout;
    }

    public void setConfirmPaymentTimeout(Duration confirmPaymentTimeout) {
        this.confirmPaymentTimeout = confirmPaymentTimeout;
    }

    public Duration getLoadMoneyTimeout() {
        return loadMoneyTimeout;
    }

    public void setLoadMoneyTimeout(Duration loadMoneyTimeout) {
        this.loadMoneyTimeout = loadMoneyTimeout;
    }

    public Duration getAbortingTimeout() {
        return abortingTimeout;
    }

    public void setAbortingTimeout(Duration abortingTimeout) {
        this.abortingTimeout = abortingTimeout;
    }

    public int getMaxRecoveryAttempts() {
        return maxRecoveryAttempts;
    }

    public void setMaxRecoveryAttempts(int maxRecoveryAttempts) {
        this.maxRecoveryAttempts = maxRecoveryAttempts;
    }
}
