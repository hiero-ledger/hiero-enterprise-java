package org.hiero.microprofile.sample;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
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

@Path("/")
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

  @Inject
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
    this.accountClient = accountClient;
    this.blockRepository = blockRepository;
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.networkRepository = networkRepository;
    this.tokenRepository = tokenRepository;
    this.fungibleTokenClient = fungibleTokenClient;
    this.nftRepository = nftRepository;
    this.nftClient = nftClient;
    this.contractRepository = contractRepository;
    this.smartContractClient = smartContractClient;
    this.contractVerificationClient = contractVerificationClient;
    this.topicRepository = topicRepository;
    this.topicClient = topicClient;
    this.fileClient = fileClient;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String createAccount() {
    try {
      final Account account = accountClient.createAccount();
      return "Account created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GET
  @Path("/accounts/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public AccountInfo getAccount(@PathParam("id") String id) {
    try {
      return accountRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account", e);
    }
  }

  @GET
  @Path("/accounts/{id}/transactions")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<TransactionInfo> getAccountTransactions(@PathParam("id") String id) {
    try {
      return transactionRepository.findByAccount(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account transactions", e);
    }
  }

  @GET
  @Path("/accounts/{id}/tokens")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Token> getAccountTokens(@PathParam("id") String id) {
    try {
      return tokenRepository.findByAccount(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account tokens", e);
    }
  }

  @GET
  @Path("/accounts/{id}/nfts")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Nft> getAccountNfts(@PathParam("id") String id) {
    try {
      return nftRepository.findByOwner(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying account nfts", e);
    }
  }

  @GET
  @Path("/blocks")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GET
  @Path("/contracts")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Contract> getContracts() {
    try {
      return contractRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contracts", e);
    }
  }

  @GET
  @Path("/network/fees")
  @Produces(MediaType.APPLICATION_JSON)
  public List<NetworkFee> getNetworkFees() {
    try {
      return networkRepository.fees();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network fees", e);
    }
  }

  @GET
  @Path("/network/exchange-rates")
  @Produces(MediaType.APPLICATION_JSON)
  public org.hiero.base.data.ExchangeRates getExchangeRates() {
    try {
      return networkRepository.exchangeRates().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying exchange rates", e);
    }
  }

  @GET
  @Path("/network/stake")
  @Produces(MediaType.APPLICATION_JSON)
  public org.hiero.base.data.NetworkStake getNetworkStake() {
    try {
      return networkRepository.stake().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network stake", e);
    }
  }

  @GET
  @Path("/network/supplies")
  @Produces(MediaType.APPLICATION_JSON)
  public org.hiero.base.data.NetworkSupplies getNetworkSupplies() {
    try {
      return networkRepository.supplies().orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying network supplies", e);
    }
  }

  @GET
  @Path("/nfts/types")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<NftMetadata> getNftTypes() {
    try {
      return nftRepository.findAllTypes();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying nft types", e);
    }
  }

  @GET
  @Path("/tokens/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public org.hiero.base.data.TokenInfo getToken(@PathParam("id") String id) {
    try {
      return tokenRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying token", e);
    }
  }

  @GET
  @Path("/tokens/{id}/balances")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<org.hiero.base.data.Balance> getTokenBalances(@PathParam("id") String id) {
    try {
      return tokenRepository.getBalances(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying token balances", e);
    }
  }

  @GET
  @Path("/topics/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Topic getTopic(@PathParam("id") String id) {
    try {
      return topicRepository.findTopicById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying topic", e);
    }
  }

  @GET
  @Path("/transactions/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public TransactionInfo getTransaction(@PathParam("id") String id) {
    try {
      return transactionRepository.findById(id).orElseThrow();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying transaction", e);
    }
  }

  @POST
  @Path("/tokens")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String createToken(TokenRequest request) {
    try {
      return fungibleTokenClient.createToken(request.name(), request.symbol()).toString();
    } catch (final Exception e) {
      throw new RuntimeException("Error creating token", e);
    }
  }

  @POST
  @Path("/tokens/{id}/mint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public long mintToken(@PathParam("id") String id, MintRequest request) {
    try {
      return fungibleTokenClient.mintToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error minting token", e);
    }
  }

  @POST
  @Path("/tokens/{id}/burn")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public long burnToken(@PathParam("id") String id, MintRequest request) {
    try {
      return fungibleTokenClient.burnToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error burning token", e);
    }
  }

  @POST
  @Path("/tokens/{id}/transfer")
  @Consumes(MediaType.APPLICATION_JSON)
  public void transferToken(@PathParam("id") String id, TransferRequest request) {
    try {
      fungibleTokenClient.transferToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.toAccountId(), request.amount());
    } catch (final Exception e) {
      throw new RuntimeException("Error transferring token", e);
    }
  }

  @POST
  @Path("/tokens/{id}/associate")
  @Consumes(MediaType.APPLICATION_JSON)
  public void associateToken(@PathParam("id") String id, AssociateRequest request) {
    try {
      fungibleTokenClient.associateToken(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          request.toAccountId(),
          request.privateKey());
    } catch (final Exception e) {
      throw new RuntimeException("Error associating token", e);
    }
  }

  @POST
  @Path("/nfts")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String createNft(NftRequest request) {
    try {
      return nftClient.createNftType(request.name(), request.symbol()).toString();
    } catch (final Exception e) {
      throw new RuntimeException("Error creating NFT", e);
    }
  }

  @POST
  @Path("/nfts/{id}/mint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public long mintNft(@PathParam("id") String id, NftMintRequest request) {
    try {
      return nftClient.mintNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.metadata().getBytes());
    } catch (final Exception e) {
      throw new RuntimeException("Error minting NFT", e);
    }
  }

  @POST
  @Path("/nfts/{id}/burn")
  @Consumes(MediaType.APPLICATION_JSON)
  public void burnNft(@PathParam("id") String id, NftBurnRequest request) {
    try {
      nftClient.burnNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id), request.serialNumber());
    } catch (final Exception e) {
      throw new RuntimeException("Error burning NFT", e);
    }
  }

  @POST
  @Path("/nfts/{id}/transfer")
  @Consumes(MediaType.APPLICATION_JSON)
  public void transferNft(@PathParam("id") String id, NftTransferRequest request) {
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

  @POST
  @Path("/nfts/{id}/associate")
  @Consumes(MediaType.APPLICATION_JSON)
  public void associateNft(@PathParam("id") String id, AssociateRequest request) {
    try {
      nftClient.associateNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.privateKey()));
    } catch (final Exception e) {
      throw new RuntimeException("Error associating NFT", e);
    }
  }

  @POST
  @Path("/nfts/{id}/dissociate")
  @Consumes(MediaType.APPLICATION_JSON)
  public void dissociateNft(@PathParam("id") String id, AssociateRequest request) {
    try {
      nftClient.dissociateNft(
          com.hedera.hashgraph.sdk.TokenId.fromString(id),
          com.hedera.hashgraph.sdk.AccountId.fromString(request.toAccountId()),
          com.hedera.hashgraph.sdk.PrivateKey.fromString(request.privateKey()));
    } catch (final Exception e) {
      throw new RuntimeException("Error dissociating NFT", e);
    }
  }

  @POST
  @Path("/tokens/{id}/dissociate")
  @Consumes(MediaType.APPLICATION_JSON)
  public void dissociateToken(@PathParam("id") String id, AssociateRequest request) {
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
