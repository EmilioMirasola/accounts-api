package com.example;

import com.example.accounts.domain.AccountTransferAmount;
import com.example.common.errors.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTransferAmountTest {

    @Test
    public void testAccountTransferAmount_negativeValue_shouldThrowException() {
        //Arrange

        double value = -1;

        //Act

        Assertions.assertThrows(ValidationException.class, () -> new AccountTransferAmount(value));

        //Assert
    }

    @Test
    public void testAccountTransferAmount_zero_shouldThrowException() {
        //Arrange

        double value = 0;

        //Act

        Assertions.assertThrows(ValidationException.class, () -> new AccountTransferAmount(value));

        //Assert
    }

    @Test
    public void testAccountTransferAmount_one_shouldSucceed() {
        //Arrange

        double value = 1;

        //Act

        Assertions.assertDoesNotThrow(() -> new AccountTransferAmount(value));

        //Assert
    }
}
