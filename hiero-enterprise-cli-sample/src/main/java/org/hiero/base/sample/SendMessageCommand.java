package org.hiero.base.sample;

import org.hiero.base.TopicClient;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.implementation.TopicClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "send-message", description = "Send a message to a Hiero topic", mixinStandardHelpOptions = true)
public class SendMessageCommand implements Runnable {

  @Option(names = {"-n", "--network"}, description = "Hiero network", defaultValue = "hedera-testnet")
  private String network;

  @Option(names = {"-a", "--account-id"}, description = "Operator account ID", required = true)
  private String accountId;

  @Option(names = {"-k", "--private-key"}, description = "Operator private key", required = true)
  private String privateKey;

  @Option(names = {"-t", "--topic-id"}, description = "Topic ID", required = true)
  private String topicId;

  @Option(names = {"-msg", "--message"}, description = "Message content", required = true)
  private String message;

  @Override
  public void run() {
    try {
      final CliHieroContext context = new CliHieroContext(accountId, privateKey, network);
      final ProtocolLayerClient protocolClient = new ProtocolLayerClientImpl(context);
      final TopicClient topicClient = new TopicClientImpl(protocolClient, context.getOperatorAccount());
      System.out.println("Sending message to topic " + topicId + "...");
      topicClient.submitMessage(topicId, message);
      System.out.println("Message sent successfully!");
    } catch (final Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
