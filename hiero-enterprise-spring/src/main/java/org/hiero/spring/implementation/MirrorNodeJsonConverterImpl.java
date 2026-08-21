package org.hiero.spring.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TransactionId;
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
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Balance;
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
import org.hiero.base.data.TokenInfo;
import org.hiero.base.data.TokenTransfer;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.data.Transfer;
import org.hiero.base.implementation.MirrorNodeJsonConverter;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;

public class MirrorNodeJsonConverterImpl implements MirrorNodeJsonConverter<JsonNode> {

  @Override
  public Optional<Nft> toNft(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final TokenId parsedTokenId = TokenId.fromString(node.get("token_id").asText());
      // account_id is null for burned NFTs — the mirror node intentionally omits the owner.
      final AccountId account =
          node.hasNonNull("account_id")
              ? AccountId.fromString(node.get("account_id").asText())
              : null;
      final long serial = node.get("serial_number").asLong();
      final byte[] metadata = node.get("metadata").binaryValue();
      return Optional.of(new Nft(parsedTokenId, serial, account, metadata));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<NetworkSupplies> toNetworkSupplies(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final String releasedSupply = node.get("released_supply").asText();
      final String totalSupply = node.get("total_supply").asText();
      return Optional.of(new NetworkSupplies(releasedSupply, totalSupply));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<NetworkStake> toNetworkStake(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final long maxStakeReward = node.get("max_stake_rewarded").asLong();
      final long maxStakeRewardPerHbar = node.get("max_staking_reward_rate_per_hbar").asLong();
      final long maxTotalReward = node.get("max_total_reward").asLong();
      final double nodeRewardFeeFraction = node.get("node_reward_fee_fraction").asDouble();
      final long reservedStakingRewards = node.get("reserved_staking_rewards").asLong();
      final long rewardBalanceThreshold = node.get("reward_balance_threshold").asLong();
      final long stakeTotal = node.get("stake_total").asLong();
      final long stakingPeriodDuration = node.get("staking_period_duration").asLong();
      final long stakingPeriodsStored = node.get("staking_periods_stored").asLong();
      final double stakingRewardFeeFraction = node.get("staking_reward_fee_fraction").asDouble();
      final long stakingRewardRate = node.get("staking_reward_rate").asLong();
      final long stakingStartThreshold = node.get("staking_start_threshold").asLong();
      final long unreservedStakingRewardBalance =
          node.get("unreserved_staking_reward_balance").asLong();

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
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<ExchangeRates> toExchangeRates(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final int currentCentEquivalent = node.get("current_rate").get("cent_equivalent").asInt();
      final int currentHbarEquivalent = node.get("current_rate").get("hbar_equivalent").asInt();
      final Instant currentExpirationTime =
          Instant.ofEpochSecond(node.get("current_rate").get("expiration_time").asLong());

      final int nextCentEquivalent = node.get("next_rate").get("cent_equivalent").asInt();
      final int nextHbarEquivalent = node.get("next_rate").get("hbar_equivalent").asInt();
      final Instant nextExpirationTime =
          Instant.ofEpochSecond(node.get("next_rate").get("expiration_time").asLong());

      return Optional.of(
          new ExchangeRates(
              new ExchangeRate(currentCentEquivalent, currentHbarEquivalent, currentExpirationTime),
              new ExchangeRate(nextCentEquivalent, nextHbarEquivalent, nextExpirationTime)));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<AccountInfo> toAccountInfo(final JsonNode node) {
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }
    try {
      final AccountId accountId =
          node.hasNonNull("account") ? AccountId.fromString(node.get("account").asText()) : null;
      final String evmAddress =
          node.hasNonNull("evm_address") ? node.get("evm_address").asText() : null;
      final long ethereumNonce =
          node.hasNonNull("ethereum_nonce") ? node.get("ethereum_nonce").asLong() : 0;
      final long pendingReward = node.get("pending_reward").asLong();
      final long balance =
          node.hasNonNull("balance") ? node.get("balance").get("balance").asLong() : 0;
      return Optional.of(
          new AccountInfo(accountId, evmAddress, balance, ethereumNonce, pendingReward));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public List<NetworkFee> toNetworkFees(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("fees")) {
      return List.of();
    }

    final JsonNode feesNode = node.get("fees");
    if (!feesNode.isArray()) {
      throw new IllegalArgumentException("Fees node is not an array: " + feesNode);
    }
    return jsonArrayToStream(feesNode)
        .map(
            n -> {
              try {
                final long gas = n.get("gas").asLong();
                final String transactionType = n.get("transaction_type").asText();
                return new NetworkFee(gas, transactionType);
              } catch (final Exception e) {
                throw new JsonParseException(n, e);
              }
            })
        .toList();
  }

  @Override
  public @NonNull Optional<TransactionInfo> toTransactionInfo(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }

    if (node.has("transactions")) {
      node = jsonArrayToStream(node.get("transactions")).findFirst().get();
    }

    try {
      final String transactionId = node.get("transaction_id").asText();
      final byte[] bytes = node.get("bytes").asText().getBytes();
      final long chargedTxFee = node.get("charged_tx_fee").asLong();
      final Instant consensusTimestamp = parseInstant(node.get("consensus_timestamp").asText());
      final String entityId = node.hasNonNull("entity_id") ? node.get("entity_id").asText() : null;
      final String maxFee = node.get("max_fee").asText();
      final byte[] memo = node.get("memo_base64").asText().getBytes();
      final TransactionType name = TransactionType.from(node.get("name").asText());
      final String _node = node.hasNonNull("node") ? node.get("node").asText() : null;
      final int nonce = node.get("nonce").asInt();
      final Instant parentConsensusTimestamp =
          node.hasNonNull("parent_consensus_timestamp")
              ? parseInstant(node.get("parent_consensus_timestamp").asText())
              : null;
      final String result = node.get("result").asText();
      final boolean scheduled = node.get("scheduled").asBoolean();
      final byte[] transactionHash = node.get("transaction_hash").asText().getBytes();
      final String validDurationSeconds = node.get("valid_duration_seconds").asText();
      final Instant validStartTimestamp = parseInstant(node.get("valid_start_timestamp").asText());

      final List<NftTransfer> nftTransfers =
          jsonArrayToStream(node.get("nft_transfers")).map(n -> toNftTransfer(n)).toList();

      final List<StakingRewardTransfer> stakingRewardTransfers =
          jsonArrayToStream(node.get("staking_reward_transfers"))
              .map(n -> toStakingRewardTransfer(n))
              .toList();

      final List<TokenTransfer> tokenTransfers =
          jsonArrayToStream(node.get("token_transfers")).map(n -> toTokenTransfer(n)).toList();

      final List<Transfer> transfers =
          jsonArrayToStream(node.get("transfers")).map(n -> toTransfer(n)).toList();

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
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<TransactionInfo> toTransactionInfos(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return List.of();
    }
    if (!node.has("transactions")) {
      return List.of();
    }

    final JsonNode transactionsNode = node.get("transactions");

    return jsonArrayToStream(transactionsNode)
        .map(n -> toTransactionInfo(n))
        .filter(n -> n.isPresent())
        .map(n -> n.get())
        .toList();
  }

  private Transfer toTransfer(JsonNode node) {
    final AccountId account =
        node.hasNonNull("account") ? AccountId.fromString(node.get("account").asText()) : null;
    final long amount = node.get("amount").asLong();
    final boolean isApproval = node.get("is_approval").asBoolean();

    return new Transfer(account, amount, isApproval);
  }

  private TokenTransfer toTokenTransfer(JsonNode node) {
    final TokenId tokenId =
        node.hasNonNull("token_id") ? TokenId.fromString(node.get("token_id").asText()) : null;
    final AccountId account =
        node.hasNonNull("account") ? AccountId.fromString(node.get("account").asText()) : null;
    final long amount = node.get("amount").asLong();
    final boolean isApproval = node.get("is_approval").asBoolean();

    return new TokenTransfer(tokenId, account, amount, isApproval);
  }

  private StakingRewardTransfer toStakingRewardTransfer(JsonNode node) {
    final AccountId account =
        node.hasNonNull("account") ? AccountId.fromString(node.get("account").asText()) : null;
    long amount = node.get("amount").asLong();

    return new StakingRewardTransfer(account, amount);
  }

  private NftTransfer toNftTransfer(JsonNode node) {
    final boolean isApproval = node.get("is_approval").asBoolean();
    final AccountId receiverAccountId =
        node.hasNonNull("receiver_account_id")
            ? AccountId.fromString(node.get("receiver_account_id").asText())
            : null;
    final AccountId senderAccountId =
        node.hasNonNull("sender_account_id")
            ? AccountId.fromString(node.get("sender_account_id").asText())
            : null;
    final long serialNumber = node.get("serial_number").asLong();
    final TokenId tokenId =
        node.hasNonNull("token_id") ? TokenId.fromString(node.get("token_id").asText()) : null;

    return new NftTransfer(isApproval, receiverAccountId, senderAccountId, serialNumber, tokenId);
  }

  @Override
  public List<Nft> toNfts(@NonNull JsonNode node) {
    if (!node.has("nfts")) {
      return List.of();
    }
    final JsonNode nftsNode = node.get("nfts");
    if (!nftsNode.isArray()) {
      throw new IllegalArgumentException("NFTs node is not an array: " + nftsNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(nftsNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toNft(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public Optional<TokenInfo> toTokenInfo(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final TokenId tokenId =
          node.hasNonNull("token_id") ? TokenId.fromString(node.get("token_id").asText()) : null;
      final TokenType type = TokenType.valueOf(node.get("type").asText());
      final String name = node.get("name").asText();
      final String symbol = node.get("symbol").asText();
      final String memo = node.get("memo").asText();
      final long decimals = node.get("decimals").asLong();
      final byte[] metadata = node.get("metadata").asText().getBytes();
      final Instant createdTimeStamp = parseInstant(node.get("created_timestamp").asText());
      final Instant modifiedTimestamp = parseInstant(node.get("modified_timestamp").asText());
      final TokenSupplyType supplyType = TokenSupplyType.valueOf(node.get("supply_type").asText());
      final String totalSupply = node.get("total_supply").asText();
      final String initialSupply = node.get("initial_supply").asText();
      final AccountId treasuryAccountId =
          node.hasNonNull("treasury_account_id")
              ? AccountId.fromString(node.get("treasury_account_id").asText())
              : null;
      final boolean deleted = node.hasNonNull("deleted") && node.get("deleted").asBoolean();
      final String maxSupply = node.get("max_supply").asText();

      final Instant expiryTimestamp =
          node.hasNonNull("expiry_timestamp")
              ? Instant.ofEpochSecond(
                  Math.floorDiv(node.get("expiry_timestamp").asLong(), 1_000_000_000L),
                  Math.floorMod(node.get("expiry_timestamp").asLong(), 1_000_000_000L))
              : null;

      final CustomFee customFees = getCustomFee(node.get("custom_fees"));

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
      throw new JsonParseException(node, e);
    }
  }

  private CustomFee getCustomFee(JsonNode node) {
    Objects.requireNonNull(node, "node must not be null");

    List<FractionalFee> fractionalFees = List.of();
    List<FixedFee> fixedFees = List.of();
    List<RoyaltyFee> royaltyFees = List.of();

    if (node.has("fixed_fees")) {
      JsonNode fixedFeeNode = node.get("fixed_fees");
      if (!fixedFeeNode.isArray()) {
        throw new IllegalArgumentException("FixedFees node is not an array: " + fixedFeeNode);
      }
      fixedFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(fixedFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long amount = n.get("amount").asLong();
                    final AccountId accountId =
                        n.hasNonNull("collector_account_id")
                            ? AccountId.fromString(n.get("collector_account_id").asText())
                            : null;
                    final TokenId tokenId =
                        n.hasNonNull("denominating_token_id")
                            ? TokenId.fromString(n.get("denominating_token_id").asText())
                            : null;
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();
    }

    if (node.has("fractional_fees")) {
      JsonNode fractionalFeeNode = node.get("fractional_fees");
      if (!fractionalFeeNode.isArray()) {
        throw new IllegalArgumentException(
            "FractionalFee node is not an array: " + fractionalFeeNode);
      }
      fractionalFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      fractionalFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long numeratorAmount = n.get("amount").get("numerator").asLong();
                    final long denominatorAmount = n.get("amount").get("denominator").asLong();
                    final AccountId accountId =
                        n.hasNonNull("collector_account_id")
                            ? AccountId.fromString(n.get("collector_account_id").asText())
                            : null;
                    final TokenId tokenId =
                        n.hasNonNull("denominating_token_id")
                            ? TokenId.fromString(n.get("denominating_token_id").asText())
                            : null;
                    return new FractionalFee(
                        numeratorAmount, denominatorAmount, accountId, tokenId);
                  })
              .toList();
    }

    if (node.has("royalty_fees")) {
      JsonNode royaltyFeeNode = node.get("royalty_fees");
      if (!royaltyFeeNode.isArray()) {
        throw new IllegalArgumentException("RoyaltyFee node is not an array: " + royaltyFeeNode);
      }
      royaltyFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      royaltyFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long numeratorAmount = n.get("amount").get("numerator").asLong();
                    final long denominatorAmount = n.get("amount").get("denominator").asLong();
                    final long fallbackFeeAmount = n.get("fallback_fee").get("amount").asLong();
                    final AccountId accountId =
                        n.hasNonNull("collector_account_id")
                            ? AccountId.fromString(n.get("collector_account_id").asText())
                            : null;
                    final TokenId tokenId =
                        n.get("fallback_fee").hasNonNull("denominating_token_id")
                            ? TokenId.fromString(
                                n.get("fallback_fee").get("denominating_token_id").asText())
                            : null;
                    return new RoyaltyFee(
                        numeratorAmount, denominatorAmount, fallbackFeeAmount, accountId, tokenId);
                  })
              .toList();
    }

    return new CustomFee(fixedFees, fractionalFees, royaltyFees);
  }

  @Override
  public List<Balance> toBalances(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("balances")) {
      return List.of();
    }
    final JsonNode balancesNode = node.get("balances");
    if (!balancesNode.isArray()) {
      throw new IllegalArgumentException("TokenBalances node is not an array: " + balancesNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(balancesNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toBalance(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public List<Token> toTokens(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("tokens")) {
      return List.of();
    }
    final JsonNode tokens = node.get("tokens");
    if (!tokens.isArray()) {
      throw new IllegalArgumentException("Tokens node is not an array: " + tokens);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(tokens.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toToken(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public @NonNull Optional<Topic> toTopic(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }

    try {
      final TopicId topicId =
          node.hasNonNull("topic_id") ? TopicId.fromString(node.get("topic_id").asText()) : null;
      final Key adminKey = node.hasNonNull("admin_key") ? parseKey(node.get("admin_key")) : null;
      final AccountId autoRenewAccount =
          node.hasNonNull("auto_renew_account")
              ? AccountId.fromString(node.get("auto_renew_account").asText())
              : null;
      final Long autoRenewPeriod =
          node.hasNonNull("auto_renew_period") ? node.get("auto_renew_period").asLong() : null;
      final Instant createdTimestamp =
          node.hasNonNull("created_timestamp")
              ? parseInstant(node.get("created_timestamp").asText())
              : null;
      final boolean deleted = node.hasNonNull("deleted") && node.get("deleted").asBoolean();
      final Key feeScheduleKey =
          node.hasNonNull("fee_schedule_key") ? parseKey(node.get("fee_schedule_key")) : null;
      final String memo = node.get("memo").asText();
      final Key submitKey = node.hasNonNull("submit_key") ? parseKey(node.get("submit_key")) : null;

      final Instant fromTimestamp = parseInstant(node.get("timestamp").get("from").asText());
      final Instant toTimestamp =
          node.get("timestamp").hasNonNull("to")
              ? parseInstant(node.get("timestamp").get("to").asText())
              : null;

      final List<FixedFee> fixedFees =
          jsonArrayToStream(node.get("custom_fees").get("fixed_fees"))
              .map(
                  n -> {
                    final long amount = n.get("amount").asLong();
                    final AccountId accountId =
                        n.hasNonNull("collector_account_id")
                            ? AccountId.fromString(n.get("collector_account_id").asText())
                            : null;
                    final TokenId tokenId =
                        n.hasNonNull("denominating_token_id")
                            ? TokenId.fromString(n.get("denominating_token_id").asText())
                            : null;
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();

      final List<Key> feeExemptKeyList =
          node.hasNonNull("fee_exempt_key_list")
              ? jsonArrayToStream(node.get("fee_exempt_key_list")).map(n -> parseKey(n)).toList()
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
      throw new JsonParseException(node, e);
    }
  }

  private @NonNull ChunkInfo toChunkInfo(JsonNode node) {
    Objects.requireNonNull(node, "node must not be null");
    final String accountId =
        node.get("initial_transaction_id").hasNonNull("account_id")
            ? node.get("initial_transaction_id").get("account_id").asText()
            : null;
    final String valid_start =
        node.get("initial_transaction_id").get("transaction_valid_start").asText();
    final Integer nonce =
        node.get("initial_transaction_id").hasNonNull("nonce")
            ? node.get("initial_transaction_id").get("nonce").asInt()
            : null;
    final boolean scheduled =
        node.get("initial_transaction_id").hasNonNull("scheduled")
            && node.get("initial_transaction_id").get("scheduled").asBoolean();

    String idStr =
        accountId + "@" + valid_start.split("\\.")[0] + "." + valid_start.split("\\.")[1];
    if (scheduled) {
      idStr += "?scheduled";
    }

    if (nonce != null) {
      idStr += "/" + nonce;
    }

    final TransactionId transactionId = TransactionId.fromString(idStr);
    final int number = node.get("number").asInt();
    final int total = node.get("total").asInt();

    return new ChunkInfo(transactionId, number, total);
  }

  @Override
  public @NonNull Optional<TopicMessage> toTopicMessage(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }
    try {
      final ChunkInfo chunkInfo =
          node.hasNonNull("chunk_info") ? toChunkInfo(node.get("chunk_info")) : null;
      final Instant consensusTimestamp = parseInstant(node.get("consensus_timestamp").asText());
      final String message = new String(Base64.getDecoder().decode(node.get("message").asText()));
      final AccountId payerAccountId =
          node.hasNonNull("payer_account_id")
              ? AccountId.fromString(node.get("payer_account_id").asText())
              : null;
      final byte[] runningHash = node.get("running_hash").asText().getBytes();
      final int runningHashVersion = node.get("running_hash_version").asInt();
      final long sequenceNumber = node.get("sequence_number").asLong();
      final TopicId topicId =
          node.hasNonNull("topic_id") ? TopicId.fromString(node.get("topic_id").asText()) : null;

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
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<TopicMessage> toTopicMessages(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("messages")) {
      return List.of();
    }

    final JsonNode messages = node.get("messages");
    if (!messages.isArray()) {
      throw new IllegalArgumentException("Messages node is not an array: " + messages);
    }

    return jsonArrayToStream(messages)
        .map(n -> toTopicMessage(n))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .toList();
  }

  private @NonNull Optional<Token> toToken(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }

    try {
      final byte[] metadata = node.get("metadata").asText().getBytes();
      final String name = node.get("name").asText();
      final String symbol = node.get("symbol").asText();
      final long decimals = node.get("decimals").asLong();
      final TokenType type = TokenType.valueOf(node.get("type").asText());
      final TokenId tokenId =
          node.hasNonNull("token_id") ? TokenId.fromString(node.get("token_id").asText()) : null;

      return Optional.of(new Token(decimals, metadata, name, symbol, tokenId, type));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  private Optional<Balance> toBalance(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final AccountId account =
          node.hasNonNull("account") ? AccountId.fromString(node.get("account").asText()) : null;
      final long balance = node.get("balance").asLong();
      final long decimals = node.get("decimals").asLong();

      return Optional.of(new Balance(account, balance, decimals));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull Optional<Contract> toContract(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final ContractId contractId =
          node.hasNonNull("contract_id")
              ? ContractId.fromString(node.get("contract_id").asText())
              : null;
      final Key adminKey = node.hasNonNull("admin_key") ? parseKey(node.get("admin_key")) : null;
      final AccountId autoRenewAccount =
          node.hasNonNull("auto_renew_account")
              ? AccountId.fromString(node.get("auto_renew_account").asText())
              : null;
      final Long autoRenewPeriod =
          node.hasNonNull("auto_renew_period") ? node.get("auto_renew_period").asLong() : null;
      final Instant createdTimestamp =
          node.hasNonNull("created_timestamp")
              ? parseInstant(node.get("created_timestamp").asText())
              : null;

      final boolean deleted = node.hasNonNull("deleted") && node.get("deleted").asBoolean();
      final Instant expirationTimestamp =
          node.hasNonNull("expiration_timestamp")
              ? parseInstant(node.get("expiration_timestamp").asText())
              : null;
      final String fileId = node.hasNonNull("file_id") ? node.get("file_id").asText() : null;
      final String evmAddress = node.get("evm_address").asText();
      final String memo = node.get("memo").asText();
      final Integer maxAutomaticTokenAssociations =
          node.hasNonNull("max_automatic_token_associations")
              ? node.get("max_automatic_token_associations").asInt()
              : null;
      final Long nonce = node.hasNonNull("nonce") ? node.get("nonce").asLong() : null;
      final String obtainerId =
          node.hasNonNull("obtainer_id") ? node.get("obtainer_id").asText() : null;
      final boolean permanentRemoval =
          node.hasNonNull("permanent_removal") && node.get("permanent_removal").asBoolean();
      final String proxyAccountId =
          node.hasNonNull("proxy_account_id") ? node.get("proxy_account_id").asText() : null;
      final Instant fromTimestamp = parseInstant(node.get("timestamp").get("from").asText());
      final Instant toTimestamp =
          node.get("timestamp").hasNonNull("to")
              ? parseInstant(node.get("timestamp").get("to").asText())
              : null;
      final String bytecode = node.hasNonNull("bytecode") ? node.get("bytecode").asText() : null;
      final String runtimeBytecode =
          node.hasNonNull("runtime_bytecode") ? node.get("runtime_bytecode").asText() : null;

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
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull Page<Contract> toContractPage(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return new SinglePage<>(List.of());
    }

    try {
      final List<Contract> contracts = toContracts(node);
      return new SinglePage<>(contracts);
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<Contract> toContracts(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("contracts")) {
      return List.of();
    }
    final JsonNode contractsNode = node.get("contracts");
    if (!contractsNode.isArray()) {
      throw new IllegalArgumentException("Contracts node is not an array: " + contractsNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(contractsNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toContract(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public @NonNull Optional<Block> toBlock(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }

    try {
      final long count = node.get("count").asLong();
      final String hapiVersion =
          node.hasNonNull("hapi_version") ? node.get("hapi_version").asText() : null;
      final String hash = node.get("hash").asText();
      final String name = node.get("name").asText();
      final long number = node.get("number").asLong();
      final String previousHash = node.get("previous_hash").asText();
      final Long size = node.hasNonNull("size") ? node.get("size").asLong() : null;
      final Long gasUsed = node.hasNonNull("gas_used") ? node.get("gas_used").asLong() : null;
      final String logsBloom =
          node.hasNonNull("logs_bloom") ? node.get("logs_bloom").asText() : null;

      final Instant fromTimestamp = parseInstant(node.get("timestamp").get("from").asText());
      final Instant toTimestamp =
          node.get("timestamp").hasNonNull("to")
              ? parseInstant(node.get("timestamp").get("to").asText())
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
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<Block> toBlocks(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("blocks")) {
      return List.of();
    }

    final JsonNode blocks = node.get("blocks");
    if (!blocks.isArray()) {
      throw new IllegalArgumentException("Blocks node is not an array: " + blocks);
    }

    return jsonArrayToStream(blocks)
        .map(n -> toBlock(n))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .toList();
  }

  private Node.ServiceEndpoint parseServiceEndpoint(final JsonNode endpoint) {
    return new Node.ServiceEndpoint(
        endpoint.hasNonNull("ip_address_v4") ? endpoint.get("ip_address_v4").asText() : null,
        endpoint.get("port").asInt(),
        endpoint.hasNonNull("domain_name") ? endpoint.get("domain_name").asText() : null);
  }

  @Override
  public @NonNull List<Node> toNodes(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "node must not be null");

    if (!node.has("nodes")) {
      return List.of();
    }

    final JsonNode nodes = node.get("nodes");
    if (!nodes.isArray()) {
      throw new IllegalArgumentException("Nodes property is not an array: " + nodes);
    }

    return jsonArrayToStream(nodes)
        .map(n -> toNode(n))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  private @NonNull Optional<Node> toNode(@NonNull JsonNode node) {
    if (node.isNull() || node.isEmpty() || node.has("_status")) {
      return Optional.empty();
    }

    try {
      final long nodeId = node.get("node_id").asLong();

      final AccountId nodeAccountId =
          node.hasNonNull("node_account_id")
              ? AccountId.fromString(node.get("node_account_id").asText())
              : null;

      final String description =
          node.hasNonNull("description") ? node.get("description").asText() : null;

      final String memo = node.hasNonNull("memo") ? node.get("memo").asText() : null;

      final String publicKey =
          node.hasNonNull("public_key") ? node.get("public_key").asText() : null;

      final Key adminKey = node.hasNonNull("admin_key") ? parseKey(node.get("admin_key")) : null;

      final String nodeCertHash =
          node.hasNonNull("node_cert_hash") ? node.get("node_cert_hash").asText() : null;

      final Long stake = node.hasNonNull("stake") ? node.get("stake").asLong() : null;

      final Long minStake = node.hasNonNull("min_stake") ? node.get("min_stake").asLong() : null;

      final Long maxStake = node.hasNonNull("max_stake") ? node.get("max_stake").asLong() : null;

      final Long stakeRewarded =
          node.hasNonNull("stake_rewarded") ? node.get("stake_rewarded").asLong() : null;

      final Long stakeNotRewarded =
          node.hasNonNull("stake_not_rewarded") ? node.get("stake_not_rewarded").asLong() : null;

      final Long rewardRateStart =
          node.hasNonNull("reward_rate_start") ? node.get("reward_rate_start").asLong() : null;

      final boolean declineReward =
          node.has("decline_reward") && node.get("decline_reward").asBoolean();

      final String fileId = node.hasNonNull("file_id") ? node.get("file_id").asText() : null;

      final List<Node.ServiceEndpoint> serviceEndpoints =
          jsonArrayToStream(node.get("service_endpoints")).map(this::parseServiceEndpoint).toList();

      final Node.ServiceEndpoint grpcProxyEndpoint =
          node.hasNonNull("grpc_proxy_endpoint")
              ? parseServiceEndpoint(node.get("grpc_proxy_endpoint"))
              : null;

      final Instant fromTimestamp = parseInstant(node.get("timestamp").get("from").asText());

      final Instant toTimestamp =
          node.get("timestamp").hasNonNull("to")
              ? parseInstant(node.get("timestamp").get("to").asText())
              : null;

      final Instant stakingPeriodFrom =
          parseInstant(node.get("staking_period").get("from").asText());

      final Instant stakingPeriodTo =
          node.get("staking_period").hasNonNull("to")
              ? parseInstant(node.get("staking_period").get("to").asText())
              : null;

      final TimestampRange timestampRange = new TimestampRange(fromTimestamp, toTimestamp);

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
      throw new JsonParseException(node, e);
    }
  }

  @NonNull
  private Stream<JsonNode> jsonArrayToStream(@NonNull final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.isArray()) {
      throw new JsonParseException("not an array", node);
    }
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(node.iterator(), Spliterator.ORDERED), false);
  }

  private @NonNull Key parseKey(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "node must not be null");

    String keyType = node.get("_type").asText();
    String keyHex = node.get("key").asText();

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
}
