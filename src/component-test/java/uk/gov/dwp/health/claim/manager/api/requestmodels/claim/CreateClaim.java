package uk.gov.dwp.health.claim.manager.api.requestmodels.claim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class CreateClaim {
  @Default
  @JsonProperty("claimant_id")
  private final String claimantId = "6ed1d430716609122be7a4d6";

  @JsonProperty("benefit_type")
  @Default private final String benefitType = "PIP";
}
