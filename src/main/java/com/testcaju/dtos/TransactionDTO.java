package com.testcaju.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Transaction authorize payload")
public record TransactionDTO(
        @Schema(description = "User identifier",
                example = "1")
        @NotNull
        String account,
        @Schema(description = "Transaction Value", example = "875.45")
        @NotNull
        BigDecimal totalAmount,
        @Schema(description = "Merchant Category Code", example = "9348")
        @NotNull
        String mcc,
        @Schema(description = "Merchant Name",
                example = "PADARIA DO ZE               SAO PAULO BR")
        @NotNull
        String merchant) {
}
