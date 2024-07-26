package com.testcaju.services;

import com.testcaju.domain.transaction.Transaction;
import com.testcaju.domain.user.MCCType;
import com.testcaju.domain.user.User;
import com.testcaju.dtos.TransactionDTO;
import com.testcaju.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class TransactionService {

    @Autowired
    TransactionRepository repository;

    @Autowired
    BalanceService balanceService;

    @Autowired
    UserService userService;

    private static final Map<String, MCCType> merchantGuess = new HashMap<>() {{
        put("super", MCCType.FOOD);
        put("mercado", MCCType.FOOD);
        put("carrefour", MCCType.FOOD);

        put("ifood", MCCType.MEAL); // IFOOD ???
        put("rest", MCCType.MEAL);
        put("burg", MCCType.MEAL);

    }};

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    protected Optional<MCCType> tryGuessMccByName(String merchantName) {
        String merchantLowerCase = merchantName.toLowerCase();
        for (Map.Entry<String, MCCType> entry : merchantGuess.entrySet()) {
            if (merchantLowerCase.contains(entry.getKey().toLowerCase())) {
                logger.info("MCC found in merchant name mapping, using MCC {} to verify balance", entry.getValue());
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    protected MCCType calcMcc(String merchantName, String mccCode) {
        Optional<MCCType> guessOptional = this.tryGuessMccByName(merchantName);
        if (guessOptional.isPresent()) return guessOptional.get();

        if ((mccCode.equals("5411")) || (mccCode.equals("5412")))
            return MCCType.FOOD;

        if ((mccCode.equals("5811")) || (mccCode.equals("5812")))
            return MCCType.MEAL;
        return MCCType.CASH;
    }

    public void save(Transaction transaction) {
        this.repository.save(transaction);
    }

    public List<Transaction> all() {
        return this.repository.findAll();
    }

    @Transactional
    public String authorize(TransactionDTO transactionPayload) {
        try {
            Optional<User> optionalUser = this.userService.getById(transactionPayload.account());
            User user = optionalUser.orElseThrow(()
                    -> new RuntimeException("Account not found: " + transactionPayload.account()));

            MCCType mccType = this.calcMcc(transactionPayload.merchant(), transactionPayload.mcc());
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(user);
            newTransaction.setMcc(mccType);
            newTransaction.setMerchant(transactionPayload.merchant());
            newTransaction.setAmount(transactionPayload.totalAmount());

            if (transactionPayload.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                this.balanceService.update(user, mccType, transactionPayload.totalAmount().negate());
                this.save(newTransaction);
                return "00";
            }
            Optional<MCCType> mcc =
                    this.balanceService.getMccForDebt(user, mccType, transactionPayload.totalAmount());
            if (mcc.isEmpty()) return "51";

            this.balanceService.update(user, mcc.get(), transactionPayload.totalAmount().negate());
            this.save(newTransaction);
            return "00";

        } catch (Exception e) {
            logger.error("Error updating balance for mcc {} in user account {}", transactionPayload.mcc(), transactionPayload.account());
        }
        return "07";
    }
}
