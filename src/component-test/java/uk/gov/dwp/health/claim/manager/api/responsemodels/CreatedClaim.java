package uk.gov.dwp.health.claim.manager.api.responsemodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class CreatedClaim {
    @JsonProperty("claim_id")
    private String claimId;

    @JsonProperty("submission_id")
    private String submissionId;

    @JsonProperty("form_data")
    private String formData;

    @JsonProperty("claim_status")
    private String claimStatus;
}
