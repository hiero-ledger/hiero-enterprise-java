# Sample Conventions

This repository includes small, concrete sample applications for framework integrations.

## Shared Configuration Contract

All samples should use the same environment variable contract:

- `HEDERA_ACCOUNT_ID`: operator account ID.
- `HEDERA_PRIVATE_KEY`: operator private key (DER-encoded ECDSA key).
- `HEDERA_NETWORK`: network name (for example `hedera-testnet`).

## Concrete Operations

Each sample should demonstrate concrete operations and keep setup minimal:

- Topic creation
- Token transfer
- Smart contract function call

Later samples should keep request/response shapes and naming aligned with the Spring and Quarkus samples added in PR-1.
