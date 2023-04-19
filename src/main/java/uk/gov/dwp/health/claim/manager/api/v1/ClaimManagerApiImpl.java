package uk.gov.dwp.health.claim.manager.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.claim.manager.openapi.api.V1Api;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCompleteObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimUpdateObject;
import uk.gov.dwp.health.claim.manager.service.impl.ClaimServiceImpl;

@Controller
@RequiredArgsConstructor
public class ClaimManagerApiImpl implements V1Api {

  private final ClaimServiceImpl claimService;

  public ResponseEntity<ClaimRecordReturn> returnOrCreateClaim(
      ClaimCreateObject claimCreateObject) {
    return claimService.returnOrCreateClaim(claimCreateObject);
  }

  public ResponseEntity<ClaimRecordReturn> updateClaim(ClaimUpdateObject claimUpdateObject) {
    return claimService.updateClaim(claimUpdateObject);
  }

  public ResponseEntity<Void> claimComplete(ClaimCompleteObject claimCompleteObject) {
    return claimService.completeClaim(claimCompleteObject);
  }

  public ResponseEntity<ClaimStatus> queryClaim(String claimId) {
    return claimService.queryClaimStatus(claimId);
  }
}
