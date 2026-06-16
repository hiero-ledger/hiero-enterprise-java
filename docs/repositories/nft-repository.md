# NFT Repository

NftRepository provides APIs for querying NFT information from a Hiero Mirror Node, including retrieving NFT types, searching NFTs by owner or token type, retrieving specific NFT instances, and accessing NFT metadata.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findTypesByOwner(AccountId ownerId)` | Retrieves NFT types owned by a specific account. |
| `findTypesByOwner(String ownerId)` | Retrieves NFT types owned by an account using an account ID string. |
| `findByOwner(AccountId ownerId)` | Retrieves all NFTs owned by a specific account. |
| `findByOwner(String ownerId)` | Retrieves all NFTs owned by an account using an account ID string. |
| `findByType(TokenId tokenId)` | Retrieves all NFTs belonging to a specific NFT type. |
| `findByType(String tokenId)` | Retrieves NFTs belonging to a token type using a token ID string. |
| `findByTypeAndSerial(TokenId tokenId, long serialNumber)` | Retrieves an NFT by token type and serial number. |
| `findByTypeAndSerial(String tokenId, long serialNumber)` | Retrieves an NFT using token ID string and serial number. |
| `findByOwnerAndType(AccountId ownerId, TokenId tokenId)` | Retrieves NFTs of a specific type owned by an account. |
| `findByOwnerAndType(String ownerId, String tokenId)` | Retrieves NFTs using owner and token ID strings. |
| `findByOwnerAndTypeAndSerial(AccountId owner, TokenId tokenId, long serialNumber)` | Retrieves a specific NFT owned by an account. |
| `findByOwnerAndTypeAndSerial(String owner, String tokenId, long serialNumber)` | Retrieves a specific NFT using string identifiers. |

---


## Find NFT Types By Owner

```java title="findTypesByOwner(AccountId ownerId)"
AccountId ownerId =
    AccountId.fromString("0.0.1234");

Page<NftMetadata> nftTypes =
    nftRepository.findTypesByOwner(ownerId);
```

---

## Find NFTs By Owner

```java title="findByOwner(AccountId ownerId)"
AccountId ownerId =
    AccountId.fromString("0.0.1234");

Page<Nft> nfts =
    nftRepository.findByOwner(ownerId);
```

---

## Find NFTs By Type

```java title="findByType(TokenId tokenId)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Page<Nft> nfts =
    nftRepository.findByType(tokenId);
```

---

## Find NFT By Type And Serial

```java title="findByTypeAndSerial(TokenId tokenId, long serialNumber)"
TokenId tokenId =
    TokenId.fromString("0.0.5678");

Optional<Nft> nft =
    nftRepository.findByTypeAndSerial(
        tokenId,
        1
    );
```


---

## Find NFTs By Owner And Type

```java title="findByOwnerAndType(AccountId ownerId, TokenId tokenId)"
AccountId ownerId =
    AccountId.fromString("0.0.1234");

TokenId tokenId =
    TokenId.fromString("0.0.5678");

Page<Nft> nfts =
    nftRepository.findByOwnerAndType(
        ownerId,
        tokenId
    );
```


---

## Find NFT By Owner, Type And Serial

```java title="findByOwnerAndTypeAndSerial(AccountId owner, TokenId tokenId, long serialNumber)"
AccountId owner =
    AccountId.fromString("0.0.1234");

TokenId tokenId =
    TokenId.fromString("0.0.5678");

Optional<Nft> nft =
    nftRepository.findByOwnerAndTypeAndSerial(
        owner,
        tokenId,
        1
    );
```
