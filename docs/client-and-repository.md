# Clients & Repositories

The `hiero-enterprise-base` module provides the core managed clients and repository interfaces used to interact with Hiero services.

The module separates network operations into two categories:

- **Clients** - Perform write, update, delete operations and transactions.
- **Repositories** - Query read-only network data through mirror node services.

These interfaces are shared across framework integrations, allowing the same application code to be used with Spring Boot or MicroProfile environments.

---

## Clients

Clients provide high-level APIs for performing Hiero network operations.

| Client                                                    | Description |
|:----------------------------------------------------------|:------------|
| [`AccountClient`](clients/account-client.md)              | Account lifecycle operations including creation, updates, and transfers. |
| [`FileClient`](clients/file-client.md)                    | File operations including create, update, append, and read. |
| [`SmartContractClient`](clients/smart-contract-client.md) | Smart contract deployment and execution. |
| [`TopicClient`](clients/topic-client.md)                  | Hiero Consensus Service (HCS) topic and message operations. |
| [`FungibleTokenClient`](clients/fungible-token-client.md) | Fungible token creation, minting, burning, and transfers. |
| [`NftClient`](clients/nft-client.md)                      | NFT creation, minting, metadata, and transfer operations. |

!!! warning
    
    Client operations require HBAR to pay transaction fees. Ensure the configured account has sufficient HBAR balance.

---

## Repositories

Repositories provide read-only access to historical and indexed Hiero network data.

They are designed for querying ledger information such as accounts, transactions, tokens, contracts, and consensus messages.

| Repository                                                        | Description |
|:------------------------------------------------------------------|:------------|
| [`AccountRepository`](repositories/account-repository.md)         | Queries account information, balances, and account-related data. |
| [`BlockRepository`](repositories/block-repository.md)             | Queries block information and consensus records. |
| [`ContractRepository`](repositories/contract-repository.md)       | Queries smart contract metadata, bytecode, and execution details. |
| [`NetworkRepository`](repositories/network-repository.md)         | Queries network information and node configuration data. |
| [`NftRepository`](repositories/nft-repository.md)                 | Queries NFT metadata, ownership, and transfer history. |
| [`TokenRepository`](repositories/token-repository.md)             | Queries fungible token information and token relationships. |
| [`TopicRepository`](repositories/topic-repository.md)             | Queries historical HCS topic messages and topic information. |
| [`TransactionRepository`](repositories/transaction-repository.md) | Queries transaction records, status, timestamps, and execution details. |

---
