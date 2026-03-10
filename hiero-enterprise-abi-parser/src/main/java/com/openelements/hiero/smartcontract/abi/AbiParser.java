package com.openelements.hiero.smartcontract.abi;

import com.openelements.hiero.smartcontract.abi.model.AbiModel;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Interface for parsing ABI (Application Binary Interface) data.
 *
 * <p>This interface provides methods to parse ABI data from various sources, including URLs and
 * strings. Implementations of this interface should provide the actual parsing logic.
 */
public interface AbiParser {

  /**
   * Parses the ABI data from the given URL.
   *
   * @param url the URL to parse
   * @return the parsed ABI model
   * @throws AbiParserException if an error occurs while parsing the ABI data
   */
  default @NonNull AbiModel parse(@NonNull URL url) throws AbiParserException {
    Objects.requireNonNull(url, "url");
    try {
      return parse(Path.of(url.toURI()));
    } catch (URISyntaxException e) {
      throw new AbiParserException("Error in converting url to uri: '" + url + "'", e);
    }
  }

  /**
   * Parses the ABI data from the given file path.
   *
   * @param abiPath the path to the ABI file
   * @return the parsed ABI model
   * @throws AbiParserException if an error occurs while parsing the ABI data
   */
  default @NonNull AbiModel parse(@NonNull Path abiPath) throws AbiParserException {
    Objects.requireNonNull(abiPath, "abiPath");
    try {
      return parse(Files.readString(abiPath));
    } catch (IOException e) {
      throw new AbiParserException("Error in reading abi data from file '" + abiPath + "'", e);
    }
  }

  /**
   * Parses the ABI data from the given string.
   *
   * @param abi the ABI data as a string
   * @return the parsed ABI model
   * @throws AbiParserException if an error occurs while parsing the ABI data
   */
  default @NonNull AbiModel parse(@NonNull String abi) throws AbiParserException {
    Objects.requireNonNull(abi, "abi");
    return parse(new StringReader(abi));
  }

  /**
   * Parses the ABI data from the given reader.
   *
   * @param abiReader the reader to read the ABI data from
   * @return the parsed ABI model
   * @throws AbiParserException if an error occurs while parsing the ABI data
   */
  @NonNull AbiModel parse(@NonNull Reader abiReader) throws AbiParserException;
}
