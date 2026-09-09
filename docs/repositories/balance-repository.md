# Balance Repository

BalanceRepository provides APIs for querying account and token balance information from the mirror node. 
It allows user to retrieve balances for all accounts, query the balance of a specific account, and retrieve the latest balance snapshot.

---

## Methods

| Method                               | Description                                                              |
| :----------------------------------- | :----------------------------------------------------------------------- |
| `findAll()`                          | Retrieves a page containing account balances.                            |
| `findByAccount(AccountId accountId)` | Retrieves the balance for a specific account using an AccountId.       |
| `findByAccount(String accountId)`    | Retrieves the balance for a specific account using an account ID string. |
| `getSnapshot()`                      | Retrieves the latest account and token balance snapshot.                 |

---

## Find All Account Balances

```java title="findAll()"
Page<AccountBalance> balances =
    balanceRepository.findAll();
```

---

## Find Balance By Account

```java title="findByAccount(AccountId accountId)"
AccountId accountId =
    AccountId.fromString("0.0.1234");

Page<AccountBalance> balance =
    balanceRepository.findByAccount(accountId);
```

---

## Get Balance Snapshot

```java title="getSnapshot()"
Optional<BalanceSnapshot> snapshot =
    balanceRepository.getSnapshot();
```

