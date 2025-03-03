package com.haru.money.application.usecase.impl;

import com.haru.money.adapters.in.event.loadmoney.LoadMoneyFinishedEvent;
import com.haru.money.domain.model.ChangingStatus;
import com.haru.money.domain.model.ChangingType;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.model.MoneyChangingRequest;
import com.haru.money.domain.repository.MoneyChangingRequestRepository;
import com.haru.money.domain.repository.MoneyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoneyTransactionServiceUnitTest {

    @InjectMocks
    private MoneyTransactionService moneyTransactionService;

    @Mock
    private MoneyChangingRequestRepository moneyChangingRequestRepository;

    @Mock
    private MoneyRepository moneyRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void loadMoney_ShouldIncreaseBalanceAndPublishEvent() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(500);

        MoneyChangingRequest request = MoneyChangingRequest.createNew(requestId, memberId, ChangingType.INCREASE, amount);
        Money money = Money.createNew(memberId);
        money.load(BigDecimal.valueOf(1000));

        when(moneyChangingRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(moneyRepository.findByMemberId(memberId)).thenReturn(Optional.of(money));

        // Act
        moneyTransactionService.loadMoney(requestId);

        // Assert
        Assertions.assertThat(money.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        Assertions.assertThat(request.getStatus()).isEqualTo(ChangingStatus.SUCCEEDED);
        verify(eventPublisher).publishEvent(any(LoadMoneyFinishedEvent.class));
        verify(moneyChangingRequestRepository).save(request);
        verify(moneyRepository).save(money);
    }
}
