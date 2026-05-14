# Hiero Enterprise Helidon SE Sample

This sample demonstrates concrete operations using `hiero-enterprise-base` with Helidon SE routing:

- Create a topic
- Transfer an existing token
- Call a function on an existing smart contract

## Configuration

Set operator credentials via environment variables:

- `HEDERA_ACCOUNT_ID`
- `HEDERA_PRIVATE_KEY`
- `HEDERA_NETWORK` (default: `hedera-testnet`)
- `SERVER_PORT` (optional, default `8082`)

See `.env.example` in this module.

## Run

```bash
../mvnw -pl hiero-enterprise-helidon-se-sample -am exec:java -Dexec.mainClass=org.hiero.helidon.se.sample.HelidonSeSampleMain
```

## Endpoints

- `POST /topics?memo=sample-topic`
- `POST /tokens/transfer?tokenId=0.0.x&toAccountId=0.0.y&amount=1`
- `POST /contracts/call?contractId=0.0.z&functionName=getValue`
