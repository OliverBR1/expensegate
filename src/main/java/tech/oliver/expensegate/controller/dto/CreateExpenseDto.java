package tech.oliver.expensegate.controller.dto;

import java.math.BigDecimal;

public record CreateExpenseDto(String title, BigDecimal amount) {
}
