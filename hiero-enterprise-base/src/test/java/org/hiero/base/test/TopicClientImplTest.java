package org.hiero.base.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import java.time.Duration;
import java.time.Instant;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.TopicClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.TopicCreateRequest;
import org.hiero.base.protocol.data.TopicCreateResult;
import org.hiero.base.protocol.data.TopicDeleteRequest;
import org.hiero.base.protocol.data.TopicDeleteResult;
import org.hiero.base.protocol.data.TopicSubmitMessageRequest;
import org.hiero.base.protocol.data.TopicSubmitMessageResult;
import org.hiero.base.protocol.data.TopicUpdateRequest;
import org.hiero.base.protocol.data.TopicUpdateResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class TopicClientImplTest {
  ProtocolLayerClient protocolLayerClient;
  Account operationalAccount;

  TopicClientImpl topicClient;

  ArgumentCaptor<TopicCreateRequest> topicCreateCaptor =
      ArgumentCaptor.forClass(TopicCreateRequest.class);
  ArgumentCaptor<TopicUpdateRequest> topicUpdateCaptor =
      ArgumentCaptor.forClass(TopicUpdateRequest.class);
  ArgumentCaptor<TopicDeleteRequest> topicDeleteCaptor =
      ArgumentCaptor.forClass(TopicDeleteRequest.class);
  ArgumentCaptor<TopicSubmitMessageRequest> topicSubmitCaptor =
      ArgumentCaptor.forClass(TopicSubmitMessageRequest.class);

  @BeforeEach
  void setup() {
    protocolLayerClient = Mockito.mock(ProtocolLayerClient.class);
    operationalAccount = Mockito.mock(Account.class);
    topicClient = new TopicClientImpl(protocolLayerClient, operationalAccount);
  }

  @Test
  void shouldCreateTopicWithOperationalAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createTopic();

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertNull(request.memo());
    Assertions.assertNull(request.submitKey());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldCreateTopicWithMemoAndOperationalAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String memo = "Hello Hiero";

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createTopic(memo);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(memo, request.memo());
    Assertions.assertNull(request.submitKey());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldCreateTopicForGivenAdminKey() throws HieroException {
    // mock
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");

    final PrivateKey adminKey = PrivateKey.generateECDSA();
    // when
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createTopic(adminKey);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertNull(request.submitKey());
    Assertions.assertNull(request.memo());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldThrowExceptionForNullParamDuringCreateTopic() {
    Assertions.assertThrows(NullPointerException.class, () -> topicClient.createTopic(null, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.createTopic((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.createTopic((PrivateKey) null));
  }

  @Test
  void shouldCreatePrivateTopicWithOperationalAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");

    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createPrivateTopic(submitKey);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertNull(request.memo());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldCreatePrivateTopicWithMemoAndOperationalAccountPrivateKeyAsAdminKey()
      throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String memo = "Hello Hiero";

    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createPrivateTopic(submitKey, memo);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertEquals(memo, request.memo());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldCreatePrivateTopicForGivenAdminKey() throws HieroException {
    // mock
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");

    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    // when
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);
    final TopicId result = topicClient.createPrivateTopic(adminKey, submitKey);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(topicCreateResult, times(1)).topicId();

    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldThrowExceptionForNullParamDuringCreatePrivateTopic() {
    Assertions.assertThrows(NullPointerException.class, () -> topicClient.createPrivateTopic(null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.createPrivateTopic(null, (PrivateKey) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.createPrivateTopic(null, null, null));
  }

  @Test
  void shouldUpdateTopicAllPropertiesAndUseOperatorAccountPrivateKeyAsAdminKey()
      throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String memo = "Hello Hiero";
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateTopic(topicId, updatedAdminKey, submitKey, memo);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(updatedAdminKey, request.updatedAdminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertEquals(memo, request.memo());
  }

  @Test
  void shouldUpdateTopicMemoAndUseOperatorAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String memo = "Updated Hello Hiero";

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateTopic(topicId, memo);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(memo, request.memo());
    Assertions.assertNull(request.updatedAdminKey());
    Assertions.assertNull(request.submitKey());
  }

  @Test
  void shouldUpdateTopicAllPropertiesForGivenAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String memo = "Hello Hiero";
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateTopic(topicId, adminKey, updatedAdminKey, submitKey, memo);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(updatedAdminKey, request.updatedAdminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertEquals(memo, request.memo());
  }

  @Test
  void shouldThrowExceptionForNullParamOnUpdateTopic() throws HieroException {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateTopic(null, null, null, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateTopic(null, null, null));
    Assertions.assertThrows(NullPointerException.class, () -> topicClient.updateTopic(null, null));
  }

  @Test
  void shouldUpdateAdminKeyAndUseOperatorAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateAdminKey(topicId, updatedAdminKey);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(updatedAdminKey, request.updatedAdminKey());
    Assertions.assertNull(request.submitKey());
    Assertions.assertNull(request.memo());
  }

  @Test
  void shouldUpdateAdminKeyForGivenAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();

    // when
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateAdminKey(topicId, adminKey, updatedAdminKey);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(updatedAdminKey, request.updatedAdminKey());
    Assertions.assertNull(request.submitKey());
    Assertions.assertNull(request.memo());
  }

  @Test
  void shouldThrowExceptionForNullParamOnUpdateAdminKey() throws HieroException {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateAdminKey(null, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateAdminKey(null, null, null));
  }

  @Test
  void shouldUpdateSubmitKeyAndUseOperatorAccountPrivateKeyAsAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateSubmitKey(topicId, submitKey);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertNull(request.updatedAdminKey());
    Assertions.assertNull(request.memo());
  }

  @Test
  void shouldUpdateSubmitKeyForGivenAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicUpdateResult topicUpdateResult = Mockito.mock(TopicUpdateResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(protocolLayerClient.executeTopicUpdateTransaction(any(TopicUpdateRequest.class)))
        .thenReturn(topicUpdateResult);
    topicClient.updateSubmitKey(topicId, adminKey, submitKey);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicUpdateTransaction(topicUpdateCaptor.capture());

    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertNull(request.updatedAdminKey());
    Assertions.assertNull(request.memo());
  }

  @Test
  void shouldThrowExceptionForNullParamOnUpdateSubmitKey() {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateSubmitKey(null, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateSubmitKey(null, null, null));
  }

  @Test
  void shouldDeleteTopicAndUseDefaultAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicDeleteResult topicDeleteResult = Mockito.mock(TopicDeleteResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(operationalAccount.privateKey()).thenReturn(adminKey);
    when(protocolLayerClient.executeTopicDeleteTransaction(any(TopicDeleteRequest.class)))
        .thenReturn(topicDeleteResult);
    topicClient.deleteTopic(topicId);

    // then
    verify(operationalAccount, times(1)).privateKey();
    verify(protocolLayerClient, times(1))
        .executeTopicDeleteTransaction(topicDeleteCaptor.capture());

    final TopicDeleteRequest request = topicDeleteCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
  }

  @Test
  void shouldDeleteTopicAndUseGivenAdminKey() throws HieroException {
    // mock
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicDeleteResult topicDeleteResult = Mockito.mock(TopicDeleteResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();

    // when
    when(protocolLayerClient.executeTopicDeleteTransaction(any(TopicDeleteRequest.class)))
        .thenReturn(topicDeleteResult);
    topicClient.deleteTopic(topicId, adminKey);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicDeleteTransaction(topicDeleteCaptor.capture());

    final TopicDeleteRequest request = topicDeleteCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
  }

  @Test
  void shouldThrowExceptionForNullParamOnUpdateDeleteTopic() {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.deleteTopic((TopicId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.deleteTopic((TopicId) null, null));
  }

  @Test
  void shouldSubmitMessageToTopic() throws HieroException {
    // mock
    final TopicSubmitMessageResult topicSubmitMessageResult =
        Mockito.mock(TopicSubmitMessageResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final byte[] message = "Hello Hiero".getBytes();

    // when
    when(protocolLayerClient.executeTopicMessageSubmitTransaction(
            any(TopicSubmitMessageRequest.class)))
        .thenReturn(topicSubmitMessageResult);
    topicClient.submitMessage(topicId, message);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicMessageSubmitTransaction(topicSubmitCaptor.capture());

    final TopicSubmitMessageRequest request = topicSubmitCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(message, request.message());
    Assertions.assertNull(request.submitKey());
  }

  @Test
  void shouldSubmitMessageToPrivateTopic() throws HieroException {
    // mock
    final TopicSubmitMessageResult topicSubmitMessageResult =
        Mockito.mock(TopicSubmitMessageResult.class);

    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final byte[] message = "Hello Hiero".getBytes();

    // when
    when(protocolLayerClient.executeTopicMessageSubmitTransaction(
            any(TopicSubmitMessageRequest.class)))
        .thenReturn(topicSubmitMessageResult);
    topicClient.submitMessage(topicId, submitKey, message);

    // then
    verify(protocolLayerClient, times(1))
        .executeTopicMessageSubmitTransaction(topicSubmitCaptor.capture());

    final TopicSubmitMessageRequest request = topicSubmitCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(message, request.message());
    Assertions.assertEquals(submitKey, request.submitKey());
  }

  @Test
  void shouldThrowExceptionIfMessageGreaterThanMaxLenOnSubmitMessage() throws HieroException {
    final String e_message = "Message cannot be longer than 1024 bytes";
    // given
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final byte[] message = new byte[1025];

    // then
    final IllegalArgumentException e1 =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> topicClient.submitMessage(topicId, message));
    final IllegalArgumentException e2 =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> topicClient.submitMessage(topicId, submitKey, message));

    Assertions.assertEquals(e_message, e1.getMessage());
    Assertions.assertEquals(e_message, e2.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullParamOnSubmitMessage() {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.submitMessage((TopicId) null, (String) null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> topicClient.submitMessage((TopicId) null, null, (String) null));
  }

  @Test
  void shouldCreateImmutableTopicWithoutAdminKey() throws HieroException {
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);

    final TopicId result = topicClient.createImmutableTopic("Immutable topic");

    verify(protocolLayerClient).executeTopicCreateTransaction(topicCreateCaptor.capture());
    verify(operationalAccount, never()).privateKey();
    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertNull(request.adminKey());
    Assertions.assertNull(request.submitKey());
    Assertions.assertEquals("Immutable topic", request.memo());
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldCreateTopicWithAutoRenewSettings() throws HieroException {
    final TopicCreateResult topicCreateResult = Mockito.mock(TopicCreateResult.class);
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final Account autoRenewAccount =
        Account.of(AccountId.fromString("0.0.1234"), PrivateKey.generateECDSA());
    final Duration autoRenewPeriod = Duration.ofDays(60);
    when(protocolLayerClient.executeTopicCreateTransaction(any(TopicCreateRequest.class)))
        .thenReturn(topicCreateResult);
    when(topicCreateResult.topicId()).thenReturn(topicId);

    final TopicId result =
        topicClient.createTopic(adminKey, submitKey, "memo", autoRenewAccount, autoRenewPeriod);

    verify(protocolLayerClient).executeTopicCreateTransaction(topicCreateCaptor.capture());
    final TopicCreateRequest request = topicCreateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(submitKey, request.submitKey());
    Assertions.assertEquals("memo", request.memo());
    Assertions.assertEquals(autoRenewAccount.accountId(), request.autoRenewAccountId());
    Assertions.assertEquals(autoRenewAccount.privateKey(), request.autoRenewAccountKey());
    Assertions.assertEquals(autoRenewPeriod, request.autoRenewPeriod());
    Assertions.assertEquals(topicId, result);
  }

  @Test
  void shouldUpdateAutoRenewAccountWithOperatorAdminKey() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");
    final Account autoRenewAccount =
        Account.of(AccountId.fromString("0.0.1234"), PrivateKey.generateECDSA());
    when(operationalAccount.privateKey()).thenReturn(adminKey);

    topicClient.updateAutoRenewAccount(topicId, autoRenewAccount);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(topicId, request.topicId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(autoRenewAccount.accountId(), request.autoRenewAccountId());
    Assertions.assertEquals(autoRenewAccount.privateKey(), request.autoRenewAccountKey());
    Assertions.assertFalse(request.clearAutoRenewAccountId());
  }

  @Test
  void shouldClearAutoRenewAccount() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");

    topicClient.clearAutoRenewAccount(topicId, adminKey);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertTrue(request.clearAutoRenewAccountId());
    Assertions.assertNull(request.autoRenewAccountId());
  }

  @Test
  void shouldUpdateAutoRenewPeriod() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");
    final Duration autoRenewPeriod = Duration.ofDays(45);

    topicClient.updateAutoRenewPeriod(topicId, adminKey, autoRenewPeriod);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(autoRenewPeriod, request.autoRenewPeriod());
  }

  @Test
  void shouldUpdateExpirationTimeWithoutAdminKey() throws HieroException {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final Instant expirationTime = Instant.now().plus(Duration.ofDays(91));

    topicClient.updateExpirationTime(topicId, expirationTime);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    verify(operationalAccount, never()).privateKey();
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertNull(request.adminKey());
    Assertions.assertEquals(expirationTime, request.expirationTime());
  }

  @Test
  void shouldClearAdminKeyWithOperatorAdminKey() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");
    when(operationalAccount.privateKey()).thenReturn(adminKey);

    topicClient.clearAdminKey(topicId);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertTrue(request.clearAdminKey());
    Assertions.assertTrue(request.clearAutoRenewAccountId());
    Assertions.assertNull(request.updatedAdminKey());
  }

  @Test
  void shouldClearSubmitKey() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");

    topicClient.clearSubmitKey(topicId, adminKey);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertTrue(request.clearSubmitKey());
    Assertions.assertNull(request.submitKey());
  }

  @Test
  void shouldClearTopicMemo() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = TopicId.fromString("1.2.3");

    topicClient.clearTopicMemo(topicId, adminKey);

    verify(protocolLayerClient).executeTopicUpdateTransaction(topicUpdateCaptor.capture());
    final TopicUpdateRequest request = topicUpdateCaptor.getValue();
    Assertions.assertTrue(request.clearMemo());
    Assertions.assertNull(request.memo());
  }

  @Test
  void shouldThrowExceptionForNullParamOnNewUpdateMethods() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.updateExpirationTime(topicId, null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> topicClient.updateAutoRenewPeriod(topicId, adminKey, null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> topicClient.updateAutoRenewAccount(topicId, adminKey, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.clearAdminKey(null, adminKey));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.clearSubmitKey(topicId, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicClient.createImmutableTopic(null));
  }
}
