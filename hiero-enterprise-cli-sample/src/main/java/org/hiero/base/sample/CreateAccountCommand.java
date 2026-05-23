package org.hiero.base.sample;

import org.hiero.base.AccountClient;
import org.hiero.base.implementation.AccountClientImpl;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.data.Account;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create-account", description = "Create a new Hiero account", mixinStandardHelpOptions = true)
public class CreateAccountCommand implements Runnable {

  @Option(names = {"-n", "--network"}, description = "Hiero network", defaultValue = "hedera-testnet")
  private String network;

  @Option(names = {"-a", "--account-id"}, description = "Operator account ID", required = true)
  private String accountId;

  @Option(names = {"-k", "--private-key"}, description = "Operator private key", required = true)
  private String privateKey;

  @Override
  public void run() {
    try {
      final CliHieroContext context = new CliHieroContext(accountId, privateKey, network);
      final ProtocolLayerClient protocolClient = new ProtocolLayerClientImpl(context);
      final AccountClient accountClient = new AccountClientImpl(protocolClient);
      System.out.println("Creating account on " + network + "...");
      final Account account = accountClient.createAccount();
      System.out.println("Account created: " + account.accountId());
    } catch (final Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
