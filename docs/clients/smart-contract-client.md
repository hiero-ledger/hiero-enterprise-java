# Smart Contract Client

`SmartContractClient` provides APIs for managing smart contracts on a Hiero network, including contract deployment and function execution.

!!! note

    Smart contract operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method                                                                                                                          | Description                                                                                                                                                                                                                    |
|:--------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `createContract(String fileId, ContractParam<?>... constructorParams)`                                                          | Creates a smart contract using bytecode stored in a file ID string.                                                                                                                                                            |
| `createContract(FileId fileId, ContractParam<?>... constructorParams)`                                                          | Creates a smart contract using an existing bytecode file.                                                                                                                                                                      |
| `createContract(byte[] contents, ContractParam<?>... constructorParams)`                                                        | Creates a smart contract using bytecode contents.                                                                                                                                                                              |
| `createContract(Path pathToBin, ContractParam<?>... constructorParams)`                                                         | Creates a smart contract using a bytecode file path.                                                                                                                                                                           |
| `createContract(String fileId, Hbar maxTransactionFee, int gas, ContractParam<?>... constructorParams)`                         | Creates a smart contract using bytecode stored in a file ID string with a custom maximum transaction fee and gas limit.                                                                                                        |
| `createContract(FileId fileId, Hbar maxTransactionFee, int gas, ContractParam<?>... constructorParams)`                         | Creates a smart contract using an existing bytecode file with a custom maximum transaction fee and gas limit.                                                                                                                  |
| `createContract(byte[] contents, Hbar maxTransactionFee, int gas, ContractParam<?>... constructorParams)`                       | Creates a smart contract using bytecode contents with a custom maximum transaction fee and gas limit.                                                                                                                          |
| `createContract(Path pathToBin, Hbar maxTransactionFee, int gas, ContractParam<?>... constructorParams)`                        | Creates a smart contract using a bytecode file path with a custom maximum transaction fee and gas limit.                                                                                                                       |
| `callContractFunction(String contractId, String functionName, ContractParam<?>... params)`                                      | Executes a contract function using a contract ID string.                                                                                                                                                                       |
| `callContractFunction(ContractId contractId, String functionName, ContractParam<?>... params)`                                  | Executes a contract function using a contract ID.                                                                                                                                                                              |
| `callContractFunction(String contractId, String functionName, Hbar maxTransactionFee, int gas, ContractParam<?>... params)`     | Executes a smart contract function using a contract ID string with a custom maximum transaction fee and gas limit.                                                                                                             |
| `callContractFunction(ContractId contractId, String functionName, Hbar maxTransactionFee, int gas, ContractParam<?>... params)` | Executes a smart contract function using a contract ID with a custom maximum transaction fee and gas limit.                                                                                                                    |
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

!!! info

     You can also specify a maximum transaction fee and gas when deploying a contract.

```java title="createContract(FileId fileId, Hbar maxTransactionFee, int gas, ContractParam<?>... constructorParams)"
FileId fileId =
    FileId.fromString("0.0.1234");

ContractId contractId =
    smartContractClient.createContract(
        fileId,
        Hbar.from(20),
        10_000_000,
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

!!! info

     You can optionally specify a maximum transaction fee and gas when executing a contract function.

```java title="callContractFunction(ContractId contractId, String functionName, Hbar maxTransactionFee, int gas, ContractParam<?>... params)"
ContractId contractId =
    ContractId.fromString("0.0.5678");

ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "getValue",
        Hbar.from(20),
        10_000_000,
        ContractParam.string("Hello Hiero")
    );
```

!!! warning
   
    - The maximum `gas` that can be specified for a contract execution is **15_000_000**. 
    - The `maxTransactionFee` and `gas` values determine the maximum amount you are willing to pay for the transaction.
      If the required gas exceeds the specified limit or the maximum transaction fee is too low, the transaction may fail.

!!! tip 

    See the [Contract Parameters](../utils/contract-param.md) documentation for all supported Solidity parameter types, including strings, addresses, booleans, bytes, and numeric types.

