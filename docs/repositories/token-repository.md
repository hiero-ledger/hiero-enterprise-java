# Token Repository

TokenRepository provides APIs for querying token information from a Hiero Mirror Node, including retrieving tokens associated with accounts, searching token details by token ID, and querying token balances.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findByAccount(AccountId accountId)` | Retrieves tokens associated with a specific account. |
| `findByAccount(String accountId)` | Retrieves tokens associated with an account using an account ID string. |
| `findById(TokenId tokenId)` | Retrieves token information using a `TokenId` object. |
| `findById(String tokenId)` | Retrieves token information using a token ID string. |
| `getBalances(TokenId tokenId)` | Retrieves account balances for a specific token. |
| `getBalances(String tokenId)` | Retrieves account balances using a token ID string. |
| `getBalancesForAccount(TokenId tokenId, AccountId accountId)` | Retrieves the balance of a specific token for an account. |
| `getBalancesForAccount(String tokenId, String accountId)` | Retrieves token balance using token and account ID strings. |

---

## Find Tokens By Account

```java title="findByAccount(AccountId accountId)"
AccountId accountId =
    AccountId.fromString("0.0.1234");

Page<Token> tokens =
    tokenRepository.findByAccount(accountId);

System.out.println(tokens);
```

---

## Find Token By ID

```java title="findById(TokenId tokenId)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Optional<TokenInfo> token =
    tokenRepository.findById(tokenId);

token.ifPresent(System.out::println);
```

---

## Get Token Balances

```java title="getBalances(TokenId tokenId)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Page<Balance> balances =
    tokenRepository.getBalances(tokenId);

System.out.println(balances);
```


---

## Get Token Balance For Account


```java title="getBalancesForAccount(TokenId tokenId, AccountId accountId)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

AccountId accountId =
    AccountId.fromString("0.0.1234");

Page<Balance> balance =
    tokenRepository.getBalancesForAccount(
        tokenId,
        accountId
    );

System.out.println(balance);
```
