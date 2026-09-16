package org.hiero.microprofile.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TransactionId;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Balance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Block;
import org.hiero.base.data.ChunkInfo;
import org.hiero.base.data.Contract;
import org.hiero.base.data.CustomFee;
import org.hiero.base.data.ExchangeRate;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.FixedFee;
import org.hiero.base.data.FractionalFee;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftTransfer;
import org.hiero.base.data.Node;
import org.hiero.base.data.Page;
import org.hiero.base.data.RoyaltyFee;
import org.hiero.base.data.SinglePage;
import org.hiero.base.data.StakingRewardTransfer;
import org.hiero.base.data.TimestampRange;
import org.hiero.base.data.Token;
import org.hiero.base.data.TokenBalance;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.data.TokenTransfer;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.data.Transfer;
import org.hiero.base.implementation.MirrorNodeJsonConverter;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;

public class MirrorNodeJsonConverterImpl implements MirrorNodeJsonConverter<JsonObject> {

  @Override
  public @NonNull Optional<Nft> toNft(@NonNull JsonObject jsonObject) {
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      final TokenId parsedTokenId = TokenId.fromString(jsonObject.getString("token_id"));
      // account_id is null for burned NFTs — the mirror node intentionally omits the owner.
      final AccountId account =
          hasNonNull(jsonObject, "account_id")
              ? AccountId.fromString(jsonObject.getString("account_id"))
              : null;
      final long serial = jsonObject.getJsonNumber("serial_number").longValue();
      final byte[] metadata = Base64.getDecoder().decode(jsonObject.getString("metadata"));
      return Optional.of(new Nft(parsedTokenId, serial, account, metadata));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Optional<NetworkSupplies> toNetworkSupplies(@NonNull JsonObject jsonObject) {
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      final String releasedSupply = jsonObject.getString("released_supply");
      final String totalSupply = jsonObject.getString("total_supply");
      return Optional.of(new NetworkSupplies(releasedSupply, totalSupply));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Optional<NetworkStake> toNetworkStake(@NonNull JsonObject jsonObject) {
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      final long maxStakeReward = jsonObject.getJsonNumber("max_stake_rewarded").longValue();
      final long maxStakeRewardPerHbar =
          jsonObject.getJsonNumber("max_staking_reward_rate_per_hbar").longValue();
      final long maxTotalReward = jsonObject.getJsonNumber("max_total_reward").longValue();
      final double nodeRewardFeeFraction =
          jsonObject.getJsonNumber("node_reward_fee_fraction").doubleValue();
      final long reservedStakingRewards =
          jsonObject.getJsonNumber("reserved_staking_rewards").longValue();
      final long rewardBalanceThreshold =
          jsonObject.getJsonNumber("reward_balance_threshold").longValue();
      final long stakeTotal = jsonObject.getJsonNumber("stake_total").longValue();
      final long stakingPeriodDuration =
          jsonObject.getJsonNumber("staking_period_duration").longValue();
      final long stakingPeriodsStored =
          jsonObject.getJsonNumber("staking_periods_stored").longValue();
      final double stakingRewardFeeFraction =
          jsonObject.getJsonNumber("staking_reward_fee_fraction").doubleValue();
      final long stakingRewardRate = jsonObject.getJsonNumber("staking_reward_rate").longValue();
      final long stakingStartThreshold =
          jsonObject.getJsonNumber("staking_start_threshold").longValue();
      final long unreservedStakingRewardBalance =
          jsonObject.getJsonNumber("unreserved_staking_reward_balance").longValue();

      return Optional.of(
          new NetworkStake(
              maxStakeReward,
              maxStakeRewardPerHbar,
              maxTotalReward,
              nodeRewardFeeFraction,
              reservedStakingRewards,
              rewardBalanceThreshold,
              stakeTotal,
              stakingPeriodDuration,
              stakingPeriodsStored,
              stakingRewardFeeFraction,
              stakingRewardRate,
              stakingStartThreshold,
              unreservedStakingRewardBalance));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Optional<ExchangeRates> toExchangeRates(@NonNull JsonObject jsonObject) {
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      final int currentCentEquivalent =
          jsonObject.getJsonObject("current_rate").getJsonNumber("cent_equivalent").intValue();
      final int currentHbarEquivalent =
          jsonObject.getJsonObject("current_rate").getJsonNumber("hbar_equivalent").intValue();
      final Instant currentExpirationTime =
          Instant.ofEpochSecond(
              jsonObject
                  .getJsonObject("current_rate")
                  .getJsonNumber("expiration_time")
                  .longValue());

      final int nextCentEquivalent =
          jsonObject.getJsonObject("next_rate").getJsonNumber("cent_equivalent").intValue();
      final int nextHbarEquivalent =
          jsonObject.getJsonObject("next_rate").getJsonNumber("hbar_equivalent").intValue();
      final Instant nextExpirationTime =
          Instant.ofEpochSecond(
              jsonObject.getJsonObject("next_rate").getJsonNumber("expiration_time").longValue());

      return Optional.of(
          new ExchangeRates(
              new ExchangeRate(currentCentEquivalent, currentHbarEquivalent, currentExpirationTime),
              new ExchangeRate(nextCentEquivalent, nextHbarEquivalent, nextExpirationTime)));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Optional<AccountInfo> toAccountInfo(@NonNull JsonObject node) {
    if (node.isEmpty() || node.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      final AccountId accountId =
          hasNonNull(node, "account") ? AccountId.fromString(node.getString("account")) : null;
      final String evmAddress =
          hasNonNull(node, "evm_address") ? node.getString("evm_address") : null;
      final long ethereumNonce =
          hasNonNull(node, "ethereum_nonce") ? node.getJsonNumber("ethereum_nonce").longValue() : 0;
      final long pendingReward = node.getJsonNumber("pending_reward").longValue();
      final long balance =
          hasNonNull(node, "balance")
              ? node.getJsonObject("balance").getJsonNumber("balance").longValue()
              : 0;
      return Optional.of(
          new AccountInfo(accountId, evmAddress, balance, ethereumNonce, pendingReward));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + node, e);
    }
  }

  @Override
  public @NonNull List<NetworkFee> toNetworkFees(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("fees")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("fees"))) {
      throw new IllegalArgumentException(
          "Fees jsonObject is not an array: " + jsonObject.get("fees"));
    }
    final JsonArray feesNode = jsonObject.getJsonArray("fees");
    return jsonArrayToStream(feesNode)
        .map(
            n -> {
              try {
                final long gas = n.asJsonObject().getJsonNumber("gas").longValue();
                final String transactionType = n.asJsonObject().getString("transaction_type");
                return new NetworkFee(gas, transactionType);
              } catch (final Exception e) {
                throw new IllegalStateException("Can not parse JSON: " + n, e);
              }
            })
        .toList();
  }

  @Override
  public @NonNull Optional<TransactionInfo> toTransactionInfo(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    if (jsonObject.containsKey("transactions")) {
      jsonObject =
          jsonArrayToStream(jsonObject.getJsonArray("transactions"))
              .findFirst()
              .get()
              .asJsonObject();
    }

    try {
      final String transactionId = jsonObject.getString("transaction_id");
      final byte[] bytes = jsonObject.getString("bytes", "").getBytes();
      final long chargedTxFee = jsonObject.getJsonNumber("charged_tx_fee").longValue();
      final Instant consensusTimestamp = parseInstant(jsonObject.getString("consensus_timestamp"));
      final String entityId = jsonObject.getString("entity_id", null);
      final String maxFee = jsonObject.getString("max_fee");
      final byte[] memo = jsonObject.getString("memo_base64").getBytes();
      final TransactionType name = TransactionType.from(jsonObject.getString("name"));
      final String _node = jsonObject.getString("node", null);
      final int nonce = jsonObject.getInt("nonce");
      final Instant parentConsensusTimestamp =
          hasNonNull(jsonObject, "parent_consensus_timestamp")
              ? parseInstant(jsonObject.getString("parent_consensus_timestamp"))
              : null;

      final String result = jsonObject.getString("result");
      final boolean scheduled = jsonObject.getBoolean("scheduled");
      final byte[] transactionHash = jsonObject.getString("transaction_hash").getBytes();
      final String validDurationSeconds = jsonObject.getString("valid_duration_seconds");
      final Instant validStartTimestamp =
          parseInstant(jsonObject.getString("valid_start_timestamp"));

      final List<NftTransfer> nftTransfers =
          jsonArrayToStream(jsonObject.getJsonArray("nft_transfers"))
              .map(n -> toNftTransfer(n))
              .toList();

      final List<StakingRewardTransfer> stakingRewardTransfers =
          jsonArrayToStream(jsonObject.getJsonArray("staking_reward_transfers"))
              .map(n -> toStakingRewardTransfer(n))
              .toList();

      final List<TokenTransfer> tokenTransfers =
          jsonArrayToStream(jsonObject.getJsonArray("token_transfers"))
              .map(n -> toTokenTransfer(n))
              .toList();

      final List<Transfer> transfers =
          jsonArrayToStream(jsonObject.getJsonArray("transfers")).map(n -> toTransfer(n)).toList();

      return Optional.of(
          new TransactionInfo(
              transactionId,
              bytes,
              chargedTxFee,
              consensusTimestamp,
              entityId,
              maxFee,
              memo,
              name,
              nftTransfers,
              _node,
              nonce,
              parentConsensusTimestamp,
              result,
              scheduled,
              stakingRewardTransfers,
              tokenTransfers,
              transactionHash,
              transfers,
              validDurationSeconds,
              validStartTimestamp));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull List<TransactionInfo> toTransactionInfos(@NonNull JsonObject jsonObject) {
    if (!jsonObject.containsKey("transactions")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("transactions"))) {
      throw new IllegalArgumentException(
          "Transactions jsonObject is not an array: " + jsonObject.get("transactions"));
    }
    final JsonArray transactionsNode = jsonObject.getJsonArray("transactions");

    return jsonArrayToStream(transactionsNode)
        .map(
            (n) -> {
              JsonObject node = n.asJsonObject();
              return toTransactionInfo(node);
            })
        .filter(n -> n.isPresent())
        .map(n -> n.get())
        .toList();
  }

  private Transfer toTransfer(JsonValue node) {
    final JsonObject jsonObject = node.asJsonObject();
    final AccountId account =
        hasNonNull(jsonObject, "account")
            ? AccountId.fromString(jsonObject.getString("account"))
            : null;
    final long amount = jsonObject.getJsonNumber("amount").longValue();
    final boolean isApproval = jsonObject.getBoolean("is_approval");

    return new Transfer(account, amount, isApproval);
  }

  private TokenTransfer toTokenTransfer(JsonValue node) {
    final JsonObject jsonObject = node.asJsonObject();
    final TokenId tokenId =
        hasNonNull(jsonObject, "token_id")
            ? TokenId.fromString(jsonObject.getString("token_id"))
            : null;
    final AccountId account =
        hasNonNull(jsonObject, "account")
            ? AccountId.fromString(jsonObject.getString("account"))
            : null;
    final long amount = jsonObject.getJsonNumber("amount").longValue();
    final boolean isApproval = jsonObject.getBoolean("is_approval");

    return new TokenTransfer(tokenId, account, amount, isApproval);
  }

  private StakingRewardTransfer toStakingRewardTransfer(JsonValue node) {
    final JsonObject jsonObject = node.asJsonObject();
    final AccountId account =
        hasNonNull(jsonObject, "account")
            ? AccountId.fromString(jsonObject.getString("account"))
            : null;
    long amount = jsonObject.getJsonNumber("amount").longValue();

    return new StakingRewardTransfer(account, amount);
  }

  private NftTransfer toNftTransfer(JsonValue node) {
    final JsonObject jsonObject = node.asJsonObject();
    final boolean isApproval = jsonObject.getBoolean("is_approval");
    final AccountId receiverAccountId =
        hasNonNull(jsonObject, "receiver_account_id")
            ? AccountId.fromString(jsonObject.getString("receiver_account_id"))
            : null;
    final AccountId senderAccountId =
        hasNonNull(jsonObject, "sender_account_id")
            ? AccountId.fromString(jsonObject.getString("sender_account_id"))
            : null;
    final long serialNumber = jsonObject.getJsonNumber("serial_number").longValue();
    final TokenId tokenId =
        hasNonNull(jsonObject, "token_id")
            ? TokenId.fromString(jsonObject.getString("token_id"))
            : null;

    return new NftTransfer(isApproval, receiverAccountId, senderAccountId, serialNumber, tokenId);
  }

  @Override
  public List<Nft> toNfts(@NonNull JsonObject jsonObject) {
    if (!jsonObject.containsKey("nfts")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("nfts"))) {
      throw new IllegalArgumentException(
          "NFTs jsonObject is not an array: " + jsonObject.get("nfts"));
    }
    final JsonArray nftsArray = jsonObject.getJsonArray("nfts");

    Spliterator<JsonValue> spliterator =
        Spliterators.spliteratorUnknownSize(nftsArray.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toNft(n.asJsonObject()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  @Override
  public List<Token> toTokens(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("tokens")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("tokens"))) {
      throw new IllegalArgumentException("Tokens node is not an array");
    }
    final JsonArray tokens = jsonObject.getJsonArray("tokens");

    Spliterator<JsonValue> spliterator =
        Spliterators.spliteratorUnknownSize(tokens.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toToken(n.asJsonObject()))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public @NonNull Optional<Topic> toTopic(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    try {
      final TopicId topicId =
          hasNonNull(jsonObject, "topic_id")
              ? TopicId.fromString(jsonObject.getString("topic_id"))
              : null;
      final Key adminKey =
          hasNonNull(jsonObject, "admin_key")
              ? parseKey(jsonObject.getJsonObject("admin_key"))
              : null;
      final AccountId autoRenewAccount =
          hasNonNull(jsonObject, "auto_renew_account")
              ? AccountId.fromString(jsonObject.getString("auto_renew_account"))
              : null;
      final Long autoRenewPeriod =
          hasNonNull(jsonObject, "auto_renew_period")
              ? jsonObject.getJsonNumber("auto_renew_period").longValue()
              : null;
      final Instant createdTimestamp =
          hasNonNull(jsonObject, "created_timestamp")
              ? parseInstant(jsonObject.getString("created_timestamp"))
              : null;
      final boolean deleted = jsonObject.getBoolean("deleted");
      final Key feeScheduleKey =
          hasNonNull(jsonObject, "fee_schedule_key")
              ? parseKey(jsonObject.getJsonObject("fee_schedule_key"))
              : null;
      final String memo = jsonObject.getString("memo");
      final Key submitKey =
          hasNonNull(jsonObject, "submit_key")
              ? parseKey(jsonObject.getJsonObject("submit_key"))
              : null;

      final Instant fromTimestamp =
          parseInstant(jsonObject.getJsonObject("timestamp").getString("from"));
      final Instant toTimestamp =
          hasNonNull(jsonObject.getJsonObject("timestamp"), "to")
              ? parseInstant(jsonObject.getJsonObject("timestamp").getString("to"))
              : null;

      final List<FixedFee> fixedFees =
          jsonArrayToStream(jsonObject.getJsonObject("custom_fees").getJsonArray("fixed_fees"))
              .map(
                  node -> {
                    JsonObject obj = node.asJsonObject();
                    final long amount = obj.getJsonNumber("amount").longValue();
                    final AccountId accountId =
                        hasNonNull(obj, "collector_account_id")
                            ? AccountId.fromString(obj.getString("collector_account_id"))
                            : null;
                    final TokenId tokenId =
                        hasNonNull(obj, "denominating_token_id")
                            ? TokenId.fromString(obj.getString("denominating_token_id"))
                            : null;
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();

      final List<Key> feeExemptKeyList =
          hasNonNull(jsonObject, "fee_exempt_key_list")
              ? jsonArrayToStream(jsonObject.getJsonArray("fee_exempt_key_list"))
                  .map(n -> parseKey(n.asJsonObject()))
                  .toList()
              : List.of();

      return Optional.of(
          new Topic(
              topicId,
              adminKey,
              autoRenewAccount,
              autoRenewPeriod,
              createdTimestamp,
              fixedFees,
              feeExemptKeyList,
              feeScheduleKey,
              submitKey,
              deleted,
              memo,
              new TimestampRange(fromTimestamp, toTimestamp)));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  private @NonNull ChunkInfo toChunkInfo(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    final String accountId =
        hasNonNull(jsonObject.getJsonObject("initial_transaction_id"), "account_id")
            ? jsonObject.getJsonObject("initial_transaction_id").getString("account_id")
            : null;
    final String valid_start =
        jsonObject.getJsonObject("initial_transaction_id").getString("transaction_valid_start");
    final Integer nonce =
        hasNonNull(jsonObject.getJsonObject("initial_transaction_id"), "nonce")
            ? jsonObject.getJsonObject("initial_transaction_id").getInt("nonce")
            : null;
    final boolean scheduled =
        hasNonNull(jsonObject.getJsonObject("initial_transaction_id"), "scheduled")
            && jsonObject.getJsonObject("initial_transaction_id").getBoolean("scheduled");

    String idStr =
        accountId + "@" + valid_start.split("\\.")[0] + "." + valid_start.split("\\.")[1];
    if (scheduled) {
      idStr += "?scheduled";
    }

    if (nonce != null) {
      idStr += "/" + nonce;
    }

    final TransactionId transactionId = TransactionId.fromString(idStr);
    final int number = jsonObject.getInt("number");
    final int total = jsonObject.getInt("total");

    return new ChunkInfo(transactionId, number, total);
  }

  @Override
  public @NonNull Optional<TopicMessage> toTopicMessage(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    try {
      final ChunkInfo chunkInfo =
          hasNonNull(jsonObject, "chunk_info")
              ? toChunkInfo(jsonObject.getJsonObject("chunk_info"))
              : null;

      final Instant consensusTimestamp = parseInstant(jsonObject.getString("consensus_timestamp"));
      final String message =
          new String(Base64.getDecoder().decode(jsonObject.getString("message")));
      final AccountId payerAccountId =
          hasNonNull(jsonObject, "payer_account_id")
              ? AccountId.fromString(jsonObject.getString("payer_account_id"))
              : null;
      final byte[] runningHash = jsonObject.getString("running_hash").getBytes();
      final int runningHashVersion = jsonObject.getInt("running_hash_version");
      final long sequenceNumber = jsonObject.getJsonNumber("sequence_number").longValue();
      final TopicId topicId =
          hasNonNull(jsonObject, "topic_id")
              ? TopicId.fromString(jsonObject.getString("topic_id"))
              : null;

      return Optional.of(
          new TopicMessage(
              chunkInfo,
              consensusTimestamp,
              message,
              payerAccountId,
              runningHash,
              runningHashVersion,
              sequenceNumber,
              topicId));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull List<TopicMessage> toTopicMessages(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("messages")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("messages"))) {
      throw new IllegalArgumentException(
          "Messages array is not an array: " + jsonObject.get("messages"));
    }
    final JsonArray messages = jsonObject.getJsonArray("messages");

    return jsonArrayToStream(messages)
        .map(n -> toTopicMessage(n.asJsonObject()))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .toList();
  }

  private Optional<Token> toToken(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    try {
      final byte[] metadata = jsonObject.getString("metadata").getBytes();
      final String name = jsonObject.getString("name");
      final String symbol = jsonObject.getString("symbol");
      final long decimals = jsonObject.getJsonNumber("decimals").longValue();
      final TokenType type = TokenType.valueOf(jsonObject.getString("type"));
      final TokenId tokenId =
          hasNonNull(jsonObject, "token_id")
              ? TokenId.fromString(jsonObject.getString("token_id"))
              : null;

      return Optional.of(new Token(decimals, metadata, name, symbol, tokenId, type));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public Optional<TokenInfo> toTokenInfo(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty()) {
      return Optional.empty();
    }

    try {
      final TokenId tokenId =
          hasNonNull(jsonObject, "token_id")
              ? TokenId.fromString(jsonObject.getString("token_id"))
              : null;
      final TokenType type = TokenType.valueOf(jsonObject.getString("type"));
      final String name = jsonObject.getString("name");
      final String symbol = jsonObject.getString("symbol");
      final String memo = jsonObject.getString("memo");
      final long decimals = Long.parseLong(jsonObject.getString("decimals"));
      final byte[] metadata = jsonObject.getString("metadata").getBytes();
      final Instant createdTimeStamp = parseInstant(jsonObject.getString("created_timestamp"));
      final Instant modifiedTimestamp = parseInstant(jsonObject.getString("modified_timestamp"));
      final TokenSupplyType supplyType =
          TokenSupplyType.valueOf(jsonObject.getString("supply_type"));
      final String totalSupply = jsonObject.getString("total_supply");
      final String initialSupply = jsonObject.getString("initial_supply");
      final AccountId treasuryAccountId =
          hasNonNull(jsonObject, "treasury_account_id")
              ? AccountId.fromString(jsonObject.getString("treasury_account_id"))
              : null;
      final boolean deleted = hasNonNull(jsonObject, "deleted") && jsonObject.getBoolean("deleted");
      final String maxSupply = jsonObject.getString("max_supply");

      final Instant expiryTimestamp =
          hasNonNull(jsonObject, "expiry_timestamp")
              ? Instant.ofEpochSecond(
                  Math.floorDiv(
                      jsonObject.getJsonNumber("expiry_timestamp").longValue(), 1_000_000_000L),
                  Math.floorMod(
                      jsonObject.getJsonNumber("expiry_timestamp").longValue(), 1_000_000_000L))
              : null;

      final CustomFee customFees = getCustomFee(jsonObject.get("custom_fees").asJsonObject());

      return Optional.of(
          new TokenInfo(
              tokenId,
              type,
              name,
              symbol,
              memo,
              decimals,
              metadata,
              createdTimeStamp,
              modifiedTimestamp,
              expiryTimestamp,
              supplyType,
              initialSupply,
              totalSupply,
              maxSupply,
              treasuryAccountId,
              deleted,
              customFees));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  private CustomFee getCustomFee(JsonObject object) {
    Objects.requireNonNull(object, "object must not be null");

    List<FractionalFee> fractionalFees = List.of();
    List<FixedFee> fixedFees = List.of();
    List<RoyaltyFee> royaltyFees = List.of();

    if (object.containsKey("fixed_fees")) {
      JsonArray fixedFeeArray = object.get("fixed_fees").asJsonArray();
      if (fixedFeeArray == null) {
        throw new IllegalArgumentException("FixedFeesArray is not an array: " + fixedFeeArray);
      }
      fixedFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      fixedFeeArray.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    JsonObject obj = n.asJsonObject();
                    final long amount = obj.getJsonNumber("amount").longValue();
                    final AccountId accountId =
                        hasNonNull(obj, "collector_account_id")
                            ? AccountId.fromString(obj.getString("collector_account_id"))
                            : null;
                    final TokenId tokenId =
                        hasNonNull(obj, "denominating_token_id")
                            ? TokenId.fromString(obj.getString("denominating_token_id"))
                            : null;
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();
    }

    if (object.containsKey("fractional_fees")) {
      JsonArray fractionalFeeArray = object.get("fractional_fees").asJsonArray();
      if (fractionalFeeArray == null) {
        throw new IllegalArgumentException(
            "FractionalFeeArray is not an array: " + fractionalFeeArray);
      }
      fractionalFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      fractionalFeeArray.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    JsonObject obj = n.asJsonObject();
                    final long numeratorAmount =
                        obj.getJsonObject("amount").getJsonNumber("numerator").longValue();
                    final long denominatorAmount =
                        obj.getJsonObject("amount").getJsonNumber("denominator").longValue();
                    final AccountId accountId =
                        hasNonNull(obj, "collector_account_id")
                            ? AccountId.fromString(obj.getString("collector_account_id"))
                            : null;
                    final TokenId tokenId =
                        hasNonNull(obj, "denominating_token_id")
                            ? TokenId.fromString(obj.getString("denominating_token_id"))
                            : null;
                    return new FractionalFee(
                        numeratorAmount, denominatorAmount, accountId, tokenId);
                  })
              .toList();
    }

    if (object.containsKey("royalty_fees")) {
      JsonArray royaltyFeeArray = object.get("royalty_fees").asJsonArray();
      if (royaltyFeeArray == null) {
        throw new IllegalArgumentException("RoyaltyFeeArray is not an array: " + royaltyFeeArray);
      }
      royaltyFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      royaltyFeeArray.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    JsonObject obj = n.asJsonObject();
                    final long numeratorAmount =
                        obj.getJsonObject("amount").getJsonNumber("numerator").longValue();
                    final long denominatorAmount =
                        obj.getJsonObject("amount").getJsonNumber("denominator").longValue();
                    final long fallbackFeeAmount =
                        obj.getJsonObject("fallback_fee").getJsonNumber("amount").longValue();
                    final AccountId accountId =
                        hasNonNull(obj, "collector_account_id")
                            ? AccountId.fromString(obj.getString("collector_account_id"))
                            : null;
                    final TokenId tokenId =
                        hasNonNull(obj.getJsonObject("fallback_fee"), "denominating_token_id")
                            ? TokenId.fromString(
                                obj.getJsonObject("fallback_fee")
                                    .getString("denominating_token_id"))
                            : null;
                    return new RoyaltyFee(
                        numeratorAmount, denominatorAmount, fallbackFeeAmount, accountId, tokenId);
                  })
              .toList();
    }

    return new CustomFee(fixedFees, fractionalFees, royaltyFees);
  }

  @Override
  public List<Balance> toBalances(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("balances")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("balances"))) {
      throw new IllegalArgumentException(
          "TokenBalances array is not an array: " + jsonObject.get("balances"));
    }

    final JsonArray balancesArray = jsonObject.getJsonArray("balances");
    Spliterator<JsonValue> spliterator =
        Spliterators.spliteratorUnknownSize(balancesArray.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toBalance(n.asJsonObject()))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  private Optional<Balance> toBalance(JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty()) {
      return Optional.empty();
    }

    try {
      final AccountId account =
          hasNonNull(jsonObject, "account")
              ? AccountId.fromString(jsonObject.getString("account"))
              : null;
      final long balance = jsonObject.getJsonNumber("balance").longValue();
      final long decimals = jsonObject.getJsonNumber("decimals").longValue();

      return Optional.of(new Balance(account, balance, decimals));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Optional<BalanceSnapshot> toBalanceSnapshot(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }
    try {
      return Optional.of(
          new BalanceSnapshot(
              hasNonNull(jsonObject, "timestamp")
                  ? parseInstant(jsonObject.getString("timestamp"))
                  : null,
              toAccountBalances(jsonObject)));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull List<AccountBalance> toAccountBalances(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("balances")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("balances"))) {
      throw new IllegalArgumentException(
          "Account balances array is not an array: " + jsonObject.get("balances"));
    }

    final JsonArray balancesArray = jsonObject.getJsonArray("balances");
    return jsonArrayToStream(balancesArray)
        .map(n -> toAccountBalance(n.asJsonObject()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  private Optional<AccountBalance> toAccountBalance(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(
          new AccountBalance(
              hasNonNull(jsonObject, "account")
                  ? AccountId.fromString(jsonObject.getString("account"))
                  : null,
              jsonObject.getJsonNumber("balance").longValue(),
              toTokenBalances(jsonObject)));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  private List<TokenBalance> toTokenBalances(@NonNull JsonObject jsonObject) {
    if (!jsonObject.containsKey("tokens") || jsonObject.isNull("tokens")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("tokens"))) {
      throw new IllegalArgumentException(
          "Token balances array is not an array: " + jsonObject.get("tokens"));
    }

    final JsonArray tokensArray = jsonObject.getJsonArray("tokens");
    return jsonArrayToStream(tokensArray).map(n -> toTokenBalance(n.asJsonObject())).toList();
  }

  private TokenBalance toTokenBalance(@NonNull JsonObject jsonObject) {
    try {
      return new TokenBalance(
          hasNonNull(jsonObject, "token_id")
              ? TokenId.fromString(jsonObject.getString("token_id"))
              : null,
          jsonObject.getJsonNumber("balance").longValue());
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  // Contract-related methods

  @Override
  public @NonNull Optional<Contract> toContract(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty()) {
      return Optional.empty();
    }

    try {
      final ContractId contractId =
          hasNonNull(jsonObject, "contract_id")
              ? ContractId.fromString(jsonObject.getString("contract_id"))
              : null;
      final Key adminKey =
          hasNonNull(jsonObject, "admin_key")
              ? parseKey(jsonObject.getJsonObject("admin_key"))
              : null;
      final AccountId autoRenewAccount =
          hasNonNull(jsonObject, "auto_renew_account")
              ? AccountId.fromString(jsonObject.getString("auto_renew_account"))
              : null;
      final Long autoRenewPeriod =
          hasNonNull(jsonObject, "auto_renew_period")
              ? jsonObject.getJsonNumber("auto_renew_period").longValue()
              : null;
      final Instant createdTimestamp =
          hasNonNull(jsonObject, "created_timestamp")
              ? parseInstant(jsonObject.getString("created_timestamp"))
              : null;

      final boolean deleted = hasNonNull(jsonObject, "deleted") && jsonObject.getBoolean("deleted");
      final Instant expirationTimestamp =
          hasNonNull(jsonObject, "expiration_timestamp")
              ? parseInstant(jsonObject.getString("expiration_timestamp"))
              : null;
      final String fileId = jsonObject.getString("file_id", null);
      final String evmAddress = jsonObject.getString("evm_address");
      final String memo = jsonObject.getString("memo");
      final Integer maxAutomaticTokenAssociations =
          hasNonNull(jsonObject, "max_automatic_token_associations")
              ? jsonObject.getJsonNumber("max_automatic_token_associations").intValue()
              : null;
      final Long nonce =
          hasNonNull(jsonObject, "nonce") ? jsonObject.getJsonNumber("nonce").longValue() : null;
      final String obtainerId = jsonObject.getString("obtainer_id", null);
      final boolean permanentRemoval =
          hasNonNull(jsonObject, "permanent_removal") && jsonObject.getBoolean("permanent_removal");
      final String proxyAccountId = jsonObject.getString("proxy_account_id", null);
      final Instant fromTimestamp =
          parseInstant(jsonObject.getJsonObject("timestamp").getString("from"));
      final Instant toTimestamp =
          hasNonNull(jsonObject.getJsonObject("timestamp"), "to")
              ? parseInstant(jsonObject.getJsonObject("timestamp").getString("to"))
              : null;

      final String bytecode = jsonObject.getString("bytecode", null);
      final String runtimeBytecode = jsonObject.getString("runtime_bytecode", null);

      return Optional.of(
          new Contract(
              contractId,
              adminKey,
              autoRenewAccount,
              autoRenewPeriod,
              createdTimestamp,
              deleted,
              expirationTimestamp,
              fileId,
              evmAddress,
              memo,
              maxAutomaticTokenAssociations,
              nonce,
              obtainerId,
              permanentRemoval,
              proxyAccountId,
              fromTimestamp,
              toTimestamp,
              bytecode,
              runtimeBytecode));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull Page<Contract> toContractPage(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty()) {
      return new SinglePage<>(List.of());
    }

    try {
      final List<Contract> contracts = toContracts(jsonObject);
      return new SinglePage<>(contracts);
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull List<Contract> toContracts(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("contracts")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("contracts"))) {
      throw new IllegalArgumentException(
          "No contracts array in JSON: " + jsonObject.get("contracts"));
    }
    final JsonArray contractsArray = jsonObject.getJsonArray("contracts");

    final Spliterator<JsonValue> spliterator =
        Spliterators.spliteratorUnknownSize(contractsArray.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toContract(n.asJsonObject()))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public @NonNull Optional<Block> toBlock(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    try {
      final long count = jsonObject.getJsonNumber("count").longValue();
      final String hapiVersion =
          hasNonNull(jsonObject, "hapi_version") ? jsonObject.getString("hapi_version") : null;
      final String hash = jsonObject.getString("hash");
      final String name = jsonObject.getString("name");
      final long number = jsonObject.getJsonNumber("number").longValue();
      final String previousHash = jsonObject.getString("previous_hash");
      final Long size =
          hasNonNull(jsonObject, "size") ? jsonObject.getJsonNumber("size").longValue() : null;
      final Long gasUsed =
          hasNonNull(jsonObject, "gas_used")
              ? jsonObject.getJsonNumber("gas_used").longValue()
              : null;
      final String logsBloom =
          hasNonNull(jsonObject, "logs_bloom") ? jsonObject.getString("logs_bloom") : null;

      final Instant fromTimestamp =
          parseInstant(jsonObject.getJsonObject("timestamp").getString("from"));
      final Instant toTimestamp =
          hasNonNull(jsonObject.getJsonObject("timestamp"), "to")
              ? parseInstant(jsonObject.getJsonObject("timestamp").getString("to"))
              : null;

      return Optional.of(
          new Block(
              count,
              hapiVersion,
              hash,
              name,
              number,
              previousHash,
              size,
              new TimestampRange(fromTimestamp, toTimestamp),
              gasUsed,
              logsBloom));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @Override
  public @NonNull List<Block> toBlocks(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    if (!jsonObject.containsKey("blocks")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("blocks"))) {
      throw new IllegalArgumentException(
          "Blocks array is not an array: " + jsonObject.get("blocks"));
    }

    final JsonArray blocks = jsonObject.getJsonArray("blocks");

    return jsonArrayToStream(blocks)
        .map(n -> toBlock(n.asJsonObject()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  @Override
  public @NonNull List<Node> toNodes(@NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");

    if (!jsonObject.containsKey("nodes")) {
      return List.of();
    }

    if (!isArray(jsonObject.get("nodes"))) {
      throw new IllegalArgumentException(
          "Nodes JSON value is not an array: " + jsonObject.get("nodes"));
    }

    final JsonArray nodes = jsonObject.get("nodes").asJsonArray();

    return jsonArrayToStream(nodes)
        .map(n -> toNode(n.asJsonObject()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  private Node.ServiceEndpoint parseServiceEndpoint(final JsonObject jsonObject) {
    return new Node.ServiceEndpoint(
        hasNonNull(jsonObject, "ip_address_v4") ? jsonObject.getString("ip_address_v4") : null,
        jsonObject.getInt("port"),
        hasNonNull(jsonObject, "domain_name") ? jsonObject.getString("domain_name") : null);
  }

  private @NonNull Optional<Node> toNode(@NonNull JsonObject jsonObject) {
    if (jsonObject.isEmpty() || jsonObject.containsKey("_status")) {
      return Optional.empty();
    }

    try {
      final long nodeId = jsonObject.getJsonNumber("node_id").longValue();

      final AccountId nodeAccountId =
          hasNonNull(jsonObject, "node_account_id")
              ? AccountId.fromString(jsonObject.getString("node_account_id"))
              : null;

      final String description =
          hasNonNull(jsonObject, "description") ? jsonObject.getString("description") : null;

      final String memo = hasNonNull(jsonObject, "memo") ? jsonObject.getString("memo") : null;

      final String publicKey =
          hasNonNull(jsonObject, "public_key") ? jsonObject.getString("public_key") : null;

      final Key adminKey =
          hasNonNull(jsonObject, "admin_key")
              ? parseKey(jsonObject.getJsonObject("admin_key"))
              : null;

      final String nodeCertHash =
          hasNonNull(jsonObject, "node_cert_hash") ? jsonObject.getString("node+cert_hash") : null;

      final Long stake =
          hasNonNull(jsonObject, "stake") ? jsonObject.getJsonNumber("stake").longValue() : null;

      final Long minStake =
          hasNonNull(jsonObject, "min_stake")
              ? jsonObject.getJsonNumber("min_stake").longValue()
              : null;

      final Long maxStake =
          hasNonNull(jsonObject, "max_stake")
              ? jsonObject.getJsonNumber("max_stake").longValue()
              : null;

      final Long stakeRewarded =
          hasNonNull(jsonObject, "stake_rewarded")
              ? jsonObject.getJsonNumber("stake_rewarded").longValue()
              : null;

      final Long stakeNotRewarded =
          hasNonNull(jsonObject, "stake_not_rewarded")
              ? jsonObject.getJsonNumber("stake_not_rewarded").longValue()
              : null;

      final Long rewardRateStart =
          hasNonNull(jsonObject, "reward_rate_start")
              ? jsonObject.getJsonNumber("reward_rate_start").longValue()
              : null;

      final boolean declineReward =
          hasNonNull(jsonObject, "decline_reward") && jsonObject.getBoolean("decline_reward");

      final String fileId =
          hasNonNull(jsonObject, "file_id") ? jsonObject.getString("file_id") : null;

      final Instant stakingPeriodFrom =
          parseInstant(jsonObject.getJsonObject("staking_period").getString("from"));

      final Instant stakingPeriodTo =
          hasNonNull(jsonObject.getJsonObject("staking_period"), "to")
              ? parseInstant(jsonObject.getJsonObject("staking_period").getString("to"))
              : null;

      final TimestampRange timestampRange =
          new TimestampRange(
              parseInstant(jsonObject.getJsonObject("timestamp").getString("from")),
              hasNonNull(jsonObject.getJsonObject("timestamp"), "to")
                  ? parseInstant(jsonObject.getJsonObject("timestamp").getString("to"))
                  : null);

      final List<Node.ServiceEndpoint> serviceEndpoints =
          jsonArrayToStream(jsonObject.getJsonArray("service_endpoints"))
              .map(endpoint -> parseServiceEndpoint(endpoint.asJsonObject()))
              .toList();

      final Node.ServiceEndpoint grpcProxyEndpoint =
          hasNonNull(jsonObject, "grpc_proxy_endpoint")
              ? parseServiceEndpoint(jsonObject.getJsonObject("grpc_proxy_endpoint"))
              : null;

      return Optional.of(
          new Node(
              nodeId,
              nodeAccountId,
              description,
              memo,
              publicKey,
              adminKey,
              nodeCertHash,
              stake,
              minStake,
              maxStake,
              stakeRewarded,
              stakeNotRewarded,
              rewardRateStart,
              declineReward,
              fileId,
              stakingPeriodFrom,
              stakingPeriodTo,
              timestampRange,
              serviceEndpoints,
              grpcProxyEndpoint));
    } catch (final Exception e) {
      throw new IllegalStateException("Can not parse JSON: " + jsonObject, e);
    }
  }

  @NonNull
  private Stream<JsonValue> jsonArrayToStream(@NonNull final JsonArray jsonObject) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(jsonObject.iterator(), Spliterator.ORDERED), false);
  }

  private @NonNull Key parseKey(final @NonNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");

    String keyType = jsonObject.getString("_type");
    String keyHex = jsonObject.getString("key");

    return switch (keyType) {
      case "ED25519" -> PublicKey.fromString(keyHex);

      case "ECDSA_SECP256K1" -> PublicKey.fromStringECDSA(keyHex);

      case "ProtobufEncoded" -> {
        byte[] decodedBytes = HexFormat.of().parseHex(keyHex);
        try {
          yield Key.fromBytes(decodedBytes);
        } catch (Exception e) {
          throw new IllegalArgumentException("Invalid Protobuf encoding", e);
        }
      }

      default -> throw new UnsupportedOperationException("Unknown key type: " + keyType);
    };
  }

  private static Instant parseInstant(final @NonNull String jsonStr) {
    Objects.requireNonNull(jsonStr, "jsonStr must not be null");
    if (jsonStr.isEmpty()) {
      return null;
    }

    String[] parts = jsonStr.split("\\.");

    long seconds = Long.parseLong(parts[0]);
    long nanos = 0;

    if (parts.length > 1) {
      String nanoString = parts[1];
      nanoString = String.format("%-9s", nanoString).replace(' ', '0');
      nanos = Long.parseLong(nanoString);
    }

    return Instant.ofEpochSecond(seconds, nanos);
  }

  private boolean hasNonNull(@NonNull JsonObject jsonObject, @NonNull String field) {
    Objects.requireNonNull(jsonObject, "jsonObject must not be null");
    Objects.requireNonNull(field, "field must not be null");
    return jsonObject.containsKey(field) && !jsonObject.isNull(field);
  }

  private boolean isArray(JsonValue jsonValue) {
    return jsonValue != null && jsonValue.getValueType() == JsonValue.ValueType.ARRAY;
  }
}
