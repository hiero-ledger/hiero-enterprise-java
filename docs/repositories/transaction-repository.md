# Transaction Repository

TransactionRepository provides APIs for querying transaction data from a Hiero Mirror Node, including searching transactions by account, transaction type, result status, balance modifications, and transaction ID.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findByAccount(AccountId accountId)` | Retrieves all transactions associated with an account. |
| `findByAccount(String accountId)` | Retrieves all transactions associated with an account using an account ID string. |
| `findByAccountAndType(AccountId accountId, TransactionType type)` | Retrieves transactions for an account filtered by transaction type. |
| `findByAccountAndType(String accountId, TransactionType type)` | Retrieves transactions for an account filtered by transaction type using an account ID string. |
| `findByAccountAndResult(AccountId accountId, Result result)` | Retrieves transactions for an account filtered by transaction result. |
| `findByAccountAndResult(String accountId, Result result)` | Retrieves transactions for an account filtered by result using an account ID string. |
| `findByAccountAndModification(AccountId accountId, BalanceModification type)` | Retrieves transactions filtered by balance modification type. |
| `findByAccountAndModification(String accountId, BalanceModification type)` | Retrieves transactions filtered by modification type using an account ID string. |
| `findById(String transactionId)` | Retrieves a transaction by its transaction ID. |

---

## Find Transactions By Account

```java title="findByAccount(AccountId accountId)"
AccountId accountId = AccountId.fromString("0.0.1234");

Page<TransactionInfo> transactions =
    transactionRepository.findByAccount(accountId);

System.out.println(transactions);
```

---

## Find Transactions By Type

```java title="findByAccountAndType(AccountId accountId, TransactionType type)"
AccountId accountId = AccountId.fromString("0.0.1234");

Page<TransactionInfo> transactions =
    transactionRepository.findByAccountAndType(
        accountId,
        TransactionType.CRYPTO_TRANSFER
    );

System.out.println(transactions);
```

---

## Find Transactions By Result

```java title="findByAccountAndResult(AccountId accountId, Result result)"
AccountId accountId = AccountId.fromString("0.0.1234");

Page<TransactionInfo> transactions =
    transactionRepository.findByAccountAndResult(
        accountId,
        Result.SUCCESS
    );

System.out.println(transactions);
```

---

## Find Transactions By Balance Modification

```java title="findByAccountAndModification(AccountId accountId, BalanceModification type)"
AccountId accountId = AccountId.fromString("0.0.1234");

Page<TransactionInfo> transactions =
    transactionRepository.findByAccountAndModification(
        accountId,
        BalanceModification.DEBIT
    );

System.out.println(transactions);
```

---

## Find Transaction By ID
```java title="findById(String transactionId)"
Optional<TransactionInfo> transaction =
    transactionRepository.findById(
        "0.0.1234-1234567890-000000001"
    );

transaction.ifPresent(System.out::println);
```