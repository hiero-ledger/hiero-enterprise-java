package org.hiero.base.sample;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "hiero",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Hiero Enterprise CLI - interact with the Hiero network",
    subcommands = {
      CreateAccountCommand.class,
      CreateTopicCommand.class,
      SendMessageCommand.class
    })
public class HieroCli implements Runnable {

  public static void main(final String[] args) {
    final int exitCode = new CommandLine(new HieroCli()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }
}
