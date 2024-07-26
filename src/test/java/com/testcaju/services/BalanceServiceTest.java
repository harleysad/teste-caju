package com.testcaju.services;

import com.testcaju.domain.user.Balance;
import com.testcaju.domain.user.MCCType;
import com.testcaju.domain.user.User;
import com.testcaju.repositories.BalanceRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository repository;

    @Autowired
    @InjectMocks
    private BalanceService balanceService;

    @Test
    @DisplayName("Determine MCC for debit, without any balance")
    void getMccForDebtCase1() {

        User user = new User();
        user.setId(1L);
        user.setName("Harley Souto Amaro Dalva");

        List<Balance> balances = new ArrayList<>();
        user.setBalances(balances);

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.empty());
        Optional<MCCType> result = this.balanceService.getMccForDebt(user, any(MCCType.class), new BigDecimal(10));
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Determine MCC for debit, with balance")
    void getMccForDebtCase2() {

        User user = new User();
        user.setId(1L);
        user.setName("Harley Souto Amaro Dalva");

        List<Balance> balances = new ArrayList<>();
        balances.add(new Balance(1L, MCCType.FOOD, new BigDecimal(100), user));
        balances.add(new Balance(1L, MCCType.MEAL, new BigDecimal(100), user));
        balances.add(new Balance(1L, MCCType.CASH, new BigDecimal(1000), user));
        user.setBalances(balances);
        // Same MCC
        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        Optional<MCCType> result = this.balanceService.getMccForDebt(user, MCCType.MEAL, new BigDecimal(10));
        assertThat(result).isEqualTo(Optional.of(MCCType.MEAL));

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.FOOD, new BigDecimal(10));
        assertThat(result).isEqualTo(Optional.of(MCCType.FOOD));

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.CASH, new BigDecimal(10));
        assertThat(result).isEqualTo(Optional.of(MCCType.CASH));

        // change to cash
        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.FOOD, new BigDecimal(500));
        assertThat(result).isEqualTo(Optional.of(MCCType.CASH));

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.MEAL, new BigDecimal(500));
        assertThat(result).isEqualTo(Optional.of(MCCType.CASH));

    }

    @Test
    @DisplayName("Determine MCC for debit, without balance")
    void getMccForDebtCase3() {

        User user = new User();
        user.setId(1L);
        user.setName("Harley Souto Amaro Dalva");

        List<Balance> balances = new ArrayList<>();
        balances.add(new Balance(1L, MCCType.FOOD, new BigDecimal(1), user));
        balances.add(new Balance(1L, MCCType.MEAL, new BigDecimal(1), user));
        balances.add(new Balance(1L, MCCType.CASH, new BigDecimal(10), user));
        user.setBalances(balances);

        // Same MCC
        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        Optional<MCCType> result = this.balanceService.getMccForDebt(user, MCCType.MEAL, new BigDecimal(11));
        assertThat(result).isEqualTo(Optional.empty());

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.FOOD, new BigDecimal(11));
        assertThat(result).isEqualTo(Optional.empty());

        when(this.repository.findByUser(anyLong())).thenReturn(Optional.of(balances));
        result = this.balanceService.getMccForDebt(user, MCCType.CASH, new BigDecimal(11));
        assertThat(result).isEqualTo(Optional.empty());

    }

}
