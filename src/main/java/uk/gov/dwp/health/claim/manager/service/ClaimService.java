package uk.gov.dwp.health.claim.manager.service;

import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCompleteObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimUpdateObject;

public interface ClaimService {

  ResponseEntity<ClaimRecordReturn> returnOrCreateClaim(ClaimCreateObject requestBody);

  ResponseEntity<ClaimRecordReturn> updateClaim(ClaimUpdateObject requestBody);

  ResponseEntity<Void> completeClaim(ClaimCompleteObject claimCompleteObject);

  ResponseEntity<ClaimStatus> queryClaimStatus(String claimId);
}
