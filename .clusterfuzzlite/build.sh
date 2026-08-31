#!/bin/bash
set -euo pipefail

cd /workspace
./mvnw -q -DskipTests test-compile

mkdir -p "$OUT"
find hiero-enterprise-base/target/test-classes -name '*FuzzTests.class' -exec cp --parents {} "$OUT" \;
find hiero-enterprise-spring/target/test-classes -name '*FuzzTests.class' -exec cp --parents {} "$OUT" \;
cp -r hiero-enterprise-base/src/test/resources/org/hiero/base/test/ContractParamFuzzTestsInputs "$OUT" 2>/dev/null || true
cp -r hiero-enterprise-spring/src/test/resources/org/hiero/spring/test/MirrorNodeJsonConverterFuzzTestsInputs "$OUT" 2>/dev/null || true
