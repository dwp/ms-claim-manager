package uk.gov.dwp.health.claim.manager.api.claim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.claim.manager.api.ApiTest;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.CreateClaim;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.UpdateClaim;
import uk.gov.dwp.health.claim.manager.api.responsemodels.CreatedClaim;
import uk.gov.dwp.health.claim.manager.api.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPatchClaimUpdateUrl;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPostClaimUrl;

public class PatchClaimUpdateIT extends ApiTest {
    String url = buildPatchClaimUpdateUrl();
    CreatedClaim createdClaim;

    @BeforeEach
    public void createClaim() {
        CreateClaim createClaim = CreateClaim.builder().claimantId(RandomStringUtil.generate(24)).build();
        createdClaim = extractPostRequest(buildPostClaimUrl(), createClaim, CreatedClaim.class);
    }

    @Test
    public void shouldReturn200StatusCode() {
        UpdateClaim updateClaim = UpdateClaim.builder().claimId(createdClaim.getClaimId()).build();

        int actualResponseCode = patchRequest(url, updateClaim).statusCode();

        assertThat(actualResponseCode).isEqualTo(200);
    }
}
