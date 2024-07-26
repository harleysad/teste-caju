package com.testcaju.domain.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testcaju.domain.user.MCCType;
import com.testcaju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "transactions")
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private MCCType mcc;

    @JoinColumn(name = "merchant_id")
    private String merchant;

    private LocalDateTime CreatedAt;

    @PrePersist
    protected void onCreate() {
        this.CreatedAt = LocalDateTime.now();
    }
}
