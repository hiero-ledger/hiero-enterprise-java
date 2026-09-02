# NFT Client

`NftClient` provides APIs for managing Hiero non-fungible tokens (NFTs), including NFT type creation, account association and dissociation, minting, burning, wiping, transferring NFTs between accounts, updating NFT metadata and types, and deleting NFT types.

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
| `createNftType(String name, String symbol, PrivateKey supplierKey, PrivateKey metadataKey)` | Creates an NFT type with a metadata key; operator is treasury. |
| `createNftType(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey, PrivateKey supplierKey, PrivateKey metadataKey)` | Creates an NFT type with custom treasury, supplier, and metadata keys. |
| `createNftType(String name, String symbol, String treasuryAccountId, String treasuryKey, String supplierKey, String metadataKey)` | Creates an NFT type with treasury, supplier, and metadata keys as strings. |
| `createNftType(String name, String symbol, Account treasuryAccount, PrivateKey supplierKey, PrivateKey metadataKey)` | Creates an NFT type using an existing treasury account with supplier and metadata keys. |
| `createNftType(String name, String symbol, PrivateKey supplierKey, PrivateKey metadataKey, PrivateKey kycKey)` | Creates an NFT type with optional metadata and KYC keys; operator is treasury. |
| `createNftType(String name, String symbol, AccountId treasuryAccountId, PrivateKey treasuryKey, PrivateKey supplierKey, PrivateKey metadataKey, PrivateKey kycKey)` | Creates an NFT type with optional metadata and KYC keys. |
| `createNftType(String name, String symbol, String treasuryAccountId, String treasuryKey, String supplierKey, String metadataKey, String kycKey)` | Creates an NFT type with optional metadata and KYC keys as strings. |
| `createNftType(String name, String symbol, Account treasuryAccount, PrivateKey supplierKey, PrivateKey metadataKey, PrivateKey kycKey)` | Creates an NFT type using an existing treasury account with optional metadata and KYC keys. |
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
| `freezeNft(TokenId tokenId, AccountId accountId)` | Freezes an account for an NFT type using the operator freeze key. |
| `freezeNft(TokenId tokenId, AccountId accountId, PrivateKey freezeKey)` | Freezes an account for an NFT type using a custom freeze key. |
| `freezeNft(String tokenId, String accountId, String freezeKey)` | Freezes an account using string identifiers. |
| `freezeNft(TokenId tokenId, Account account)` | Freezes an account using an account object. |
| `unfreezeNft(TokenId tokenId, AccountId accountId)` | Unfreezes an account for an NFT type using the operator freeze key. |
| `unfreezeNft(TokenId tokenId, AccountId accountId, PrivateKey freezeKey)` | Unfreezes an account for an NFT type using a custom freeze key. |
| `unfreezeNft(String tokenId, String accountId, String freezeKey)` | Unfreezes an account using string identifiers. |
| `unfreezeNft(TokenId tokenId, Account account)` | Unfreezes an account using an account object. |
| `grantKycNft(TokenId tokenId, AccountId accountId)` | Grants KYC for an account on an NFT type using the operator KYC key. |
| `grantKycNft(TokenId tokenId, AccountId accountId, PrivateKey kycKey)` | Grants KYC for an account on an NFT type using a custom KYC key. |
| `grantKycNft(String tokenId, String accountId, String kycKey)` | Grants KYC using string identifiers. |
| `grantKycNft(TokenId tokenId, Account account)` | Grants KYC using an account object. |
| `revokeKycNft(TokenId tokenId, AccountId accountId)` | Revokes KYC for an account on an NFT type using the operator KYC key. |
| `revokeKycNft(TokenId tokenId, AccountId accountId, PrivateKey kycKey)` | Revokes KYC for an account on an NFT type using a custom KYC key. |
| `revokeKycNft(String tokenId, String accountId, String kycKey)` | Revokes KYC using string identifiers. |
| `revokeKycNft(TokenId tokenId, Account account)` | Revokes KYC using an account object. |
| `mintNft(TokenId tokenId, byte[] metadata)` | Mints a new NFT using the operator account as supply account. |
| `mintNft(String tokenId, byte[] metadata)` | Mints a new NFT using token ID string. |
| `mintNft(TokenId tokenId, PrivateKey supplyKey, byte[] metadata)` | Mints a new NFT using a custom supply key. |
| `mintNft(String tokenId, String supplyKey, byte[] metadata)` | Mints a new NFT using string token ID and supply key. |
| `mintNfts(TokenId tokenId, byte[]... metadata)` | Mints multiple NFTs using the operator account as supply account. |
| `mintNfts(String tokenId, byte[]... metadata)` | Mints multiple NFTs using token ID string. |
| `mintNfts(TokenId tokenId, PrivateKey supplyKey, byte[]... metadata)` | Mints multiple NFTs using a custom supply key. |
| `mintNfts(String tokenId, String supplyKey, byte[]... metadata)` | Mints multiple NFTs using string token ID and supply key. |
| `burnNft(TokenId tokenId, long serialNumber)` | Burns a single NFT using the operator supply account. Returns total supply. |
| `burnNft(TokenId tokenId, long serialNumber, PrivateKey supplyKey)` | Burns a single NFT using a custom supply key. Returns total supply. |
| `burnNfts(TokenId tokenId, Set<Long> serialNumbers)` | Burns multiple NFTs using the operator supply account. Returns total supply. |
| `burnNfts(TokenId tokenId, Set<Long> serialNumbers, PrivateKey supplyKey)` | Burns multiple NFTs using a custom supply key. Returns total supply. |
| `wipeNft(TokenId tokenId, long serialNumber, AccountId accountId)` | Wipes a single NFT from an account using the operator wipe key. Returns total supply. |
| `wipeNft(TokenId tokenId, long serialNumber, AccountId accountId, PrivateKey wipeKey)` | Wipes a single NFT using a custom wipe key. Returns total supply. |
| `wipeNfts(TokenId tokenId, Set<Long> serialNumbers, AccountId accountId)` | Wipes multiple NFTs from an account using the operator wipe key. Returns total supply. |
| `wipeNfts(TokenId tokenId, Set<Long> serialNumbers, AccountId accountId, PrivateKey wipeKey)` | Wipes multiple NFTs using a custom wipe key. Returns total supply. |
| `transferNft(TokenId tokenId, long serialNumber, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)` | Transfers an NFT between accounts. |
| `transferNft(TokenId tokenId, long serialNumber, Account fromAccount, AccountId toAccountId)` | Transfers an NFT using an account object as sender. |
| `transferNfts(TokenId tokenId, List<Long> serialNumbers, AccountId fromAccountId, PrivateKey fromAccountKey, AccountId toAccountId)` | Transfers multiple NFTs between accounts. |
| `transferNfts(TokenId tokenId, List<Long> serialNumbers, Account fromAccount, AccountId toAccountId)` | Transfers multiple NFTs using an account object as sender. |
| `updateNftType(TokenId tokenId, String name, String symbol)` | Updates an NFT type name and symbol using the operator account as admin key. |
| `updateNftType(TokenId tokenId, String name, String symbol, PrivateKey adminKey)` | Updates an NFT type name and symbol using a custom admin key. |
| `updateNftType(String tokenId, String name, String symbol)` | Updates an NFT type using a token ID string and the operator admin key. |
| `updateNftType(String tokenId, String name, String symbol, String adminKey)` | Updates an NFT type using string token ID and admin key. |
| `updateNftMetadata(TokenId tokenId, long serialNumber, byte[] metadata)` | Updates metadata for a single NFT using the operator key. |
| `updateNftMetadata(TokenId tokenId, long serialNumber, PrivateKey metadataKey, byte[] metadata)` | Updates metadata for a single NFT using a custom metadata (or supply) key. |
| `updateNftsMetadata(TokenId tokenId, List<Long> serialNumbers, byte[] metadata)` | Updates metadata for up to 10 NFT serials using the operator key. |
| `updateNftsMetadata(TokenId tokenId, List<Long> serialNumbers, PrivateKey metadataKey, byte[] metadata)` | Updates metadata for up to 10 NFT serials using a custom metadata (or supply) key. |
| `updateNftsMetadata(String tokenId, List<Long> serialNumbers, String metadataKey, byte[] metadata)` | Updates NFT metadata using string token ID and key. |
| `deleteNftType(TokenId tokenId)` | Deletes an NFT type using the operator account as admin key. |
| `deleteNftType(TokenId tokenId, PrivateKey adminKey)` | Deletes an NFT type using a custom admin key. |
| `deleteNftType(String tokenId)` | Deletes an NFT type using a token ID string and the operator admin key. |
| `deleteNftType(String tokenId, String adminKey)` | Deletes an NFT type using string token ID and admin key. |

---

## Create NFT Type

Creates a new NFT token type that can later be minted into individual NFTs.

```java title="createNftType(String name, String symbol)"
TokenId tokenId = nftClient.createNftType(
    "Demo NFT",
    "DNFT"
);
```

```java title="createNftType(String name, String symbol, PrivateKey supplierKey)"
PrivateKey supplierKey = PrivateKey.generateED25519();

TokenId tokenId = nftClient.createNftType(
    "Custom NFT",
    "CNFT",
    supplierKey
);
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

```java title="createNftType(String name, String symbol, PrivateKey supplierKey, PrivateKey metadataKey)"
PrivateKey supplierKey = PrivateKey.generateED25519();
PrivateKey metadataKey = PrivateKey.generateED25519();

TokenId tokenId = nftClient.createNftType(
    "Metadata NFT",
    "MNFT",
    supplierKey,
    metadataKey
);
```

!!! info

    Set a metadata key at creation if you need to update NFT serial metadata after the NFT leaves the treasury. While serials remain in the treasury, the supply key can also update metadata ([HIP-850](https://hips.hedera.com/hip/hip-850)).

---

## Associate NFT

Associates an account with one or more NFT token types before receiving NFTs.

```java title="associateNft(TokenId tokenId, AccountId accountId,PrivateKey accountKey)" 
TokenId tokenId =
    TokenId.fromString("0.0.5000");

AccountId accountIdToAssociateWith =
    AccountId.fromString("0.0.1001");

nftClient.associateNft(
    tokenId,
    accountIdToAssociateWith,
    accountKeyToAssociateWith
    );
```

```java title="associateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
List<TokenId> tokenIds =
    List.of(
        TokenId.fromString("0.0.5000"),
        TokenId.fromString("0.0.5001")
    );

AccountId accountIdToAssociateWith =
    AccountId.fromString("0.0.1001");

nftClient.associateNft(
    tokenIds,
    accountIdToAssociateWith,
    accountKeyToAssociateWith
    );
```

---

## Dissociate NFT

Removes one or more NFT token associations from an account.

```java title="dissociateNft(TokenId tokenId, AccountId accountId, PrivateKey accountKey)" 
TokenId tokenId =
    TokenId.fromString("0.0.5000");

AccountId accountIdToDissociateFrom =
    AccountId.fromString("0.0.1001");

nftClient.dissociateNft(
    tokenId,
    accountIdToDissociateFrom,
    accountKeyToDissociateFrom
    );
```

```java title="dissociateNft(List<TokenId> tokenIds, AccountId accountId, PrivateKey accountKey)"
List<TokenId> tokenIds =
    List.of(
        TokenId.fromString("0.0.5000"),
        TokenId.fromString("0.0.5001")
    );

AccountId accountIdToDissociateFrom =
    AccountId.fromString("0.0.1001");

nftClient.dissociateNft(
    tokenIds,
    accountIdToDissociateFrom,
    accountKeyToDissociateFrom
    );
```

---

## Freeze / Unfreeze NFT Account

Freezes or unfreezes an account for an NFT type. A frozen account cannot send or receive NFTs of that type until it is unfrozen. The token must have been created with a freeze key.

```java title="freezeNft(TokenId tokenId, AccountId accountId)"
TokenId tokenId = TokenId.fromString("0.0.5000");
AccountId accountId = AccountId.fromString("0.0.1001");

nftClient.freezeNft(tokenId, accountId);
```

```java title="unfreezeNft(TokenId tokenId, AccountId accountId, PrivateKey freezeKey)"
TokenId tokenId = TokenId.fromString("0.0.5000");
AccountId accountId = AccountId.fromString("0.0.1001");
PrivateKey freezeKey = PrivateKey.generateED25519();

nftClient.unfreezeNft(tokenId, accountId, freezeKey);
```

:::note
The operator account key is used as the freeze key when no custom key is provided. The NFT type must have a freeze key set at creation time for freeze and unfreeze to succeed.
:::

---

## Grant / Revoke KYC for NFT Account

Grants or revokes KYC for an account on an NFT type. When a token has a KYC key, accounts must be KYC-granted before they can receive that token. The token must have been created with a KYC key.

```java title="grantKycNft(TokenId tokenId, AccountId accountId)"
TokenId tokenId = TokenId.fromString("0.0.5000");
AccountId accountId = AccountId.fromString("0.0.1001");

nftClient.grantKycNft(tokenId, accountId);
```

```java title="revokeKycNft(TokenId tokenId, AccountId accountId, PrivateKey kycKey)"
TokenId tokenId = TokenId.fromString("0.0.5000");
AccountId accountId = AccountId.fromString("0.0.1001");
PrivateKey kycKey = PrivateKey.generateED25519();

nftClient.revokeKycNft(tokenId, accountId, kycKey);
```

:::note
The operator account key is used as the KYC key when no custom key is provided. The NFT type must have a KYC key set at creation time for grant and revoke KYC to succeed.
:::

---

## Mint NFT

Creates one or more NFT instances for an NFT token type.

```java title="mintNft(TokenId tokenId, byte[] metadata)" 
TokenId tokenId =
        TokenId.fromString("0.0.5000");

byte[] metadata =
        "NFT metadata".getBytes();

long serialNumber =
        nftClient.mintNft(
                tokenId,
                metadata
        );
```


```java title="mintNfts(TokenId tokenId, byte[]... metadata)" 
TokenId tokenId =
        TokenId.fromString("0.0.5000");

List<Long> serialNumbers =
        nftClient.mintNfts(
                tokenId,
                "NFT One".getBytes(),
                "NFT Two".getBytes()
        );
```


```java title="mintNfts(TokenId tokenId, PrivateKey supplyKey, byte[]... metadata)" 
TokenId tokenId =
        TokenId.fromString("0.0.5000");

List<Long> serialNumbers =
        nftClient.mintNfts(
                tokenId,
                supplyKey,
                "NFT One".getBytes(),
                "NFT Two".getBytes()
        );
```

!!! info

    Provide `supplyKey` when the NFT type was created with a custom supply key that is different from the configured operator account key.


---

## Burn NFT

Permanently removes one or more NFTs from circulation. Returns the total supply of the NFT type after the burn.

```java title="burnNft(TokenId tokenId, long serialNumber)"
TokenId tokenId =
        TokenId.fromString("0.0.5000");

long totalSupply = nftClient.burnNft(
        tokenId,
        1L
);
```

```java title="burnNfts(TokenId tokenId, Set<Long> serialNumbers)"
TokenId tokenId =
        TokenId.fromString("0.0.5000");

Set<Long> serialNumbers =
        Set.of(1L, 2L, 3L);

long totalSupply = nftClient.burnNfts(
        tokenId,
        serialNumbers
);
```

```java title="burnNfts(TokenId tokenId, Set<Long> serialNumbers, PrivateKey supplyKey)"
TokenId tokenId =
        TokenId.fromString("0.0.5000");

Set<Long> serialNumbers =
        Set.of(1L, 2L, 3L);

long totalSupply = nftClient.burnNfts(
        tokenId,
        serialNumbers,
        supplyKey
);
```


!!! info

    Provide `supplyKey` when the NFT type was created with a custom supply key that is different from the configured operator account key.


---

## Wipe NFT

Wipes one or more NFTs from a non-treasury account. The wipe key must sign. Wiping removes the NFT from the account and decreases total supply. Returns the total supply of the NFT type after the wipe. The treasury account cannot be wiped.

```java title="wipeNft(TokenId tokenId, long serialNumber, AccountId accountId)"
AccountId holder = AccountId.fromString("0.0.1001");

long totalSupply = nftClient.wipeNft(
        tokenId,
        1L,
        holder
);
```

```java title="wipeNfts(TokenId tokenId, Set<Long> serialNumbers, AccountId accountId, PrivateKey wipeKey)"
Set<Long> serialNumbers = Set.of(1L, 2L, 3L);
PrivateKey wipeKey = PrivateKey.generateED25519();

long totalSupply = nftClient.wipeNfts(
        tokenId,
        serialNumbers,
        holder,
        wipeKey
);
```

!!! info

    Newly created NFT types use the admin (treasury) key as the wipe key when no wipe key is set explicitly, so the operator key can wipe when the operator is also the treasury/admin.

---

## Transfer NFT


Transfers ownership of one or more NFTs between accounts.

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

---

## Update NFT Type

Updates properties of an NFT type (token class), such as name and symbol. Requires the admin key set at creation (by default the treasury key). Fields that are not changed are left as-is on the network.

```java title="updateNftType(TokenId tokenId, String name, String symbol)"
TokenId tokenId = nftClient.createNftType("Demo NFT", "DNFT");

nftClient.updateNftType(tokenId, "Updated Demo NFT", "UDNFT");
```

```java title="updateNftType(TokenId tokenId, String name, String symbol, PrivateKey adminKey)"
PrivateKey adminKey = PrivateKey.generateED25519();

nftClient.updateNftType(tokenId, "Updated Demo NFT", "UDNFT", adminKey);
```

!!! info

    Provide `adminKey` when the NFT type was created with a custom treasury whose key differs from the configured operator account key.

---

## Update NFT Metadata

Updates the metadata of one or more NFT serials (at most 10 per call). Metadata is limited to 100 bytes. Requires the token metadata key, or the supply key while the NFT is still held by the treasury ([HIP-850](https://hips.hedera.com/hip/hip-850)).

```java title="updateNftMetadata(TokenId tokenId, long serialNumber, byte[] metadata)"
TokenId tokenId = nftClient.createNftType("Demo NFT", "DNFT");
long serial = nftClient.mintNft(tokenId, "https://example.com/old".getBytes());

nftClient.updateNftMetadata(
        tokenId,
        serial,
    "https://example.com/new".getBytes()
);
```

```java title="updateNftsMetadata(TokenId tokenId, List<Long> serialNumbers, PrivateKey metadataKey, byte[] metadata)"
PrivateKey metadataKey = PrivateKey.generateED25519();
PrivateKey supplierKey = PrivateKey.generateED25519();
TokenId tokenId = nftClient.createNftType("Demo NFT", "DNFT", supplierKey, metadataKey);
List<Long> serials = nftClient.mintNfts(tokenId, supplierKey, "https://example.com/old".getBytes());

nftClient.updateNftsMetadata(
        tokenId,
        serials,
        metadataKey,
    "https://example.com/new".getBytes()
);
```

!!! info

    For NFT types created with the default operator supply/treasury keys, the operator key can update metadata while serials remain in the treasury. After transfer out of treasury, sign with the metadata key set at token creation.

---

## Delete NFT Type

Deletes an NFT type (token class). You cannot delete a specific NFT serial; burn all serials first, then delete the type. Requires the admin key set at creation (by default the treasury key).

```java title="deleteNftType(TokenId tokenId)"
TokenId tokenId = nftClient.createNftType("Demo NFT", "DNFT");

nftClient.deleteNftType(tokenId);
```

```java title="deleteNftType(TokenId tokenId, PrivateKey adminKey)"
PrivateKey adminKey = PrivateKey.generateED25519();

nftClient.deleteNftType(tokenId, adminKey);
```

!!! info

    Provide `adminKey` when the NFT type was created with a custom treasury whose key differs from the configured operator account key.
