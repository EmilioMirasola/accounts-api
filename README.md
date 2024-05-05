# Account API coding challenge

## Stack

The Account API coding challenge is implemented in Java using the Quarkus framework. The main reason for choosing this
stack is that Bankdata is using this stack. But also because I've been very curious on the Quarkus framework and wanted
to try it out and give myself a challenge.

## Problem

The program attempts so solve the problem of handling accounts that holds money and how to transfer money between
accounts.

## Solution

The solution has been to use OOP with strong emphasis on the domain. The fundamental business rules are implemented in a
domain layer with validation.

The use cases for this domain is implemented in an application layer, where procedural code uses the business rules to
compose use cases. For example transferring money from one account to another.
The application layer is implemented with commands and queries to separate reads and writes.

To save the state of accounts, a database is used, encapsulated in a repository pattern. As the balance of an account is
a function of initial account balance, all incoming transactions and all outgoing transactions, a balance state is not
maintained for accounts, but is instead calculated. This also enables auditing, as the transaction history of all
accounts are saved.

There's a REST API with two resources - accounts and transactions - that can be utilized using HTTP. The API layer
consumes the application layer to invoke use cases.

## Tests

As mentioned, the most critical rules in the domain is tested with Junit.
The Transfer money use case is also unit tested with mocked out repositories.

## Observability

Metrics are exposed using Micrometer and scraped by a prometheus server.
Using grafana, those metrics are visualized in Grafana using a Micrometer dashboard.
Major calls to the stack are traced with a new span using Open Telemetry and exported to a Jaeger collector.

If you want to run those services alongside the application, run docker compose up in your terminal.