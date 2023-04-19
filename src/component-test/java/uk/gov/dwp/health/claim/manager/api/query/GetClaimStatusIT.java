package uk.gov.dwp.health.claim.manager.api.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.claim.manager.api.ApiTest;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.CreateClaim;
import uk.gov.dwp.health.claim.manager.api.responsemodels.CreatedClaim;
import uk.gov.dwp.health.claim.manager.api.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildGetClaimStatusUrl;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPostClaimUrl;

public class GetClaimStatusIT extends ApiTest {
    String url;

    @BeforeEach
    public void createClaim() {
        CreateClaim createClaim = CreateClaim.builder().claimantId(RandomStringUtil.generate(24)).build();
        CreatedClaim createdClaim = extractPostRequest(buildPostClaimUrl(), createClaim, CreatedClaim.class);
        url = buildGetClaimStatusUrl(createdClaim.getClaimId());
    }

    @Test
    public void shouldReturn200StatusCode() {
        int actualResponseCode = getRequest(url).statusCode();

        assertThat(actualResponseCode).isEqualTo(200);
    }
}
