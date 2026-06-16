# Contract Param

`ContractParam` provides a type-safe way to define parameters passed to Hiero smart contract creation and function execution APIs.

It represents Solidity compatible values that can be converted into contract function parameters.

Each parameter contains:

- The Java value
- The Solidity native type

`ContractParam` is used with [`SmartContractClient`](../clients/smart-contract-client.md) methods such as:

- `createContract()`
- `callContractFunction()`

---

## Supported Parameter Types

| Method | Solidity Type | Description |
|:-------|:--------------|:------------|
| `string(String value)` | `string` | Creates a string parameter. |
| `bytes(byte[] value)` | `bytes` | Creates a bytes parameter. |
| `bytes32(byte[] value)` | `bytes32` | Creates a fixed 32-byte parameter. |
| `address(String value)` | `address` | Creates an address parameter from an account ID. |
| `address(AccountId value)` | `address` | Creates an address parameter from an AccountId. |
| `address(ContractId value)` | `address` | Creates an address parameter from a ContractId. |
| `bool(boolean value)` | `bool` | Creates a boolean parameter. |
| `int8()` - `int64()` | `intN` | Creates signed integer parameters. |
| `uint8()` - `uint64()` | `uintN` | Creates unsigned integer parameters. |
| `int72()` - `int256()` | `intN` | Creates large signed integer parameters using BigInteger. |
| `uint72()` - `uint256()` | `uintN` | Creates large unsigned integer parameters using BigInteger. |

---

## String Parameters

Creates a Solidity `string` parameter.

```java title="ContractParam.string(String value)"
ContractParam<String> param =
    ContractParam.string("Hello Hiero");
```

---

## Bytes Parameters

Creates Solidity `byte` parameters.

```java title="ContractParam.bytes(byte[] value)"
ContractParam<Bytes> param =
  ContractParam.bytes("Hello".getBytes());
```

Creates a fixed size `bytes32` parameter.

```java title="ContractParam.bytes32(byte[] value)"
ContractParam<Bytes> param =
  ContractParam.bytes32("Hello".getBytes());
```

!!! note
    
    `bytes32` values must not exceed 32 bytes.

---

## Address Parameters

Creates a Solidity `address` parameter.

```java title="ContractParam.address(String value)"
ContractParam<String> param =
    ContractParam.address("0.0.1234");
```

Creates a Solidity `address` parameter from an `AccountId`.
```java title="ContractParam.address(AccountId value)"
AccountId accountId = AccountId.fromString("0.0.1234");

ContractParam<String> param =
    ContractParam.address(accountId);
```

Creates a Solidity `address` parameter from a `ContractId`.

```java title="ContractParam.address(ContractId value)"
ContractId contractId = ContractId.fromString("0.0.5678");

ContractParam<String> param =
    ContractParam.address(contractId);
```

Creates a Solidity address parameter using an already converted Solidity address.

```java title="addressBySolidty(String value)"
ContractParam<String> param = 
    ContractParam.addressBySolidty("00000000000000000000000000000000000004d2");
```

!!! note

    `addressBySolidty()` expects a Solidity address string. Unlike `address(String)`, the value is not converted from a Hiero account ID such as `0.0.1234`.

---

## Boolean Parameters

Creates a Solidity `bool` parameter.
```java title="ContractParam.bool(boolean value)"
ContractParam<Boolean> param = ContractParam.bool(true);
```

---

## Integer Parameters

Creates Solidity integer parameters. 

### Small Integer Types
```java title="ContractParam.int32(int value)"
ContractParam<Long> param = ContractParam.int32(100);
```
```java title="ContractParam.uint64(long value)"
ContractParam<Long> param = ContractParam.uint64(100);
```

### Large Integer Types

Large integer values use BigInteger.

```java title="ContractParam.int256(BigInteger value)"
ContractParam<BigInteger> param =
    ContractParam.int256(BigInteger.valueOf(100));
```

```java title="ContractParam.uint256(BigInteger value)"
ContractParam<BigInteger> param = 
    ContractParam.uint256(BigInteger.valueOf(100));
```

---

## Using Contract Parameters

Parameters can be supplied when deploying contracts or executing contract functions.

```java
ContractCallResult result =
    smartContractClient.callContractFunction(
        ContractId.fromString("0.0.5678"),
        "setValue",
        ContractParam.string("Hello Hiero"),
        ContractParam.uint256(BigInteger.valueOf(100))
    );
```