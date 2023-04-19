package uk.gov.dwp.health.claim.manager.api.requestmodels.claim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class CompleteClaim {
  @Default
  @JsonProperty("claim_id")
  private final String claimId = "6ed1d430716609122be7a4d6";

  @JsonProperty("submission_id")
  @Default private final String submissionId = "6ed1d430716609122be7a4d6";

  @JsonProperty("drs_request_id")
  @Default private final String drsRequestId = "6ed1d430716609122be7a4d6";
}
