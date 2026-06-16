# Fungible Token Client

`FungibleTokenClient` provides APIs for managing Hiero fungible tokens, including token creation, account association, dissociation, minting, burning, and token transfers.

!!! note

    Fungible token operations that submit transactions to the Hiero network require HBAR to pay transaction fees.  
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createToken(String name, String symbol)` | Creates a new token using the operator account as treasury and supply account. |
| `createToken(String name, String symbol, PrivateKey supplyKey)` | Creates a new token using a custom supply key with the operator account as treasury. |
| `createToken(String name, String symbol, String supplyKey)` | Creates a new token using a supply key provided as a string. |
| `createToken(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey)` | Creates a token using a custom treasury account. |
| `createToken(String name, String symbol, String treasuryAccountId, String treasuryKey)` | Creates a token using treasury account ID and key provided as strings. |
| `createToken(String name, String symbol, Account treasuryAccount)` | Creates a token using an existing treasury account. |
| `createToken(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey, PrivateKey supplyKey)` | Creates a token with custom treasury and supply keys. |
| `createToken(String name, String symbol, String treasuryAccountId, String treasuryKey, String supplyKey)` | Creates a token with treasury and supply credentials provided as strings. |
| `createToken(String name, String symbol, Account treasuryAccount, PrivateKey supplyKey)` | Creates a token using an existing treasury account and custom supply key. |
| `createToken(String name, String symbol, Account treasuryAccount, String supplyKey)` | Creates a token using an existing treasury account and supply key string. |
| `associateToken(TokenId tokenId, AccountId accountId, PrivateKey accountKey)` | Associates an account with a token. |
| `associateToken(TokenId tokenId, String accountId, String accountKey)` | Associates an account with a token using account ID and key strings. |
| `associateToken(TokenId tokenId, Account account)` | Associates an existing account with a token. |
| `associateToken(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)` | Associates an account with multiple tokens. |
| `associateToken(List<TokenId> tokenIds, Account account)` | Associates an account with multiple tokens using an account object. |
| `dissociateToken(TokenId tokenId, AccountId accountId, PrivateKey accountKey)` | Removes a token association from an account. |
| `dissociateToken(String tokenId, String accountId, String accountKey)` | Removes a token association using string-based credentials. |
| `dissociateToken(TokenId tokenId, Account account)` | Removes a token association using an account object. |
| `dissociateToken(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)` | Removes multiple token associations from an account. |
| `dissociateToken(List<TokenId> tokenIds, Account account)` | Removes multiple token associations using an account object. |
| `mintToken(TokenId tokenId, long amount)` | Mints tokens using the operator supply account. |
| `mintToken(String tokenId, long amount)` | Mints tokens using a token ID string. |
| `mintToken(TokenId tokenId, PrivateKey supplyKey, long amount)` | Mints tokens using a custom supply key. |
| `mintToken(String tokenId, String supplyKey, long amount)` | Mints tokens using string-based token and supply key values. |
| `burnToken(TokenId tokenId, long amount)` | Burns tokens using the operator supply account. |
| `burnToken(TokenId tokenId, long amount, PrivateKey supplyKey)` | Burns tokens using a custom supply key. |
| `burnToken(TokenId tokenId, long amount, String supplyKey)` | Burns tokens using a supply key provided as a string. |
| `transferToken(TokenId tokenId, AccountId toAccountId, long amount)` | Transfers tokens from the operator account to another account. |
| `transferToken(TokenId tokenId, String toAccountId, long amount)` | Transfers tokens using a receiver account ID string. |
| `transferToken(TokenId tokenId, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId, long amount)` | Transfers tokens between accounts using sender credentials. |
| `transferToken(TokenId tokenId, Account fromAccount, AccountId toAccountId, long amount)` | Transfers tokens using an existing sender account object. |
| `transferToken(TokenId tokenId, String fromAccountId, String fromAccountKey, String toAccountId, long amount)` | Transfers tokens between accounts using string-based credentials. |

---

## Create Token

Creates a token using the configured operator account as both treasury and supply account.

```java title="createToken(String name, String symbol)"
TokenId tokenId =
    fungibleTokenClient.createToken(
        "My Token",
        "MTK"
    );
```

```java title="createToken(String name, String symbol, PrivateKey supplyKey)"
PrivateKey supplyKey =
    PrivateKey.generateED25519();

TokenId tokenId =
    fungibleTokenClient.createToken(
        "My Token",
        "MTK",
        supplyKey
    );
```

```java title="createToken(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey)"
AccountId treasuryAccountId =
    AccountId.fromString("0.0.1234");

PrivateKey treasuryAccountKey =
    PrivateKey.generateED25519();

TokenId tokenId =
    fungibleTokenClient.createToken(
        "My Token",
        "MTK",
        treasuryAccountId,
        treasuryAccountKey
    );
```

```java title="createToken(String name, String symbol, Account treasuryAccount)"
Account treasuryAccount =
    accountClient.createAccount();

TokenId tokenId =
    fungibleTokenClient.createToken(
        "My Token",
        "MTK",
        treasuryAccount
    );
```

```java title="createToken(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey, PrivateKey supplyKey)"
AccountId treasuryAccountId =
    AccountId.fromString("0.0.1234");

PrivateKey treasuryAccountKey =
    PrivateKey.generateED25519();

PrivateKey supplyKey =
    PrivateKey.generateED25519();

TokenId tokenId =
    fungibleTokenClient.createToken(
        "My Token",
        "MTK",
        treasuryAccountId,
        treasuryAccountKey,
        supplyKey
    );
```

---

## Associate Token

Accounts must be associated with a token before they can receive or hold it.

```java title="associateToken(TokenId tokenId, AccountId accountId, PrivateKey accountKey)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

AccountId accountIdToAssociateWith =
    AccountId.fromString("0.0.1234");

fungibleTokenClient.associateToken(
    tokenId,
    accountIdToAssociateWith,
    accountKeyToAssociateWith
);
```

```java title="associateToken(TokenId tokenId, Account account)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Account accountToAssociateWith =
    accountClient.createAccount();

fungibleTokenClient.associateToken(
    tokenId,
    accountToAssociateWith
);
```

```java title="associateToken(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
List<TokenId> tokens =
    List.of(
        TokenId.fromString("0.0.5678"),
        TokenId.fromString("0.0.5679")
    );

AccountId accountIdToAssociateWith =
    AccountId.fromString("0.0.1234");


fungibleTokenClient.associateToken(
    tokens,
    accountIdToAssociateWith,
    accountKeyToAssociateWith
);
```

---

## Dissociate Token

Removes token associations from an account.

```java title="dissociateToken(TokenId tokenId, AccountId accountId, PrivateKey accountKey)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

AccountId accountIdToDissociate =
    AccountId.fromString("0.0.1234");


fungibleTokenClient.dissociateToken(
    tokenId,
    accountIdToDissociate,
    accountKeyToDissociate
);
```

```java title="dissociateToken(TokenId tokenId, Account account)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Account accountToDissociate =
    accountClient.createAccount();

fungibleTokenClient.dissociateToken(
    tokenId,
    accountToDissociate
);
```

```java title="dissociateToken(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
List<TokenId> tokens =
    List.of(
        TokenId.fromString("0.0.5678"),
        TokenId.fromString("0.0.5679")
    );

AccountId accountIdToDissociate =
    AccountId.fromString("0.0.1234");

fungibleTokenClient.dissociateToken(
    tokens,
    accountIdToDissociate,
    accountKeyToDissociate
    );
```

---

## Mint Token

Creates additional token supply.

```java title="mintToken(TokenId tokenId, long amount)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

long newSupply =
    fungibleTokenClient.mintToken(
        tokenId,
        1000
    );
```

```java title="mintToken(TokenId tokenId, PrivateKey supplyKey, long amount)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

fungibleTokenClient.mintToken(
    tokenId,
    supplyKey,
    1000
);
```

!!! info
    
    Provide `supplyKey` when the token was created with a custom supply key that differs from the configured operator account key.

---

## Burn Token

Removes tokens from circulation.

```java title="burnToken(TokenId tokenId, long amount)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

fungibleTokenClient.burnToken(
    tokenId,
    100
);
```

```java title="burnToken(TokenId tokenId, long amount, PrivateKey supplyKey)" 


TokenId tokenId =
    TokenId.fromString("0.0.5678");

fungibleTokenClient.burnToken(
    tokenId,
    100,
    supplyKey
);
```

!!! info

    Provide `supplyKey` when the token was created with a custom supply key that differs from the configured operator account key.

---

## Transfer Token

Transfer token from one account to other

```java title="transferToken(TokenId tokenId, AccountId toAccountId, long amount)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");
AccountId receiver =
    AccountId.fromString("0.0.4321");

fungibleTokenClient.transferToken(
    tokenId,
    receiver,
    100
);
```

```java title="transferToken(TokenId tokenId, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId, long amount)"
fungibleTokenClient.transferToken(
    tokenId,
    senderId,
    senderKey,
    receiverId,
    100
);
```

```java title="transferToken(TokenId tokenId, Account fromAccount, AccountId toAccountId, long amount)"
Account sender =
    accountClient.createAccount();

fungibleTokenClient.transferToken(
    tokenId,
    sender,
    receiverId,
    100
);
```