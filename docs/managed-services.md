# Base / Managed Services

`hiero-enterprise-base` provides the core managed clients used by Spring and MicroProfile modules.

## Hook lifecycle support

Hook lifecycle operations are exposed through `AccountClient.updateAccountHooks(...)`.
This API supports both:

- creating hooks (`hooksToCreate`)
- deleting hooks (`hooksToDelete`)

Core types:

- `org.hiero.base.data.HookDetails`
- `com.hedera.hashgraph.sdk.HookExtensionPoint`
- `org.hiero.base.protocol.data.AccountHookUpdateRequest`
- `org.hiero.base.protocol.data.AccountHookUpdateResult`

Example:

```java
HookDetails hookToCreate =
    new HookDetails(
        HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK,
        1001L,
        new EvmHook(ContractId.fromString("0.0.5001")),
        null);

accountClient.updateAccountHooks(account, List.of(hookToCreate), List.of(77L));
```
