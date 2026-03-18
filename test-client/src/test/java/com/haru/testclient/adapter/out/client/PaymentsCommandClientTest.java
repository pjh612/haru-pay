package com.haru.testclient.adapter.out.client;

import com.haru.testclient.application.dto.ConfirmPaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PaymentsCommandClientTest {

    @Test
    void preparePayment_ShouldForwardIdempotencyKeyHeader() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://payments.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PaymentsCommandClient client = new PaymentsCommandClient(builder.build());

        server.expect(requestTo("http://payments.test/api/payment/prepare"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "api-key"))
                .andExpect(header("X-PAY-CLIENT-ID", "client-1"))
                .andExpect(header("Idempotency-Key", "prepare-key-1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("{\"paymentId\":\"019cfca0-7147-727d-9d56-53793256e8ac\"}", MediaType.APPLICATION_JSON));

        client.preparePayment(
                "client-1",
                "api-key",
                new PreparePaymentRequest("ORDER-1", "product", BigDecimal.TEN),
                "prepare-key-1"
        );

        server.verify();
    }

    @Test
    void confirmPayment_ShouldForwardIdempotencyKeyHeader() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://payments.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PaymentsCommandClient client = new PaymentsCommandClient(builder.build());
        UUID paymentId = UUID.fromString("019cfca0-7147-727d-9d56-53793256e8ac");

        server.expect(requestTo("http://payments.test/api/payment/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "api-key"))
                .andExpect(header("X-PAY-CLIENT-ID", "client-1"))
                .andExpect(header("Idempotency-Key", "confirm-key-1"))
                .andRespond(withSuccess());

        client.confirmPayment(
                "client-1",
                "api-key",
                new ConfirmPaymentRequest(paymentId),
                "confirm-key-1"
        );

        server.verify();
    }

    @Test
    void preparePayment_ShouldOmitIdempotencyKeyHeaderWhenBlank() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://payments.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PaymentsCommandClient client = new PaymentsCommandClient(builder.build());

        server.expect(requestTo("http://payments.test/api/payment/prepare"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "api-key"))
                .andExpect(header("X-PAY-CLIENT-ID", "client-1"))
                .andRespond(withSuccess("{\"paymentId\":\"019cfca0-7147-727d-9d56-53793256e8ac\"}", MediaType.APPLICATION_JSON));

        client.preparePayment(
                "client-1",
                "api-key",
                new PreparePaymentRequest("ORDER-1", "product", BigDecimal.TEN),
                "  "
        );

        server.verify();
    }
}
