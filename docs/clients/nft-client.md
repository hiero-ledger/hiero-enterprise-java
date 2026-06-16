# NFT Client

`NftClient` provides APIs for managing Hiero non-fungible tokens (NFTs), including NFT type creation, account association and dissociation, minting, burning, and transferring NFTs between accounts.

!!! note

    NFT operations that submit transactions to the Hiero network require HBAR to pay transaction fees.  
    The configured operator account is used as the transaction payer and must have a sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createNftType(String name, String symbol)` | Creates a new NFT type using the operator account as supplier and treasury account. |
| `createNftType(String name, String symbol, PrivateKey supplierKey)` | Creates a new NFT type using a custom supplier key and operator account as treasury. |
| `createNftType(String name, String symbol, String supplierKey)` | Creates a new NFT type using a supplier key string. |
| `createNftType(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey)` | Creates an NFT type with a custom treasury account. |
| `createNftType(String name, String symbol, String treasuryAccountId, String treasuryKey)` | Creates an NFT type using treasury account ID and key strings. |
| `createNftType(String name, String symbol, Account treasuryAccount)` | Creates an NFT type using an existing treasury account. |
| `createNftType(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey, PrivateKey supplierKey)` | Creates an NFT type with custom treasury and supplier accounts. |
| `createNftType(String name, String symbol, String treasuryAccountId, String treasuryKey, String supplierKey)` | Creates an NFT type with treasury and supplier keys as strings. |
| `createNftType(String name, String symbol, Account treasuryAccount, PrivateKey supplierKey)` | Creates an NFT type using an existing treasury account and custom supplier key. |
| `associateNft(TokenId tokenId, AccountId accountId, PrivateKey accountKey)` | Associates an account with an NFT type. |
| `associateNft(String tokenId, String accountId, String accountKey)` | Associates an account with an NFT type using string identifiers. |
| `associateNft(TokenId tokenId, Account account)` | Associates an account object with an NFT type. |
| `associateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)` | Associates an account with multiple NFT types. |
| `associateNft(List<TokenId> tokenIds, Account account)` | Associates an account object with multiple NFT types. |
| `dissociateNft(TokenId tokenId, AccountId accountId, PrivateKey accountKey)` | Removes an NFT type association from an account. |
| `dissociateNft(String tokenId, String accountId, String accountKey)` | Removes an NFT association using string identifiers. |
| `dissociateNft(TokenId tokenId, Account account)` | Removes an NFT association using an account object. |
| `dissociateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)` | Removes multiple NFT type associations from an account. |
| `dissociateNft(List<TokenId> tokenIds, Account account)` | Removes multiple NFT associations using an account object. |
| `mintNft(TokenId tokenId, byte[] metadata)` | Mints a new NFT using the operator account as supply account. |
| `mintNft(String tokenId, byte[] metadata)` | Mints a new NFT using token ID string. |
| `mintNft(TokenId tokenId, PrivateKey supplyKey, byte[] metadata)` | Mints a new NFT using a custom supply key. |
| `mintNft(String tokenId, String supplyKey, byte[] metadata)` | Mints a new NFT using string token ID and supply key. |
| `mintNfts(TokenId tokenId, byte[]... metadata)` | Mints multiple NFTs using the operator account as supply account. |
| `mintNfts(String tokenId, byte[]... metadata)` | Mints multiple NFTs using token ID string. |
| `mintNfts(TokenId tokenId, PrivateKey supplyKey, byte[]... metadata)` | Mints multiple NFTs using a custom supply key. |
| `mintNfts(String tokenId, String supplyKey, byte[]... metadata)` | Mints multiple NFTs using string token ID and supply key. |
| `burnNft(TokenId tokenId, long serialNumber)` | Burns a single NFT using the operator supply account. |
| `burnNft(TokenId tokenId, long serialNumber, PrivateKey supplyKey)` | Burns a single NFT using a custom supply key. |
| `burnNfts(TokenId tokenId, Set<Long> serialNumbers)` | Burns multiple NFTs using the operator supply account. |
| `burnNfts(TokenId tokenId, Set<Long> serialNumbers, PrivateKey supplyKey)` | Burns multiple NFTs using a custom supply key. |
| `transferNft(TokenId tokenId, long serialNumber, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)` | Transfers an NFT between accounts. |
| `transferNft(TokenId tokenId, long serialNumber, Account fromAccount, AccountId toAccountId)` | Transfers an NFT using an account object as sender. |
| `transferNfts(TokenId tokenId, List<Long> serialNumbers, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)` | Transfers multiple NFTs between accounts. |
| `transferNfts(TokenId tokenId, List<Long> serialNumbers, Account fromAccount, AccountId toAccountId)` | Transfers multiple NFTs using an account object as sender. |

---

## Create NFT Type

```java title="createNftType(String name, String symbol)"
TokenId tokenId = nftClient.createNftType("Demo NFT", "DNFT");
```

```java title="createNftType(String name, String symbol, PrivateKey supplierKey)"
PrivateKey supplierKey = PrivateKey.generateED25519();

TokenId tokenId = nftClient.createNftType( "Custom NFT", "CNFT", supplierKey );
```

```java title="createNftType(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey)"
AccountId treasuryId = AccountId.fromString("0.0.1234");
PrivateKey treasuryKey = PrivateKey.generateED25519();

TokenId tokenId = 
    nftClient.createNftType(
        "Treasury NFT", 
        "TNFT", 
        treasuryId, 
        treasuryKey
    );
```

---

## Associate NFT

```java title="associateNft(TokenId tokenId, AccountId accountId,PrivateKey accountKey)" 
TokenId tokenId = TokenId.fromString("0.0.5000");
AccountId accountId = AccountId.fromString("0.0.1001");

nftClient.associateNft(tokenId, accountId, accountKey);
```

```java title="associateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
List<TokenId> tokenIds = List.of(
    TokenId.fromString("0.0.5000"),
    TokenId.fromString("0.0.5001")
);

nftClient.associateNft(
    tokenIds,
    accountId,
    accountKey
);
```

---

## Dissociate NFT

```java title="dissociateNft(TokenId tokenId, AccountId accountId, PrivateKey accountKey)" 
nftClient.dissociateNft(
  tokenId,
  accountId,
  accountKey
  );
```

```java title="dissociateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
nftClient.dissociateNft(
    tokenIds, 
    accountId, 
    accountKey
    );
```

---

## Mint NFT

```java title="mintNft(TokenId tokenId, byte[] metadata)" 
byte[] metadata = "NFT metadata".getBytes();

nftClient.mintNft(tokenId, metadata);
```


```java title="mintNfts(TokenId tokenId, byte[]... metadata)" 
nftClient.mintNfts(tokenId, "NFT One".getBytes(), "NFT Two".getBytes());
```

---

## Burn NFT

```java title="burnNft(TokenId tokenId, long serialNumber)"
nftClient.burnNft(tokenId, 1L);
```

```java title="burnNfts(TokenId tokenId, Set<Long> serialNumbers)"
Set<Long> serialNumbers = Set.of(1L, 2L, 3L);
nftClient.burnNfts(tokenId, serialNumbers);
```

---

## Transfer NFT

```java title="transferNft(TokenId tokenId, long serialNumber, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)"
AccountId sender = AccountId.fromString("0.0.1001");
AccountId receiver = AccountId.fromString("0.0.1002");

nftClient.transferNft(
    tokenId,
    1L,
    sender,
    PrivateKey.generateED25519(),
    receiver
);
```

```java title="transferNfts(TokenId tokenId, List<Long>serialNumbers, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)"
List<Long> serialNumbers = List.of(1L,2L);

nftClient.transferNfts(
    tokenId,
    serialNumbers,
    sender, 
    PrivateKey.generateED25519(),
    receiver
);
```
