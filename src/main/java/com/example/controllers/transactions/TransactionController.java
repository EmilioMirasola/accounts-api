package com.example.controllers.transactions;

import com.example.accounts.application.commands.TransferMoneyCommand;
import com.example.accounts.application.commands.TransferMoneyCommandHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;

@Path("/api/v1/transactions")
public class TransactionController {

    @Inject
    TransferMoneyCommandHandler transferMoneyCommandHandler;

    @POST
    public Response createTransaction(NewTransactionRequest request) {
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(request.withdrawalAccountRef(), request.depositAccountRef(), request.amount());

        UUID result = transferMoneyCommandHandler.Handle(transferMoneyCommand);

        var location = URI.create("/api/v1/transactions/" + result.toString());
        return Response.created(location).build();
    }

}
