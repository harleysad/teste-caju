package com.testcaju.services;

import com.testcaju.domain.user.Balance;
import com.testcaju.domain.user.MCCType;
import com.testcaju.domain.user.User;
import com.testcaju.repositories.BalanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {

    @Autowired
    private BalanceRepository repository;

    @Autowired
    private UserService userService;

    public Optional<Balance> getById(String id) {
        return this.repository.findById(id);
    }

    public boolean hasBalance(Balance balance, BigDecimal newValue) {
        return (balance.getAmount().compareTo(newValue) >= 0);
    }

    public Optional<MCCType> getMccForDebt(User user, MCCType mcc, BigDecimal newValue) {

        Optional<List<Balance>> existingBalancesOptional = this.repository.findByUser(user.getId());
        // no balances no credit
        if (existingBalancesOptional.isEmpty()) return Optional.empty();
        // no balances no credit
        List<Balance> existingBalances = existingBalancesOptional.get();
        if (existingBalances.isEmpty()) return Optional.empty();

        // find correct mcc
        Optional<Balance> correctMccBalanceOptional = existingBalances.stream()
                .filter(b -> b.getMcc() == mcc)
                .findFirst();
        // check exists and value
        if (correctMccBalanceOptional.isPresent() &&
                this.hasBalance(correctMccBalanceOptional.get(), newValue)
        ) {
            return Optional.ofNullable(correctMccBalanceOptional.get().getMcc());
        }

        // find fallback mcc
        Optional<Balance> fallBackMccBalanceOptional = existingBalances.stream()
                .filter(b -> b.getMcc() == MCCType.CASH)
                .findFirst();
        // check exists and value
        if (fallBackMccBalanceOptional.isPresent() &&
                this.hasBalance(fallBackMccBalanceOptional.get(), newValue)
        ) {
            return Optional.ofNullable(fallBackMccBalanceOptional.get().getMcc());
        }
        return Optional.empty();
    }

    @Transactional
    public void update(User user, MCCType mcc, BigDecimal newTransactionValue) {

        Optional<Balance> existingBalanceOptional = this.repository.findByUserIdAndMcc(user.getId(), mcc.toString());

        if (existingBalanceOptional.isEmpty()) {
            Balance newBalance = new Balance();
            newBalance.setUser(user);
            newBalance.setMcc(mcc);
            newBalance.setAmount(newTransactionValue);
            this.repository.save(newBalance);
        } else {
            this.userService.getById(user.getId().toString())
                    .orElseThrow(() -> new RuntimeException("Balance not found for user id: " + user.getId()));
            Balance existingBalance = existingBalanceOptional.get();
            existingBalance.setAmount(newTransactionValue.add(existingBalance.getAmount()));
            this.repository.save(existingBalance);
        }
    }

    public List<Balance> all() {
        return this.repository.findAll();
    }
}
