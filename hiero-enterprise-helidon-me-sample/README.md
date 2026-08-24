# Hiero Enterprise Helidon ME Sample

This sample demonstrates concrete operations with `hiero-enterprise-microprofile` on Helidon MicroProfile:

- Create a topic
- Transfer an existing token
- Call a function on an existing smart contract

## Prerequisites

- Java
- A Hedera account with sufficient testnet balance
- The account ID and private key for the operator account

## Configuration

Set the operator credentials via environment variables:

```bash
export HEDERA_ACCOUNT_ID=0.0.123456
export HEDERA_PRIVATE_KEY=<your-private-key>
export HEDERA_NETWORK=hedera-testnet
```

`HEDERA_NETWORK` defaults to `hedera-testnet` when it is not set.

See `.env.example` in this module for the required environment variables.

## Run

From the repository root, start the sample with Maven:

```bash
./mvnw -pl hiero-enterprise-helidon-me-sample -am exec:java -Dexec.mainClass=<ME_SAMPLE_MAIN_CLASS>
```

Replace `<ME_SAMPLE_MAIN_CLASS>` with the application entry-point class used by this sample.

## Verify the Application

Once the application is running, verify that the Helidon MicroProfile server is available:

```bash
curl http://localhost:8080/
```

Expected response:

```text
Hiero Helidon ME sample is running
```

## Create a Topic

Create a topic using the default memo:

```bash
curl -X POST \
  http://localhost:8080/topics \
  -H "Content-Type: application/json" \
  -d '{"memo":"sample-topic"}'
```

The response contains the newly created topic ID:

```json
{
  "topicId": "0.0.123456"
}
```

## Transfer a Token

Transfer an existing token to another account:

```bash
curl -X POST \
  http://localhost:8080/tokens/transfer \
  -H "Content-Type: application/json" \
  -d '{"tokenId":"0.0.123456","toAccountId":"0.0.654321","amount":1}'
```

The response confirms that the transfer was submitted:

```json
{
  "status": "Token transfer submitted"
}
```

## Call a Smart Contract

Call a function on an existing smart contract:

```bash
curl -X POST \
  http://localhost:8080/contracts/call \
  -H "Content-Type: application/json" \
  -d '{"contractId":"0.0.123456","functionName":"getValue"}'
```

The response contains the gas used and transaction cost:

```json
{
  "gasUsed": 1234,
  "cost": "0.0.123"
}
```

> [!NOTE]
> The IDs in these examples are placeholders. Replace them with resources that exist on the configured Hedera network.
