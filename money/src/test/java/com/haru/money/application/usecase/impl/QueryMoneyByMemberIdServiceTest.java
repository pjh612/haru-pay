package com.haru.money.application.usecase.impl;

import com.haru.money.application.dto.MoneyInfoResponse;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.repository.MoneyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryMoneyByMemberIdServiceTest {

    @InjectMocks
    private QueryMoneyByMemberIdService queryMoneyByMemberIdService;

    @Mock
    private MoneyRepository moneyRepository;

    @Test
    void query_ShouldReturnMoneyInfoResponse_WhenMemberExists() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(1000);
        Money money = new Money(UUID.randomUUID(), memberId, balance);

        when(moneyRepository.findByMemberId(memberId)).thenReturn(Optional.of(money));

        // Act
        MoneyInfoResponse response = queryMoneyByMemberIdService.query(memberId);

        // Assert
        Assertions.assertThat(response.memberId()).isEqualTo(memberId);
        Assertions.assertThat(response.balance()).isEqualTo(balance);
        verify(moneyRepository, times(1)).findByMemberId(memberId);
    }

    @Test
    void query_ShouldThrowEntityNotFoundException_WhenMemberDoesNotExist() {
        // Arrange
        UUID memberId = UUID.randomUUID();

        when(moneyRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> queryMoneyByMemberIdService.query(memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 회원의 하루 머니 정보가 없습니다.");

        verify(moneyRepository, times(1)).findByMemberId(memberId);
    }
}