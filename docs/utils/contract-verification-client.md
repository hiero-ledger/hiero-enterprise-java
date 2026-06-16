# Contract Verification Client

`ContractVerificationClient` provides APIs for verifying deployed smart contracts and checking their verification status on supported Hiero networks.

Currently, contract verification is supported on:

- Hedera Mainnet
- Hedera Testnet

Contract verification allows published source code and metadata to be matched against deployed contract bytecode, enabling transparency and public verification.

---

## Methods

| Method | Description |
|:-------|:------------|
| `checkVerification(ContractId contractId)` | Retrieves the verification status of a smart contract. |
| `checkVerification(ContractId contractId, String fileName, String fileContent)` | Verifies that a specific source file exists in a verified contract and matches the provided content. |
| `verify(ContractId contractId, String contractName, String contractSource, String contractMetadata)` | Attempts to verify a contract using source code and optional metadata content. |
| `verify(ContractId contractId, String contractName, Map<String, String> files)` | Attempts to verify a contract using multiple source files and metadata. |

---

## Check Verification Status

Retrieves the current verification state of a deployed contract.

```java title="checkVerification(ContractId contractId)"
ContractId contractId =
    ContractId.fromString("0.0.123456");

ContractVerificationState state =
    contractVerificationClient.checkVerification(
        contractId
    );
```

---

## Verify Contract File Content

Checks whether a verified contract contains a specific file with matching content.

```java title="checkVerification(ContractId contractId, String fileName, String fileContent)"
ContractId contractId =
    ContractId.fromString("0.0.123456");

boolean matches =
    contractVerificationClient.checkVerification(
        contractId,
        "MyContract.sol",
        contractSource
    );

```

!!! info

    This method can only be used after the contract has been successfully verified.

---

## Verify Contract Using Source Code

Verifies a contract using Solidity source code and optional metadata.

```java title="verify(ContractId contractId, String contractName, String contractSource, String contractMetadata)"
ContractId contractId =
    ContractId.fromString("0.0.123456");

String source = """
pragma solidity ^0.8.0;

contract Counter {
    uint256 private value;

    function increment() public {
        value++;
    }
}
""";

ContractVerificationState state =
    contractVerificationClient.verify(
        contractId,
        "Counter",
        source,
        null
    );
```

If metadata is available from the Solidity compilation process, it can be supplied as the fourth parameter.

```java title="verify(ContractId contractId, String contractName, String contractSource, String contractMetadata)"
String metadata =
    Files.readString(
        Path.of("metadata.json")
    );

ContractVerificationState state =
    contractVerificationClient.verify(
        contractId,
        "Counter",
        source,
        metadata
    );
```

---

## Verify Contract Using Multiple Files

For contracts that consist of multiple Solidity source files, libraries, interfaces, or metadata files, provide all files as a map.

```java title="verify(ContractId contractId, String contractName, Map<String, String> files)"
ContractId contractId =
    ContractId.fromString("0.0.123456");

Map<String, String> files =
    Map.of(
        "Counter.sol",
        Files.readString(Path.of("Counter.sol")),
        "Ownable.sol",
        Files.readString(Path.of("Ownable.sol")),
        "metadata.json",
        Files.readString(Path.of("metadata.json"))
    );

ContractVerificationState state =
    contractVerificationClient.verify(
        contractId,
        "Counter",
        files
    );
```
