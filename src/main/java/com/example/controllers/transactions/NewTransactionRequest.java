package com.example.controllers.transactions;

import java.util.UUID;

public record NewTransactionRequest(UUID withdrawalAccountRef, UUID depositAccountRef, double amount) {
}
