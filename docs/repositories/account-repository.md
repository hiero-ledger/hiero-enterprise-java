# Account Repository

AccountRepository provides APIs for querying Hiero account information from the mirror node.  
It allows applications to search for account details using an account ID.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findById(AccountId accountId)` | Retrieves account information using an `AccountId`. |
| `findById(String accountId)` | Retrieves account information using an account ID string. |

---

## Find Account By ID

```java title="findById(AccountId accountId)"
AccountId accountId =
    AccountId.fromString("0.0.1234");

Optional<AccountInfo> accountInfo =
    accountRepository.findById(accountId);
```
