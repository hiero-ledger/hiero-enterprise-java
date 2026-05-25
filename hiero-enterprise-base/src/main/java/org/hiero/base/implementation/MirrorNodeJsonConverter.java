package org.hiero.base.implementation;

import java.util.List;
import java.util.Optional;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Balance;
import org.hiero.base.data.Block;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Nft;
import org.hiero.base.data.Page;
import org.hiero.base.data.Token;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.jspecify.annotations.NonNull;

public interface MirrorNodeJsonConverter<JSON> {
  @NonNull Optional<Nft> toNft(@NonNull JSON json);

  @NonNull Optional<NetworkSupplies> toNetworkSupplies(@NonNull JSON json);

  @NonNull Optional<NetworkStake> toNetworkStake(@NonNull JSON json);

  @NonNull Optional<ExchangeRates> toExchangeRates(@NonNull JSON json);

  @NonNull Optional<AccountInfo> toAccountInfo(@NonNull JSON jsonNode);

  @NonNull List<NetworkFee> toNetworkFees(@NonNull JSON json);

  @NonNull Optional<TransactionInfo> toTransactionInfo(@NonNull JSON json);

  @NonNull List<TransactionInfo> toTransactionInfos(@NonNull JSON json);

  List<Nft> toNfts(@NonNull JSON json);

  Optional<TokenInfo> toTokenInfo(JSON json);

  List<Balance> toBalances(JSON node);

  List<Token> toTokens(JSON node);

  @NonNull Optional<Topic> toTopic(JSON json);

  @NonNull Optional<TopicMessage> toTopicMessage(JSON json);

  @NonNull List<TopicMessage> toTopicMessages(JSON json);

  @NonNull Optional<Contract> toContract(@NonNull JSON json);

  @NonNull Page<Contract> toContractPage(@NonNull JSON json);

  @NonNull List<Contract> toContracts(@NonNull JSON json);

  @NonNull Optional<Block> toBlock(@NonNull JSON json);

  @NonNull List<Block> toBlocks(@NonNull JSON json);
}
