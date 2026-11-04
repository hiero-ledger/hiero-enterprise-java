# Hiero Enterprise Helidon SE Sample

This sample demonstrates concrete operations using `hiero-enterprise-base` with Helidon SE routing:

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
export HEDERA_ACCOUNT_ID=<your-account-Id>
export HEDERA_PRIVATE_KEY=<your-private-key>
export HEDERA_NETWORK=hedera-testnet
export SERVER_PORT=8082
```

`HEDERA_NETWORK` defaults to `hedera-testnet` and `SERVER_PORT` defaults to `8082` when they are not set.

See `.env.example` in this module for the required environment variables.

## Run

From the repository root, start the sample with Maven:

```bash
./mvnw -pl hiero-enterprise-helidon-se-sample -am exec:java -Dexec.mainClass=org.hiero.helidon.se.sample.HelidonSeSampleMain
```

## Verify the Application

Once the application is running, verify that the Helidon SE server is available:

```bash
curl http://localhost:8082/
```

Expected response:

```text
Hiero Helidon SE sample is running
```

## Why Does the SE Sample Use Query Parameters?

The Helidon SE sample intentionally keeps its HTTP layer minimal. It uses Helidon SE routing directly on top of `hiero-enterprise-base` and does not introduce an additional JSON request-body mapping dependency.

For this reason, the SE sample uses query parameters for its small POST requests, while the MicroProfile sample demonstrates JSON request bodies through the MicroProfile/JAX-RS integration.

This keeps the SE sample focused on the base Hiero clients and Helidon SE routing.

## Create a Topic

Create a topic using the default memo:

```bash
curl -X POST \
  "http://localhost:8082/topics"
```

Or provide a custom memo:

```bash
curl -X POST \
  "http://localhost:8082/topics?memo=sample-topic"
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
  "http://localhost:8082/tokens/transfer?tokenId=0.0.123456&toAccountId=0.0.654321&amount=1"
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
  "http://localhost:8082/contracts/call?contractId=0.0.123456&functionName=getValue"
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
