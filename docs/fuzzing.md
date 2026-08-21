# Fuzz Testing

This project uses Jazzer for fuzz testing untrusted input parsing in both the base and Spring modules.

## Jazzer Approach

Fuzz tests use `@FuzzTest` from `com.code_intelligence.jazzer.junit` and the `FuzzedDataProvider` API.
These tests exercise entry points that parse user-controlled content and should only throw documented exceptions such as `IllegalArgumentException` or `IllegalStateException`. Any other uncaught exception is treated as a finding.

## Base Module Targets

The base module adds JUnit fuzz tests for `ContractParam` parsing and datatype validation:

- `ContractParam.address(String)` via `StringBasedDatatype.ADDRESS` and `AccountId.fromString`
- `ContractParam.bytes32(String)` and `ContractParam.bytes32(byte[])` for UTF-8 and raw byte-length validation
- `ContractParam.uint8`, `ContractParam.uint16`, and `ContractParam.uint32` for `LongBasedNumericDatatypes`
- `ContractParam.int72` and `ContractParam.uint72` for `BigIntegerBasedNumericDatatypes`

The fuzz tests are located in `hiero-enterprise-base/src/test/java/org/hiero/base/test/ContractParamFuzzTests.java`.

## Spring Module Targets

The Spring module adds fuzz tests for the JSON parser in `MirrorNodeJsonConverterImpl`:

- `parseInstant(String)`
- `parseKey(JsonNode)`
- `toTopicMessage(JsonNode)`
- `toBlock(JsonNode)`
- `toContract(JsonNode)`

The fuzz tests are located in `hiero-enterprise-spring/src/test/java/org/hiero/spring/test/MirrorNodeJsonConverterFuzzTests.java`.

## Seed Corpus Layout

Seed corpora are stored under the same test package paths in `src/test/resources`:

- `hiero-enterprise-base/src/test/resources/org/hiero/base/test/ContractParamFuzzTestsInputs`
- `hiero-enterprise-spring/src/test/resources/org/hiero/spring/test/MirrorNodeJsonConverterFuzzTestsInputs`

These directories contain regression inputs for boundary values and malformed JSON samples.

## ClusterFuzzLite Integration

ClusterFuzzLite is configured at the repository root:

- `.clusterfuzzlite/project.yaml` sets `language: jvm`
- `.clusterfuzzlite/Dockerfile` is based on `gcr.io/oss-fuzz-base/base-builder-jvm`
- `.clusterfuzzlite/build.sh` builds the Maven test classes and copies fuzz targets plus seed corpus files into `$OUT`

A dedicated PR workflow runs fuzz target build validation in code-change mode: `.github/workflows/cflite-pr.yml`.
