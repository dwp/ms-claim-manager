package uk.gov.dwp.health.claim.manager.api.v1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.TestFixtures;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCompleteObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimUpdateObject;
import uk.gov.dwp.health.claim.manager.service.impl.ClaimServiceImpl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus.ClaimStatusEnum.APPLICATION_SUBMITTED;

@ExtendWith(MockitoExtension.class)
class ClaimManagerApiImplTest {

  @InjectMocks private ClaimManagerApiImpl cut;
  @Mock private ClaimServiceImpl claimService;

  @Nested
  @DisplayName("Test claim endpoint")
  class ClaimEndpoint {
    @Test
    @DisplayName("test claim endpoint invocation service")
    void testClaimEndpointInvocationService() {
      ClaimCreateObject claimCreateObject = mock(ClaimCreateObject.class);
      ClaimRecordReturn claimRecordReturn = new ClaimRecordReturn();
      when(claimService.returnOrCreateClaim(any(ClaimCreateObject.class)))
          .thenReturn(ResponseEntity.ok(claimRecordReturn));
      ResponseEntity<ClaimRecordReturn> actual = cut.returnOrCreateClaim(claimCreateObject);
      verify(claimService).returnOrCreateClaim(claimCreateObject);
      assertAll(
          "assert returns",
          () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
          () -> assertEquals(claimRecordReturn, actual.getBody()));
    }
  }

  @Nested
  @DisplayName("Test claim update endpoint")
  class ClaimUpdateEndpoint {
    @Test
    @DisplayName("Test claim update endpoint and invocation service")
    void testClaimUpdateEndpointAndInvocationService() {
      ClaimUpdateObject claimUpdateObject = mock(ClaimUpdateObject.class);
      ClaimRecordReturn claimRecordReturn = new ClaimRecordReturn();
      when(claimService.updateClaim(any(ClaimUpdateObject.class)))
          .thenReturn(ResponseEntity.ok(claimRecordReturn));
      ResponseEntity<ClaimRecordReturn> actual = cut.updateClaim(claimUpdateObject);
      verify(claimService).updateClaim(claimUpdateObject);
      assertAll(
          "assert returns",
          () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
          () -> assertEquals(claimRecordReturn, actual.getBody()));
    }
  }

  @Nested
  @DisplayName("Test claim complete endpoint")
  class ClaimCompleteEndpoint {

    @Test
    @DisplayName("test claim complete endpoint and invocation service")
    void testClaimCompleteEndpointAndInvocationService() {
      ClaimCompleteObject claimCompleteObject = mock(ClaimCompleteObject.class);
      when(claimService.completeClaim(any(ClaimCompleteObject.class)))
          .thenReturn(ResponseEntity.ok().build());
      ResponseEntity<Void> actual = cut.claimComplete(claimCompleteObject);
      verify(claimService).completeClaim(claimCompleteObject);
      assertEquals(HttpStatus.OK, actual.getStatusCode());
    }
  }

  @Nested
  @DisplayName("Test claim status endpoint")
  class ClaimStatusEndpoint {
    @Test
    @DisplayName("Test claim status endpoint and invocation service")
    void testClaimStatusEndpointAndInvocationService() {
      String claimId = TestFixtures.CLAIM_ID;
      ClaimStatus status = new ClaimStatus();
      status.setClaimStatus(APPLICATION_SUBMITTED);
      when(claimService.queryClaimStatus(claimId)).thenReturn(ResponseEntity.ok().body(status));
      ResponseEntity<ClaimStatus> actual = cut.queryClaim(claimId);
      verify(claimService).queryClaimStatus(claimId);
      assertEquals(HttpStatus.OK, actual.getStatusCode());
      assertEquals("APPLICATION_SUBMITTED", actual.getBody().getClaimStatus().getValue());
    }
  }
}
