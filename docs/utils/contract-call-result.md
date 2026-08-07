# Contract Call Result

`ContractCallResult` provides access to values returned from smart contract function executions.

It is returned by [`SmartContractClient.callContractFunction()`](../clients/smart-contract-client.md) and allows contract return values to be read using type-safe getter methods that map directly to Solidity return types.

Return values are indexed from `0` to `n - 1` in the order they are defined by the contract function.

---

## Methods

| Method | Description |
|:-------|:------------|
| `gasUsed()` | Returns the amount of gas consumed by the contract call. |
| `cost()` | Returns the HBAR cost of the contract call. |
| `getString(int index)` | Returns a Solidity `string` value. |
| `getAddress(int index)` | Returns a Solidity `address` value. |
| `getBool(int index)` | Returns a Solidity `bool` value. |
| `getInt8(int index)` | Returns a Solidity `int8` value. |
| `getInt32(int index)` | Returns a Solidity `int32` value. |
| `getInt64(int index)` | Returns a Solidity `int64` value. |
| `getInt256(int index)` | Returns a Solidity `int256` value. |
| `getUint8(int index)` | Returns a Solidity `uint8` value. |
| `getUint32(int index)` | Returns a Solidity `uint32` value. |
| `getUint64(int index)` | Returns a Solidity `uint64` value. |
| `getUint256(int index)` | Returns a Solidity `uint256` value. |

---

## Contract Call Information

Retrieve gas usage and transaction cost information for a contract execution.

```java title="gasUsed() & cost()"
ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "getValue"
    );

long gasUsed = result.gasUsed();
Hbar cost = result.cost();
```

---

## String Values

Read a Solidity `string` return value.

```java title="getString(int index)"
ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "name"
    );

String name =
    result.getString(0);
```

---

## Address Values

Read a Solidity `address` return value.

```java title="getAddress(int index)"
ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "owner"
    );

String ownerAddress =
    result.getAddress(0);
```

---

## Boolean Values

Read a Solidity `bool` return value.

```java title="getBool(int index)"
ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "isActive"
    );

boolean active =
    result.getBool(0);
```

---

## Signed Integer Values

Read Solidity signed integer return values.

```java title="getInt32(int index)"
int value =
    result.getInt32(0);
```

```java title="getInt64(int index)"
long value =
    result.getInt64(0);
```

```java title="getInt256(int index)"
BigInteger value =
    result.getInt256(0);
```

---

## Unsigned Integer Values

Read Solidity unsigned integer return values.

```java title="getUint8(int index)"
long value =
    result.getUint8(0);
```

```java title="getUint32(int index)"
long value =
    result.getUint32(0);
```

```java title="getUint64(int index)"
long value =
    result.getUint64(0);
```

```java title="getUint256(int index)"
BigInteger value =
    result.getUint256(0);
```

---

## Multiple Return Values

Contract functions can return multiple values. Use the return value index to access each result.

```java title="Reading multiple return values"
ContractCallResult result =
    smartContractClient.callContractFunction(
        contractId,
        "getAccountInfo"
    );

String accountName =
    result.getString(0);

String accountAddress =
    result.getAddress(1);

boolean active =
    result.getBool(2);
```

!!! tip

    Return value indexes correspond to the order of values defined in the Solidity function return signature.