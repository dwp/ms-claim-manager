package uk.gov.dwp.health.claim.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.TestFixtures;
import uk.gov.dwp.health.claim.manager.config.properties.ClaimProperties;
import uk.gov.dwp.health.claim.manager.entity.Claim;
import uk.gov.dwp.health.claim.manager.entity.DrsRequestId;
import uk.gov.dwp.health.claim.manager.exception.ClaimNotFoundException;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCompleteObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimUpdateObject;
import uk.gov.dwp.health.claim.manager.repository.ClaimRepository;
import uk.gov.dwp.health.mongo.changestream.config.properties.Channel;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject.BenefitTypeEnum.PIP;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

  @InjectMocks private ClaimServiceImpl claimService;
  @Mock private ClaimRepository repository;
  @Mock private ClaimProperties properties;
  @Mock private WatcherConfigProperties watcherConfigProperties;

  @Nested
  @DisplayName("Test return existing claim or create new claim")
  class ReturnOrCreateClaim {
    @Test
    @DisplayName("Test return a new claim")
    void testReturnANewClaim() {
      ClaimCreateObject requestBody = new ClaimCreateObject();
      requestBody.setClaimantId(TestFixtures.CLAIMANT_ID);
      requestBody.setBenefitType(PIP);
      Claim claim = mock(Claim.class);
      when(claim.getId()).thenReturn(TestFixtures.CLAIM_ID);
      when(claim.getApplicationData()).thenReturn("{}");
      when(claim.getClaimStatus()).thenReturn(CLAIM_STARTED.getValue());
      when(claim.getSubmissionId()).thenReturn(null);
      when(repository.findClaimByClaimantIdAndBenefitCode(anyString(), anyString()))
          .thenReturn(Optional.empty());
      when(repository.save(any(Claim.class))).thenReturn(claim);
      ResponseEntity<ClaimRecordReturn> actual = claimService.returnOrCreateClaim(requestBody);
      assertAll(
          "assert response entity",
          () -> assertEquals(HttpStatus.CREATED, actual.getStatusCode()),
          () -> assertEquals(TestFixtures.CLAIM_ID, actual.getBody().getClaimId()),
          () -> assertEquals(CLAIM_STARTED, actual.getBody().getClaimStatus()),
          () -> assertEquals("{}", actual.getBody().getFormData()));
    }

    @Test
    @DisplayName("Test return an existing claim")
    void testReturnAnExistingClaim() {
      ClaimCreateObject requestBody = new ClaimCreateObject();
      requestBody.setClaimantId(TestFixtures.CLAIMANT_ID);
      requestBody.setBenefitType(PIP);
      Claim claim = mock(Claim.class);
      when(claim.getId()).thenReturn(TestFixtures.CLAIM_ID);
      when(claim.getApplicationData()).thenReturn("{existing-data}");
      when(claim.getClaimStatus()).thenReturn(CLAIM_STARTED.getValue());
      when(claim.getSubmissionId()).thenReturn(null);
      when(repository.findClaimByClaimantIdAndBenefitCode(anyString(), anyString()))
          .thenReturn(Optional.of(claim));
      ResponseEntity<ClaimRecordReturn> actual = claimService.returnOrCreateClaim(requestBody);
      assertAll(
          "assert response entity",
          () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
          () -> assertEquals(TestFixtures.CLAIM_ID, actual.getBody().getClaimId()),
          () -> assertEquals(CLAIM_STARTED, actual.getBody().getClaimStatus()),
          () -> assertEquals("{existing-data}", actual.getBody().getFormData()));
      verify(repository, times(0)).save(any(Claim.class));
    }

    @Test
    @DisplayName("Test return an existing claim on 2nd attempt search")
    void testDuplicateKeyExceptionThrownAnd2ndSearchAttemptPerformed() {
      ClaimCreateObject requestBody = new ClaimCreateObject();
      requestBody.setClaimantId(TestFixtures.CLAIMANT_ID);
      requestBody.setBenefitType(PIP);
      Claim claim2ndAttempt =
          Claim.builder().claimStatus("CLAIM_STARTED").claimantId(TestFixtures.CLAIM_ID).build();
      when(repository.findClaimByClaimantIdAndBenefitCode(anyString(), anyString()))
          .thenReturn(Optional.empty())
          .thenReturn(Optional.of(claim2ndAttempt));
      doThrow(DuplicateKeyException.class).when(repository).save(any(Claim.class));
      claimService.returnOrCreateClaim(requestBody);
      verify(repository, times(2)).findClaimByClaimantIdAndBenefitCode(anyString(), anyString());
    }

    @Test
    @DisplayName("Test throw claim not found exception on 2nd attempt search")
    void testClaimNotFoundOnDuplicateKeyExceptionThrownAnd2ndAttemptSearchFail() {
      ClaimCreateObject requestBody = new ClaimCreateObject();
      requestBody.setClaimantId(TestFixtures.CLAIMANT_ID);
      requestBody.setBenefitType(PIP);
      when(repository.findClaimByClaimantIdAndBenefitCode(anyString(), anyString()))
          .thenReturn(Optional.empty())
          .thenReturn(Optional.empty());
      doThrow(DuplicateKeyException.class).when(repository).save(any(Claim.class));
      assertThatThrownBy(() -> claimService.returnOrCreateClaim(requestBody))
          .isInstanceOf(ClaimNotFoundException.class)
          .hasMessageStartingWith("No claim found on claimant ID");
      verify(repository, times(2)).findClaimByClaimantIdAndBenefitCode(anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("Test update a claim")
  class UpdateClaim {
    @Test
    @DisplayName("Test return a new claim")
    void testFoundClaimAndUpdateClaim() {
      ClaimUpdateObject requestBody = new ClaimUpdateObject();
      requestBody.setClaimId(TestFixtures.CLAIM_ID);
      requestBody.setFormData(TestFixtures.FORM_DATA);
      Claim claim = spy(Claim.builder().build());
      claim.setId(TestFixtures.CLAIM_ID);
      claim.setClaimStatus(CLAIM_STARTED.getValue());
      when(repository.findClaimById(anyString())).thenReturn(Optional.of(claim));
      when(repository.save(claim)).thenReturn(claim);
      ResponseEntity<ClaimRecordReturn> actual = claimService.updateClaim(requestBody);
      verify(claim).setApplicationData(TestFixtures.FORM_DATA);
      assertAll(
          "assert response entity",
          () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
          () -> assertEquals(TestFixtures.CLAIM_ID, actual.getBody().getClaimId()),
          () -> assertEquals(CLAIM_STARTED, actual.getBody().getClaimStatus()),
          () -> assertEquals(TestFixtures.FORM_DATA, actual.getBody().getFormData()));
    }

    @Test
    @DisplayName("Test given claim id not found claim not found exception thrown")
    void testGivenClaimIdNotFoundClaimNotFoundExceptionThrown() {
      ClaimUpdateObject requestBody = new ClaimUpdateObject();
      requestBody.setClaimId(TestFixtures.CLAIM_ID);
      requestBody.setFormData(TestFixtures.FORM_DATA);
      when(repository.findClaimById(anyString())).thenReturn(Optional.empty());
      assertThrows(ClaimNotFoundException.class, () -> claimService.updateClaim(requestBody));
    }
  }

  @Nested
  @DisplayName("Test complete a claim")
  class CompleteClaim {

    @Test
    @DisplayName("Test complete a claim")
    void testCompleteAClaim() {
      ClaimCompleteObject requestBody = new ClaimCompleteObject();
      requestBody.setClaimId(TestFixtures.CLAIM_ID);
      requestBody.setSubmissionId(TestFixtures.SUBMISSION_ID);
      requestBody.setDrsRequestId(TestFixtures.DRS_REQUEST_ID);
      Claim claim = spy(Claim.builder().build());
      claim.setId(TestFixtures.CLAIM_ID);
      claim.setClaimStatus(CLAIM_STARTED.getValue());
      when(repository.findClaimById(anyString())).thenReturn(Optional.of(claim));
      Channel channel = new Channel();
      channel.setCollection("claim");
      channel.setInstanceId("instance-id-123");
      ResponseEntity<Void> actual = claimService.completeClaim(requestBody);
      verify(claim)
          .setClaimStatus(ClaimRecordReturn.ClaimStatusEnum.APPLICATION_SUBMITTED.getValue());
      verify(claim).setCompleted(any(LocalDate.class));
      verify(claim).addDrsRequest(any(DrsRequestId.class));
      verify(repository).save(any(Claim.class));
      verify(watcherConfigProperties).setChangeStreamChannel(claim, "claim");
      assertEquals
              (HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    @DisplayName("Test claim is not found claimNotFoundExceptionThrown")
    void testClaimIsNotFoundClaimNotFoundExceptionThrown() {
      ClaimCompleteObject requestBody = new ClaimCompleteObject();
      requestBody.setClaimId(TestFixtures.CLAIM_ID);
      when(repository.findClaimById(anyString())).thenReturn(Optional.empty());
      assertThrows(ClaimNotFoundException.class, () -> claimService.completeClaim(requestBody));
    }
  }

  @Nested
  @DisplayName("Test query a claim status")
  class ClaimStatus {

    @Test
    @DisplayName("test query claim and status returned")
    void testQueryClaimAndStatusReturned() {
      String claimId = TestFixtures.CLAIM_ID;
      Claim claim = Claim.builder().build();
      claim.setClaimStatus(CLAIM_STARTED.getValue());
      when(repository.findClaimById(anyString())).thenReturn(Optional.of(claim));
      var actual = claimService.queryClaimStatus(claimId);
      assertAll(
          "assert response entity",
          () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
          () ->
              assertEquals(
                  uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus.ClaimStatusEnum
                      .CLAIM_STARTED,
                  actual.getBody().getClaimStatus()));
    }

    @Test
    @DisplayName("Test claim is not found claimNotFoundExceptionThrown")
    void testClaimIsNotFoundClaimNotFoundExceptionThrown() {
      String claimId = TestFixtures.CLAIM_ID;
      when(repository.findClaimById(anyString())).thenReturn(Optional.empty());
      assertThrows(ClaimNotFoundException.class, () -> claimService.queryClaimStatus(claimId));
    }
  }
}
