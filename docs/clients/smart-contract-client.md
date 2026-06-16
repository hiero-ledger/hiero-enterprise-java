# Smart Contract Client

SmartContractClient provides APIs for managing smart contracts on a Hiero network, including contract creation and contract function execution.

!!! note

    Smart contract operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createContract(String fileId, ContractParam<?>... constructorParams)` | Creates a smart contract using bytecode stored in a file. |
| `createContract(FileId fileId, ContractParam<?>... constructorParams)` | Creates a smart contract using a bytecode file ID. |
| `createContract(byte[] contents, ContractParam<?>... constructorParams)` | Creates a smart contract using contract bytecode contents. |
| `createContract(Path pathToBin, ContractParam<?>... constructorParams)` | Creates a smart contract using a bytecode file path. |
| `callContractFunction(String contractId, String functionName, ContractParam<?>... params)` | Executes a contract function using a contract ID string. |
| `callContractFunction(ContractId contractId, String functionName, ContractParam<?>... params)` | Executes a contract function using a contract ID. |

---

## Create Contract

```java title="createContract(FileId fileId, ContractParam<?>... constructorParams)"
ContractParam<String> param = ContractParam.of("value", StringBasedDatatype.STRING);
FileId fileId = FileId.fromString("0.0.1234");

ContractId contractId =
    smartContractClient.createContract(fileId, param);
```

```java title="createContract(byte[] contents, ContractParam<?>... constructorParams)"
ContractParam<String> param = ContractParam.of("value", StringBasedDatatype.STRING);
byte[] bytecode = Files.readAllBytes(
    Path.of("contract.bin")
);

ContractId contractId =
    smartContractClient.createContract(bytecode, param);
```

```java title="createContract(Path pathToBin, ContractParam<?>... constructorParams)"
ContractParam<String> param = ContractParam.of("value", StringBasedDatatype.STRING);

ContractId contractId =
    smartContractClient.createContract(
        Path.of("contract.bin"),
        param
    );
```

---

## Call Contract Function

```java title="callContractFunction(ContractId contractId, String functionName, ContractParam<?>... params)"
ContractParam<String> param = ContractParam.of("value", StringBasedDatatype.STRING);
ContractId contractId =
    ContractId.fromString("0.0.5678");

ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "getValue",
        param
    );
```

