package org.hiero.spring.sample;

import java.util.List;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.NftClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.Contract;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.base.data.Token;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.base.mirrornode.NetworkRepository;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.mirrornode.TokenRepository;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.verification.ContractVerificationClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient accountClient;
  private final BlockRepository blockRepository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final NetworkRepository networkRepository;
  private final TokenRepository tokenRepository;
  private final FungibleTokenClient fungibleTokenClient;
  private final NftRepository nftRepository;
  private final NftClient nftClient;
  private final ContractRepository contractRepository;
  private final SmartContractClient smartContractClient;
  private final ContractVerificationClient contractVerificationClient;
  private final TopicRepository topicRepository;
  private final TopicClient topicClient;
  private final FileClient fileClient;

  public HieroEndpoint(
      final AccountClient accountClient,
      final BlockRepository blockRepository,
      final AccountRepository accountRepository,
      final TransactionRepository transactionRepository,
      final NetworkRepository networkRepository,
      final TokenRepository tokenRepository,
      final FungibleTokenClient fungibleTokenClient,
      final NftRepository nftRepository,
      final NftClient nftClient,
      final ContractRepository contractRepository,
      final SmartContractClient smartContractClient,
      final ContractVerificationClient contractVerificationClient,
      final TopicRepository topicRepository,
      final TopicClient topicClient,
      final FileClient fileClient) {
    this.accountClient = Objects.requireNonNull(accountClient, "accountClient must not be null");
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
    this.accountRepository =
        Objects.requireNonNull(accountRepository, "accountRepository must not be null");
    this.transactionRepository =
        Objects.requireNonNull(transactionRepository, "transactionRepository must not be null");
    this.networkRepository =
        Objects.requireNonNull(networkRepository, "networkRepository must not be null");
    this.tokenRepository =
        Objects.requireNonNull(tokenRepository, "tokenRepository must not be null");
    this.fungibleTokenClient =
        Objects.requireNonNull(fungibleTokenClient, "fungibleTokenClient must not be null");
    this.nftRepository = Objects.requireNonNull(nftRepository, "nftRepository must not be null");
    this.nftClient = Objects.requireNonNull(nftClient, "nftClient must not be null");
    this.contractRepository =
        Objects.requireNonNull(contractRepository, "contractRepository must not be null");
    this.smartContractClient =
        Objects.requireNonNull(smartContractClient, "smartContractClient must not be null");
    this.contractVerificationClient =
        Objects.requireNonNull(
            contractVerificationClient, "contractVerificationClient must not be null");
    this.topicRepository =
        Objects.requireNonNull(topicRepository, "topicRepository must not be null");
    this.topicClient = Objects.requireNonNull(topicClient, "topicClient must not be null");
    this.fileClient = Objects.requireNonNull(fileClient, "fileClient must not be null");
  }

  @GetMapping("/")
  public String createAccount() {
    try {
      final Account account = accountClient.createAccount();
      return "Account " + account.accountId() + " created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GetMapping("/accounts/{id}")
  public AccountInfo getAccount(@PathVariable("id") String id) {
    try {
      return accountRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account", e);
    }
  }

  @GetMapping("/accounts/{id}/transactions")
  public Page<TransactionInfo> getAccountTransactions(@PathVariable("id") String id) {
    try {
      return transactionRepository.findByAccount(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account transactions", e);
    }
  }

  @GetMapping("/accounts/{id}/tokens")
  public Page<Token> getAccountTokens(@PathVariable("id") String id) {
    try {
      return tokenRepository.findByAccount(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account tokens", e);
    }
  }

  @GetMapping("/accounts/{id}/nfts")
  public Page<Nft> getAccountNfts(@PathVariable("id") String id) {
    try {
      return nftRepository.findByOwner(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account nfts", e);
    }
  }

  @GetMapping("/blocks")
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GetMapping("/contracts")
  public Page<Contract> getContracts() {
    try {
      return contractRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contracts", e);
    }
  }

  @GetMapping("/network/fees")
  public List<NetworkFee> getNetworkFees() {
    try {
      return networkRepository.fees();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network fees", e);
    }
  }

  @GetMapping("/network/exchange-rates")
  public org.hiero.base.data.ExchangeRates getExchangeRates() {
    try {
      return networkRepository.exchangeRates().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying exchange rates", e);
    }
  }

  @GetMapping("/network/stake")
  public org.hiero.base.data.NetworkStake getNetworkStake() {
    try {
      return networkRepository.stake().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network stake", e);
    }
  }

  @GetMapping("/network/supplies")
  public org.hiero.base.data.NetworkSupplies getNetworkSupplies() {
    try {
      return networkRepository.supplies().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network supplies", e);
    }
  }

  @GetMapping("/nfts/types")
  public Page<NftMetadata> getNftTypes() {
    try {
      return nftRepository.findAllTypes();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying nft types", e);
    }
  }

  @GetMapping("/tokens/{id}")
  public org.hiero.base.data.TokenInfo getToken(@PathVariable("id") String id) {
    try {
      return tokenRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying token", e);
    }
  }

  @GetMapping("/tokens/{id}/balances")
  public Page<org.hiero.base.data.Balance> getTokenBalances(@PathVariable("id") String id) {
    try {
      return tokenRepository.getBalances(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying token balances", e);
    }
  }

  @GetMapping("/topics/{id}")
  public Topic getTopic(@PathVariable("id") String id) {
    try {
      return topicRepository.findTopicById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying topic", e);
    }
  }

  @GetMapping("/transactions/{id}")
  public TransactionInfo getTransaction(@PathVariable("id") String id) {
    try {
      return transactionRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying transaction", e);
    }
  }

  @PostMapping("/tokens")
  public String createToken(@RequestBody TokenRequest request) {
    try {
      return fungibleTokenClient.createToken(request.name(), request.symbol()).toString();
    } catch (final Exception e) {
      throw new RuntimeException("Error creating token", e);
    }
  }

  @PostMapping("/tokens/{id}/mint")
  public long mintToken(@PathVariable("id") String id, @RequestBody MintRequest request) {
    try {
      return fungibleTokenClient.mintToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error minting token", e);
    }
  }

  @PostMapping("/tokens/{id}/burn")
  public long burnToken(@PathVariable("id") String id, @RequestBody MintRequest request) {
    try {
      return fungibleTokenClient.burnToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error burning token", e);
    }
  }

  @PostMapping("/tokens/{id}/transfer")
  public void transferToken(@PathVariable("id") String id, @RequestBody TransferRequest request) {
    try {
      fungibleTokenClient.transferToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.toAccountId(), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error transferring token", e);
    }
  }

  @PostMapping("/tokens/{id}/associate")
  public void associateToken(@PathVariable("id") String id, @RequestBody AssociateRequest request) {
    try {
      fungibleTokenClient.associateToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          request.toAccountId(),
          request.privateKey());
    } catch (final Exception e) {
      throw new RuntimeException("Error associating token", e);
    }
  }

  @PostMapping("/nfts")
  public String createNft(@RequestBody NftRequest request) {
    try {
      return nftClient.createNftType(request.name(), request.symbol()).toString();
    } catch (final Exception e) {
      throw new RuntimeException("Error creating NFT", e);
    }
  }

  @PostMapping("/nfts/{id}/mint")
  public long mintNft(@PathVariable("id") String id, @RequestBody NftMintRequest request) {
    try {
      return nftClient.mintNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.metadata().getBytes());
    } catch (final Exception e) {
      throw new RuntimeException("Error minting NFT", e);
    }
  }

  @PostMapping("/nfts/{id}/burn")
  public void burnNft(@PathVariable("id") String id, @RequestBody NftBurnRequest request) {
    try {
      nftClient.burnNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.serialNumber());
    } catch (final Exception e) {
      throw new RuntimeException("Error burning NFT", e);
    }
  }

  @PostMapping("/nfts/{id}/transfer")
  public void transferNft(@PathVariable("id") String id, @RequestBody NftTransferRequest request) {
    try {
      nftClient.transferNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          request.serialNumber(),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.fromAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.fromAccountKey()),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()));
    } catch (final Exception e) {
      throw new RuntimeException("Error transferring NFT", e);
    }
  }

  @PostMapping("/nfts/{id}/associate")
  public void associateNft(@PathVariable("id") String id, @RequestBody AssociateRequest request) {
    try {
      nftClient.associateNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.privateKey()));
    } catch (final Exception e) {
      throw new RuntimeException("Error associating NFT", e);
    }
  }

  @PostMapping("/nfts/{id}/dissociate")
  public void dissociateNft(@PathVariable("id") String id, @RequestBody AssociateRequest request) {
    try {
      nftClient.dissociateNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.privateKey()));
    } catch (final Exception e) {
      throw new RuntimeException("Error dissociating NFT", e);
    }
  }

  @PostMapping("/tokens/{id}/dissociate")
  public void dissociateToken(@PathVariable("id") String id, @RequestBody AssociateRequest request) {
    try {
      fungibleTokenClient.dissociateToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.privateKey()));
    } catch (final Exception e) {
      throw new RuntimeException("Error dissociating token", e);
    }
  }

  public record TokenRequest(String name, String symbol, int decimals, long initialSupply) {}

  public record NftRequest(String name, String symbol) {}

  public record MintRequest(long amount) {}

  public record NftMintRequest(String metadata) {}

  public record NftBurnRequest(long serialNumber) {}

  public record TransferRequest(String toAccountId, long amount) {}

  public record NftTransferRequest(
      long serialNumber, String fromAccountId, String fromAccountKey, String toAccountId) {}

  public record AssociateRequest(String toAccountId, String privateKey) {}
}
