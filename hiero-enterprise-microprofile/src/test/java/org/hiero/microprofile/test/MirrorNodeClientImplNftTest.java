package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hiero.base.HieroException;
import org.hiero.base.data.Nft;
import org.hiero.base.data.Page;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MirrorNodeClientImplNftTest {

  private static final String MOCK_TARGET = "http://mock-target";
  private static final String ACCOUNT_NFTS_PATH = "/api/v1/accounts/0.0.1001/nfts";
  private static final String TOKEN_NFTS_PATH = "/api/v1/tokens/0.0.2002/nfts";
  private static final String NFT_METADATA = "test NFT";
  private static final String NFT_METADATA_BASE64 = "dGVzdCBORlQ=";
  private static final AccountId ACCOUNT_ID = AccountId.fromString("0.0.1001");
  private static final TokenId TOKEN_ID = TokenId.fromString("0.0.2002");

  private final MirrorNodeRestClientImpl restClient = mock(MirrorNodeRestClientImpl.class);
  private final Client client = mock(Client.class);
  private final WebTarget webTarget = mock(WebTarget.class);
  private final Invocation.Builder requestBuilder = mock(Invocation.Builder.class);
  private final Response response = mock(Response.class);
  private MockedStatic<ClientBuilder> clientBuilderMock;

  @BeforeEach
  void setUp() {
    when(restClient.getTarget()).thenReturn(MOCK_TARGET);
    clientBuilderMock = mockStatic(ClientBuilder.class);
    clientBuilderMock.when(ClientBuilder::newClient).thenReturn(client);
    when(client.target(MOCK_TARGET)).thenReturn(webTarget);
    when(webTarget.path(anyString())).thenReturn(webTarget);
    when(webTarget.queryParam(anyString(), any())).thenReturn(webTarget);
    when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(requestBuilder);
    when(requestBuilder.get()).thenReturn(response);
    when(response.readEntity(JsonObject.class)).thenReturn(emptyNftsResponse());
  }

  @AfterEach
  void tearDown() {
    if (clientBuilderMock != null) {
      clientBuilderMock.close();
    }
  }

  @Test
  void queryNftsByAccountHitsExpectedPath() throws HieroException {
    final Page<Nft> page = newClient().queryNftsByAccount(ACCOUNT_ID);

    assertNotNull(page);
    assertEquals(0, page.getSize());
    verify(client).target(MOCK_TARGET);
    verify(webTarget).path(ACCOUNT_NFTS_PATH);
    verify(webTarget, never()).queryParam(anyString(), any());
    verify(requestBuilder).get();
  }

  @Test
  void queryNftsByTokenIdHitsExpectedPath() throws HieroException {
    final Page<Nft> page = newClient().queryNftsByTokenId(TOKEN_ID);

    assertNotNull(page);
    assertEquals(0, page.getSize());
    verify(webTarget).path(TOKEN_NFTS_PATH);
    verify(webTarget, never()).queryParam(anyString(), any());
  }

  @Test
  void queryNftsByAccountAndTokenIdIncludesAccountFilter() throws HieroException {
    final Page<Nft> page = newClient().queryNftsByAccountAndTokenId(ACCOUNT_ID, TOKEN_ID);

    assertNotNull(page);
    assertEquals(0, page.getSize());
    verify(webTarget).path(TOKEN_NFTS_PATH);
    verify(webTarget).queryParam("account.id", "0.0.1001");
  }

  @Test
  void queryNftsByAccountParsesResponse() throws HieroException {
    when(response.readEntity(JsonObject.class)).thenReturn(singleNftResponse());

    final Page<Nft> page = newClient().queryNftsByAccount(ACCOUNT_ID);

    assertEquals(1, page.getSize());
    final List<Nft> data = page.getData();
    assertEquals(TOKEN_ID, data.get(0).tokenId());
    assertEquals(ACCOUNT_ID, data.get(0).owner());
    assertEquals(1L, data.get(0).serial());
    assertArrayEquals(NFT_METADATA.getBytes(StandardCharsets.UTF_8), data.get(0).metadata());
  }

  @Test
  void queryNftsByAccountRejectsNullInput() {
    assertThrows(
        NullPointerException.class, () -> newClient().queryNftsByAccount((AccountId) null));
    verify(client, never()).target(anyString());
  }

  @Test
  void queryNftsByTokenIdRejectsNullInput() {
    assertThrows(NullPointerException.class, () -> newClient().queryNftsByTokenId((TokenId) null));
    verify(client, never()).target(anyString());
  }

  private MirrorNodeClientImpl newClient() {
    return new MirrorNodeClientImpl(restClient, new MirrorNodeJsonConverterImpl());
  }

  private static JsonObject emptyNftsResponse() {
    return Json.createObjectBuilder()
        .add("nfts", Json.createArrayBuilder().build())
        .add("links", Json.createObjectBuilder().add("next", JsonObject.NULL).build())
        .build();
  }

  private static JsonObject singleNftResponse() {
    final JsonArray nfts =
        Json.createArrayBuilder()
            .add(
                Json.createObjectBuilder()
                    .add("account_id", "0.0.1001")
                    .add("created_timestamp", "1700000000.000000000")
                    .add("modified_timestamp", "1700000001.000000000")
                    .add("deleted", false)
                    .add("metadata", NFT_METADATA_BASE64)
                    .add("serial_number", 1)
                    .add("token_id", "0.0.2002")
                    .build())
            .build();
    return Json.createObjectBuilder()
        .add("nfts", nfts)
        .add("links", Json.createObjectBuilder().add("next", JsonObject.NULL).build())
        .build();
  }
}
