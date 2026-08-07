# Contract Repository

ContractRepository provides APIs for querying smart contract information from a Hiero Mirror Node, 
including retrieving all contracts and searching contracts by contract ID.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findAll()` | Retrieves a paginated list of contracts from the Mirror Node. |
| `findById(ContractId contractId)` | Retrieves a contract using a `ContractId` object. |
| `findById(String contractId)` | Retrieves a contract using a contract ID string. |

---

## Find All Contracts

```java title="findAll()"
Page<Contract> contracts =
    contractRepository.findAll();
```

---

## Find Contract By ID

```java title="findById(ContractId contractId)"
ContractId contractId =
    ContractId.fromString("0.0.1234");

Optional<Contract> contract =
    contractRepository.findById(contractId);
```