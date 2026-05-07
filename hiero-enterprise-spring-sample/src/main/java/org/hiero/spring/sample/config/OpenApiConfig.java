package org.hiero.spring.sample.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI hieroEnterpriseOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Hiero Enterprise Spring Sample API")
                .description(
                    "Interactive REST API for Hiero Enterprise Java integration. "
                        + "Provides endpoints for Accounts, Tokens, NFTs, Consensus Topics, and Files.")
                .version("v0.20.0")
                .contact(
                    new Contact()
                        .name("Hiero Enterprise Team")
                        .url("https://github.com/hiero-ledger/hiero-enterprise-java"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
  }

  @Bean
  public OpenApiCustomizer sortTagsCustomizer() {
    return openApi -> {
      final List<String> order =
          List.of(
              "Accounts",
              "Fungible Tokens",
              "Non-Fungible Tokens",
              "Consensus Topics",
              "Blocks",
              "Network",
              "Files");

      List<Tag> tags = openApi.getTags();
      if (tags != null) {
        // Create a copy to avoid modification issues during sorting if it's a fixed-size list
        List<Tag> sortedTags = new ArrayList<>(tags);
        sortedTags.sort(
            Comparator.comparingInt(
                tag -> {
                  int index = order.indexOf(tag.getName());
                  return index == -1 ? order.size() : index;
                }));
        openApi.setTags(sortedTags);
      }
    };
  }
}
