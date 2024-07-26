package com.testcaju.services;

import com.testcaju.domain.user.Balance;
import com.testcaju.domain.user.MCCType;
import com.testcaju.domain.user.User;
import com.testcaju.dtos.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    BalanceService balanceService;

    @Mock
    UserService userService;

    @Autowired
    @InjectMocks
    TransactionService transactionService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("Harley Souto Amaro Dalva");

        List<Balance> balances = new ArrayList<>();
        balances.add(new Balance(1L, MCCType.FOOD, new BigDecimal(100), user));
        balances.add(new Balance(1L, MCCType.MEAL, new BigDecimal(100), user));
        balances.add(new Balance(1L, MCCType.CASH, new BigDecimal(100), user));
        user.setBalances(balances);

        when(this.userService.getById(anyString())).thenReturn(Optional.of(user));
        doNothing().when(this.balanceService).update(any(User.class), any(MCCType.class), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Authorize a transaction with a negative value which means deposit")
    void authorizeCase1() {
        String userId = "1";
        String merchantName = "PAG*JoseDaSilva          RIO DE JANEI BR";
        String merchantMcc = "5411";
        BigDecimal value = new BigDecimal(-100);
        TransactionDTO request = new TransactionDTO(userId, value, merchantMcc, merchantName);
        String result = transactionService.authorize(request);
        assertThat(result.equals("00")).isTrue();
    }

    @Test
    @DisplayName("Authorize a transaction with balance at same mcc")
    void authorizeCase2() {
        String userId = "1";
        String merchantName = "PAG*JoseDaSilva          RIO DE JANEI BR";
        String merchantMcc = "5411";
        BigDecimal value = new BigDecimal(100);

        when(this.balanceService.getMccForDebt(any(User.class), any(MCCType.class), any(BigDecimal.class))).thenReturn(Optional.of(MCCType.FOOD));
        TransactionDTO request = new TransactionDTO(userId, value, merchantMcc, merchantName);
        String result = this.transactionService.authorize(request);
        assertThat(result.equals("00")).isTrue();
    }

    @Test
    @DisplayName("Deny a transaction without balance")
    void authorizeCase3() {
        String userId = "1";
        String merchantName = "PAG*JoseDaSilva          RIO DE JANEI BR";
        String merchantMcc = "5411";
        BigDecimal value = new BigDecimal(5000);

        when(this.balanceService.getMccForDebt(any(User.class), any(MCCType.class), any(BigDecimal.class))).thenReturn(Optional.empty());
        TransactionDTO request = new TransactionDTO(userId, value, merchantMcc, merchantName);
        String result = transactionService.authorize(request);
        assertThat(result.equals("51")).isTrue();
    }

    @Test
    @DisplayName("Determine MCC code")
    void calcMccCase1() {

        MCCType result = transactionService.calcMcc("PAG*JoseDaSilva RIO DE JANEI BR", "1010");
        assertThat(result).isEqualTo(MCCType.CASH);
        result = transactionService.calcMcc("Super mercado GUARANI  RIO DE JANEI BR", "1010");
        assertThat(result).isEqualTo(MCCType.FOOD);
        result = transactionService.calcMcc("RESTAURANTE BARBACOA", "1010");
        assertThat(result).isEqualTo(MCCType.MEAL);

        result = transactionService.calcMcc("PAG*JoseDaSilva RIO DE JANEI BR", "5411");
        assertThat(result).isEqualTo(MCCType.FOOD);
        result = transactionService.calcMcc("PAG*JoseDaSilva RIO DE JANEI BR", "5412");
        assertThat(result).isEqualTo(MCCType.FOOD);

        result = transactionService.calcMcc("PAG*JoseDaSilva RIO DE JANEI BR", "5811");
        assertThat(result).isEqualTo(MCCType.MEAL);
        result = transactionService.calcMcc("PAG*JoseDaSilva RIO DE JANEI BR", "5812");
        assertThat(result).isEqualTo(MCCType.MEAL);

    }
}