package uk.gov.dwp.health.claim.manager.api.claim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.claim.manager.api.ApiTest;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.CompleteClaim;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.CreateClaim;
import uk.gov.dwp.health.claim.manager.api.responsemodels.CreatedClaim;
import uk.gov.dwp.health.claim.manager.api.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPostClaimCompleteUrl;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPostClaimUrl;

public class PostClaimCompleteIT extends ApiTest {
    String url = buildPostClaimCompleteUrl();
    CreatedClaim createdClaim;

    @BeforeEach
    public void createClaim() {
        CreateClaim createClaim = CreateClaim.builder().claimantId(RandomStringUtil.generate(24)).build();
        createdClaim = extractPostRequest(buildPostClaimUrl(), createClaim, CreatedClaim.class);
    }

    @Test
    public void shouldReturn200StatusCode() {
        CompleteClaim completeClaim = CompleteClaim.builder().claimId(createdClaim.getClaimId()).build();

        int actualResponseCode = postRequest(url, completeClaim).statusCode();

        assertThat(actualResponseCode).isEqualTo(200);
    }
}
