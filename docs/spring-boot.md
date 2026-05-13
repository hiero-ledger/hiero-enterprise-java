# Spring Boot Integration

Spring integration exposes `AccountClient` as a managed bean.

## Hook lifecycle usage

Use `AccountClient.updateAccountHooks(...)` to create and/or delete hooks in one account update transaction.

```java
@Autowired AccountClient accountClient;

HookDetails hookToCreate =
    new HookDetails(
        HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK,
        1001L,
        new EvmHook(ContractId.fromString("0.0.5001")),
        null);

accountClient.updateAccountHooks(account, List.of(hookToCreate), List.of(77L));
```
