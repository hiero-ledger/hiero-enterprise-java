FROM gcr.io/oss-fuzz-base/base-builder-jvm

COPY . /workspace
WORKDIR /workspace

RUN ./mvnw -q -DskipTests package

# Collect compiled fuzz targets and seed corpora
RUN mkdir -p $OUT
RUN find hiero-enterprise-base/target/test-classes -name '*FuzzTests.class' -exec cp --parents {} $OUT \;
RUN find hiero-enterprise-spring/target/test-classes -name '*FuzzTests.class' -exec cp --parents {} $OUT \;
RUN cp -r hiero-enterprise-base/src/test/resources/org/hiero/base/test/ContractParamFuzzTestsInputs $OUT 2>/dev/null || true
RUN cp -r hiero-enterprise-spring/src/test/resources/org/hiero/spring/test/MirrorNodeJsonConverterFuzzTestsInputs $OUT 2>/dev/null || true
