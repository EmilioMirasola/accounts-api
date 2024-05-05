package com.example.controllers.accounts;

import com.example.accounts.application.commands.OpenAccountCommand;
import com.example.accounts.application.commands.OpenAccountCommandHandler;
import com.example.accounts.application.dto.AccountDTO;
import com.example.accounts.application.queries.FindAccountsQuery;
import com.example.accounts.application.queries.FindAccountsQueryHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/accounts")
public class AccountController {
    @Inject
    OpenAccountCommandHandler openAccountCommandHandler;

    @Inject
    FindAccountsQueryHandler findAccountsQueryHandler;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(NewAccountRequest request) {
        UUID ref = openAccountCommandHandler.Handle(new OpenAccountCommand(request.name()));
        return Response.created(URI.create("/api/v1/accounts/" + ref.toString())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(@QueryParam("page") int page, @QueryParam("numberOfAccounts") int numberOfAccounts) {
        final var query = new FindAccountsQuery(page, numberOfAccounts);
        List<AccountDTO> result = findAccountsQueryHandler.handle(query);

        List<AccountResource> accountResources = result.stream().map(accountDTO -> new AccountResource(accountDTO.name(), accountDTO.ref(), accountDTO.balance())).toList();
        return Response.ok(accountResources).build();
    }
}
