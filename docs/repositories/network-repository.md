# Network Repository

NetworkRepository provides APIs for querying Hiero network information from a Mirror Node, including exchange rates, network fees, staking information, and network token supplies.

---

## Methods

| Method | Description |
|:-------|:------------|
| `exchangeRates()` | Retrieves the current network exchange rates. |
| `fees()` | Retrieves the list of network transaction fees. |
| `stake()` | Retrieves the current network staking information. |
| `supplies()` | Retrieves the current network token supply information. |

---

## Get Exchange Rates

```java title="exchangeRates()"
Optional<ExchangeRates> exchangeRates =
    networkRepository.exchangeRates();

exchangeRates.ifPresent(System.out::println);
```

---

## Get Network Fees

```java title="fees()"
List<NetworkFee> fees =
    networkRepository.fees();

fees.forEach(System.out::println);
```

---

## Get Network Stake

```java title="stake()"
Optional<NetworkStake> stake =
    networkRepository.stake();

stake.ifPresent(System.out::println);
```

---

## Get Network Supplies

```java title="supplies()"
Optional<NetworkSupplies> supplies =
    networkRepository.supplies();

supplies.ifPresent(System.out::println);
```