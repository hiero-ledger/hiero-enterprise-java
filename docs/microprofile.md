# MicroProfile Integration

MicroProfile integration exposes `AccountClient` via CDI.

## Hook lifecycle usage

Use `AccountClient.updateAccountHooks(...)` to create and/or delete hooks in one account update transaction.

```java
@Inject AccountClient accountClient;

HookDetails hookToCreate =
    new HookDetails(
        HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK,
        1001L,
        new EvmHook(ContractId.fromString("0.0.5001")),
        null);

accountClient.updateAccountHooks(account, List.of(hookToCreate), List.of(77L));
```
