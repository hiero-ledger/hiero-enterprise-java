# Smart Contract Client

`SmartContractClient` provides APIs for managing smart contracts on a Hiero network, including contract deployment and function execution.

!!! note

    Smart contract operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createContract(String fileId, ContractParam<?>... constructorParams)` | Creates a smart contract using bytecode stored in a file ID string. |
| `createContract(FileId fileId, ContractParam<?>... constructorParams)` | Creates a smart contract using an existing bytecode file. |
| `createContract(byte[] contents, ContractParam<?>... constructorParams)` | Creates a smart contract using bytecode contents. |
| `createContract(Path pathToBin, ContractParam<?>... constructorParams)` | Creates a smart contract using a bytecode file path. |
| `callContractFunction(String contractId, String functionName, ContractParam<?>... params)` | Executes a contract function using a contract ID string. |
| `callContractFunction(ContractId contractId, String functionName, ContractParam<?>... params)` | Executes a contract function using a contract ID. |

---

## Create Contract

Deploys a smart contract to the Hiero network.

```java title="createContract(FileId fileId, ContractParam<?>... constructorParams)"
FileId fileId =
    FileId.fromString("0.0.1234");


ContractId contractId =
    smartContractClient.createContract(
        fileId,
        ContractParam.string("Hello Hiero")
    );
```

```java title="createContract(byte[] contents, ContractParam<?>... constructorParams)"
byte[] bytecode =
    Files.readAllBytes(
        Path.of("contract.bin")
    );

ContractId contractId =
    smartContractClient.createContract(
        bytecode,
        ContractParam.string("Hello Hiero")
    );
```

```java title="createContract(Path pathToBin, ContractParam<?>... constructorParams)"
ContractId contractId =
    smartContractClient.createContract(
        Path.of("contract.bin"),
        ContractParam.string("Hello Hiero")
    );
```

---

## Call Contract Function

Executes a function on an existing smart contract.

```java title="callContractFunction(ContractId contractId, String functionName, ContractParam<?>... params)"
ContractId contractId =
    ContractId.fromString("0.0.5678");

ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "getValue",
        ContractParam.string("Hello Hiero")
    );
```

!!! tip 

    See the [Contract Parameters](../utils/contract-param.md) documentation for all supported Solidity parameter types, including strings, addresses, booleans, bytes, and numeric types.

