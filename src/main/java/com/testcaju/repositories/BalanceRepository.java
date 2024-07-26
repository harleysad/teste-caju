package com.testcaju.repositories;

import com.testcaju.domain.user.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, String> {
    @Query(value = "SELECT * FROM user_balances t WHERE t.user_id = :userId AND t.mcc = :mcc",
            nativeQuery = true)
    Optional<Balance> findByUserIdAndMcc(@Param("userId") Long userId, @Param("mcc") String mcc);

    @Query(value = "SELECT * FROM user_balances t WHERE t.user_id = :userId ",
            nativeQuery = true)
    Optional<List<Balance>> findByUser(@Param("userId") Long userId);
}