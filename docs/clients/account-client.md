# Account Client

`AccountClient` provides APIs for managing Hiero accounts, including account creation, deletion, updates, and balance queries.

!!! note

    Account operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createAccount()` | Creates a new account with a default balance of 0 HBAR. |
| `createAccount(Hbar initialBalance)` | Creates a new account with the specified initial HBAR balance. |
| `createAccount(long initialBalanceInHbar)` | Creates a new account with the specified initial balance in HBAR. |
| `deleteAccount(Account account)` | Deletes an account and transfers the remaining balance to the operator account. |
| `deleteAccount(Account account, Account toAccount)` | Deletes an account and transfers the remaining balance to the specified account. |
| `updateAccountKey(Account account, PrivateKey updatedPrivateKey)` | Updates the account private key. |
| `updateAccountMemo(Account account, String memo)` | Updates the account memo. |
| `updateAccount(Account account, PrivateKey updatedPrivateKey, String memo)` | Updates both account key and memo. |
| `getAccountBalance(AccountId accountId)` | Retrieves the HBAR balance of the specified account. |
| `getAccountBalance(String accountId)` | Retrieves account balance using an account ID string. |
| `getOperatorAccountBalance()` | Retrieves the balance of the configured operator account. |

---

## Create Account

Creates a new Hiero account.

```java title="createAccount()"
Account account = accountClient.createAccount();

AccountId accountId = account.accountId();
```

```java title="createAccount(Hbar initialBalance)"
Account account = accountClient.createAccount(
    Hbar.from(10)
);

AccountId accountId = account.accountId();
```

---

## Delete Account
Deletes an account and transfers the remaining balance.

```java title="deleteAccount(Account account)"
Account account = accountClient.createAccount();

accountClient.deleteAccount(account);
```

!!! info
    
    If `toAccount` is not provided, the remaining account balance is transferred to the configured operator account.


```java title="deleteAccount(Account account, Account toAccount)"
Account account = accountClient.createAccount();
Account receiver = accountClient.createAccount();

accountClient.deleteAccount(account, receiver);
```

---

## Update Account

Updates account configuration such as keys and memo.

```java title="updateAccountKey(Account account, PrivateKey updatedPrivateKey)"
Account account = accountClient.createAccount();
PrivateKey newKey = PrivateKey.generateED25519();

Account updatedAccount =
    accountClient.updateAccountKey(
        account, 
        newKey
    );
```

```java title="updateAccountMemo(Account account, String memo)"
Account account = accountClient.createAccount();

Account updatedAccount = accountClient.updateAccountMemo(
    account,
    "Updated account memo"
);
```

```java title="updateAccount(Account account, PrivateKey updatedPrivateKey, String memo)"
Account account = accountClient.createAccount();
PrivateKey newKey = PrivateKey.generateED25519();

Account updatedAccount = accountClient.updateAccount(
    account,
    newKey,
    "Updated account"
);
```

---

## Get Account Balance

Retrieves account balances from the Hiero network.

```java title="getAccountBalance(AccountId accountId)"
AccountId accountId = AccountId.fromString("0.0.1234");

Hbar balance =
    accountClient.getAccountBalance(accountId);
```

```java title="getOperatorAccountBalance()"
Hbar balance =
    accountClient.getOperatorAccountBalance();
```

