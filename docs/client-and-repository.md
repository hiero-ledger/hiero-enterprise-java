# Clients & Repositories

The `hiero-enterprise-base` module provides the core managed clients and repository interfaces used to interact with Hiero services.

The module separates network operations into two categories:

- **Clients** - Perform write, update, delete operations and transactions.
- **Repositories** - Query read-only network data through mirror node services.

These interfaces are shared across framework integrations, allowing the same application code to be used with Spring Boot or MicroProfile environments.

---

## Clients

Clients provide high-level APIs for performing Hiero network operations.

| Client | Description |
|:-------|:------------|
| `AccountClient` | Account lifecycle operations including creation, updates, and transfers. |
| `FileClient` | File operations including create, update, append, and read. |
| `SmartContractClient` | Smart contract deployment and execution. |
| `TopicClient` | Hiero Consensus Service (HCS) topic and message operations. |
| `FungibleTokenClient` | Fungible token creation, minting, burning, and transfers. |
| `NftClient` | NFT creation, minting, metadata, and transfer operations. |

!!! warning
    
    Client operations require HBAR to pay transaction fees. Ensure the configured account has sufficient HBAR balance.

---

## Repositories

Repositories provide read-only access to historical and indexed Hiero network data.

They are designed for querying ledger information such as accounts, transactions, tokens, contracts, and consensus messages.

| Repository | Description |
|:-----------|:------------|
| `AccountRepository` | Queries account information, balances, and account-related data. |
| `BlockRepository` | Queries block information and consensus records. |
| `ContractRepository` | Queries smart contract metadata, bytecode, and execution details. |
| `NetworkRepository` | Queries network information and node configuration data. |
| `NftRepository` | Queries NFT metadata, ownership, and transfer history. |
| `TokenRepository` | Queries fungible token information and token relationships. |
| `TopicRepository` | Queries historical HCS topic messages and topic information. |
| `TransactionRepository` | Queries transaction records, status, timestamps, and execution details. |

---
