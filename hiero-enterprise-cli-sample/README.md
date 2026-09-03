# Hiero Enterprise CLI Sample

A command-line interface for interacting with the Hiero network using [picocli](https://picocli.info) and `hiero-enterprise-base`.

## Prerequisites

- Java 21+
- Maven 3.8+
- A Hedera testnet account ([get one free](https://portal.hedera.com))

## Build

```bash
mvn clean package -pl hiero-enterprise-cli-sample -am -DskipTests
```

## Usage

```bash
java -jar hiero-enterprise-cli-sample/target/hiero-enterprise-cli-sample-*.jar [command]
```

### Create Account
```bash
java -jar target/*.jar create-account \
  --account-id 0.0.123 \
  --private-key <YOUR_PRIVATE_KEY>
```

### Create Topic
```bash
java -jar target/*.jar create-topic \
  --account-id 0.0.123 \
  --private-key <YOUR_PRIVATE_KEY> \
  --memo "My first topic"
```

### Send Message to Topic
```bash
java -jar target/*.jar send-message \
  --account-id 0.0.123 \
  --private-key <YOUR_PRIVATE_KEY> \
  --topic-id 0.0.456 \
  --message "Hello Hiero!"
```

## Configuration

Set the following environment variables or pass them as CLI options:

| Variable | Description |
|----------|-------------|
| `--account-id` | Your Hedera operator account ID (e.g. `0.0.123`) |
| `--private-key` | Your Hedera operator private key |
| `--network` | Network name (default: `hedera-testnet`) |
