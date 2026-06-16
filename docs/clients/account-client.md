# Account Client

AccountClient provides APIs for managing Hiero accounts, including account creation, deletion, updates, and balance queries.

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
| `deleteAccount(Account account)` | Deletes an account and transfers remaining fees to the operator account. |
| `deleteAccount(Account account, Account toAccount)` | Deletes an account and transfers remaining fees to the specified account. |
| `updateAccountKey(Account account, PrivateKey updatedPrivateKey)` | Updates the private key of an account. |
| `updateAccountMemo(Account account, String memo)` | Updates the memo field of an account. |
| `updateAccount(Account account, PrivateKey updatedPrivateKey, String memo)` | Updates both account key and memo. |
| `getAccountBalance(AccountId accountId)` | Retrieves the balance of a specific account. |
| `getAccountBalance(String accountId)` | Retrieves account balance using an account ID string. |
| `getOperatorAccountBalance()` | Retrieves the balance of the configured operator account. |

---

## Create Account

```java title="createAccount()"
Account account = accountClient.createAccount();

System.out.println(account.getId());
```

```java title="createAccount(Hbar initialBalance)"
Account account = accountClient.createAccount(
    Hbar.from(10)
);

System.out.println(account.getId());
```

---

## Delete Account

```java title="deleteAccount(Account account)"
Account account = accountClient.createAccount();

accountClient.deleteAccount(account);
```

!!! info
    
    When `toAccount` is not provided, the remaining balance of the deleted account is transferred to the configured operator account.


```java title="deleteAccount(Account account, Account toAccount)"
Account account = accountClient.createAccount();

Account receiver = accountClient.createAccount();

accountClient.deleteAccount(
    account,
    receiver
);
```

---

## Update Account

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

accountClient.updateAccountMemo(
    account,
    "Updated account memo"
);
```

```java title="updateAccount(Account account, PrivateKey updatedPrivateKey, String memo)"
Account account = accountClient.createAccount();

PrivateKey newKey = PrivateKey.generateED25519();

Account updatedAccount =
    accountClient.updateAccount(
        account,
        newKey,
        "Updated account"
    );
```

---

## Get Account Balance

```java title="getAccountBalance(AccountId accountId)"
AccountId accountId = AccountId.fromString("0.0.1234");

Hbar balance =
    accountClient.getAccountBalance(accountId);

System.out.println(balance);
```

```java title="getOperatorAccountBalance()"
Hbar balance =
    accountClient.getOperatorAccountBalance();

System.out.println(balance);
```

