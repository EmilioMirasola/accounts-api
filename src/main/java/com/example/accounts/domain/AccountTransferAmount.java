package com.example.accounts.domain;

import com.example.common.errors.ValidationException;

public record AccountTransferAmount(double value) {
    public AccountTransferAmount(double value) {
        this.value = validateTransferAmount(value);
    }

    private static double validateTransferAmount(double amount) {
        if (amount <= 0) {
            throw new ValidationException("Transfer amount must be greater than zero");
        }
        return amount;
    }
}
