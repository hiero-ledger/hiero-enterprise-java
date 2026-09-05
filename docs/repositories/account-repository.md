# Account Repository

AccountRepository provides APIs for querying Hiero account information from the mirror node.  
It allows applications to search for account details using an account ID.

---

## Methods

| Method                                       | Description                                                           |
|:---------------------------------------------|:----------------------------------------------------------------------|
| `findById(AccountId accountId)`              | Retrieves account information using an `AccountId`.                   |
| `findById(String accountId)`                 | Retrieves account information using an account ID string.             |
| `findCryptoAllowances(String accountId)`     | Retrieves HBAR allowances using an account ID string.                 |
| `findCryptoAllowances(AccountId accountId)`  | Retrieves HBAR allowances granted by the given account.               |
| `findTokenAllowances(String accountId)`      | Retrieves fungible token allowances using an account ID string.       |
| `findTokenAllowances(AccountId accountId)`   | Retrieves fungible token allowances granted by the given account.     |
| `findNftAllowances(String accountId)`        | Retrieves non-fungible token allowances using an account ID string.   |
| `findNftAllowances(AccountId accountId)`     | Retrieves non-fungible token allowances granted by the given account. |
| `findStakingRewards(String accountId)`       | Retrieves past staking reward payouts using an account ID string.     |
| `findStakingRewards(AccountId accountId)`    | Retrieves outstanding token airdrops sent by the given account.       |
| `findPendingAirdrops(String accountId)`      | Retrieves outstanding token airdrops sent by the given account.       |
| `findPendingAirdrops(AccountId accountId)`   | Retrieves pending token airdrops received by the given account.       |
| `findOutstandingAirdrops(String accountId)`                                            | Retrieves outstanding token airdrops using an account ID string.      |
| `findOutstandingAirdrops(AccountId accountId)`                                           | Retrieves outstanding token airdrops sent by the given account.                                                                      | 

---

## Find Account By ID

```java title="findById(AccountId accountId)"
AccountId accountId =
    AccountId.fromString("0.0.1234");

Optional<AccountInfo> accountInfo =
    accountRepository.findById(accountId);
```

---

## Find HBAR Allowances

```java title="findCryptoAllowances(AccountId accountId)"
AccountId accountId =
    AccountId.fromString("0.0.1234");

Page<CryptoAllowance> allowances =
    accountRepository.findCryptoAllowances(accountId);
```

---

## Find Fungible Token Allowances

```java title="findTokenAllowances(AccountId accountId)"
AccountId accountId = 
    AccountId.fromString("0.0.1234");

Page<TokenAllowance> allowances = 
    accountRepository.findTokenAllowances(accountId);
```

---

## Find NFT Allowances

```java title="findNftAllowances(AccountId accountId)"
AccountId accountId = 
    AccountId.fromString("0.0.1234");

Page<NftAllowance> allowances = 
    accountRepository.findNftAllowances(accountId);
```

---

## Find Staking Rewards

```java title="findStakingRewards(AccountId accountId)"
AccountId accountId = 
    AccountId.fromString("0.0.1234");

Page<StakingReward> rewards = 
    accountRepository.findStakingRewards(accountId);
```

---

## Find Pending Airdrops

```java title="findPendingAirdrops(AccountId accountId)"
AccountId accountId = 
    AccountId.fromString("0.0.1234");

Page<TokenAirdrop> airdrops = 
    accountRepository.findPendingAirdrops(accountId);
```

---

## Find Outstanding Airdrops

```java title="findOutstandingAirdrops(AccountId accountId)"
AccountId accountId = 
    AccountId.fromString("0.0.1234");

Page<TokenAirdrop> airdrops = 
    accountRepository.findOutstandingAirdrops(accountId);
```

