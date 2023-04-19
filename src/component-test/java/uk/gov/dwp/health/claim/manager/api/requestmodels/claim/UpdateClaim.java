package uk.gov.dwp.health.claim.manager.api.requestmodels.claim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UpdateClaim {
  @Default
  @JsonProperty("claim_id")
  private final String claimId = "6ed1d430716609122be7a4d6";

  @JsonProperty("form_data")
  @Default
  private final String formData = "{\"data\", \"claim data\"}";
}


