# Hiero Enterprise Spring Sample

This sample demonstrates concrete operations with `hiero-enterprise-spring`:

- Create a topic
- Transfer an existing token
- Call a function on an existing smart contract

## Configuration

Use environment variables (or module-local `.env`) for operator credentials:

- `HEDERA_ACCOUNT_ID`
- `HEDERA_PRIVATE_KEY`
- `HEDERA_NETWORK` (default: `hedera-testnet`)

See `.env.example` in this module.

## Run

```bash
../mvnw -pl hiero-enterprise-spring-sample spring-boot:run
```

## Endpoints

- `POST /topics`
  - body: `{"memo":"sample-topic"}`
- `POST /tokens/transfer`
  - body: `{"tokenId":"0.0.x","toAccountId":"0.0.y","amount":1}`
- `POST /contracts/call`
  - body: `{"contractId":"0.0.z","functionName":"getValue"}`
