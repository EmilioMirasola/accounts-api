package com.example.accounts.application.commands;

import java.util.UUID;

public record TransferMoneyCommand(UUID withdrawalAccountRef, UUID depositAccountRef, double amount) {
}
