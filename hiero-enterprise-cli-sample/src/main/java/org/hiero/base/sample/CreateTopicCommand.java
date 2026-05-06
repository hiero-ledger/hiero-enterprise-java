package org.hiero.base.sample;

import com.hedera.hashgraph.sdk.TopicId;
import org.hiero.base.TopicClient;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.implementation.TopicClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create-topic", description = "Create a new Hiero topic", mixinStandardHelpOptions = true)
public class CreateTopicCommand implements Runnable {

  @Option(names = {"-n", "--network"}, description = "Hiero network", defaultValue = "hedera-testnet")
  private String network;

  @Option(names = {"-a", "--account-id"}, description = "Operator account ID", required = true)
  private String accountId;

  @Option(names = {"-k", "--private-key"}, description = "Operator private key", required = true)
  private String privateKey;

  @Option(names = {"-m", "--memo"}, description = "Topic memo", defaultValue = "Created via Hiero CLI")
  private String memo;

  @Override
  public void run() {
    try {
      final CliHieroContext context = new CliHieroContext(accountId, privateKey, network);
      final ProtocolLayerClient protocolClient = new ProtocolLayerClientImpl(context);
      final TopicClient topicClient = new TopicClientImpl(protocolClient, context.getOperatorAccount());
      System.out.println("Creating topic on " + network + "...");
      final TopicId topicId = topicClient.createTopic(memo);
      System.out.println("Topic created: " + topicId);
    } catch (final Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
