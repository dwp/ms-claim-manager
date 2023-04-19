package uk.gov.dwp.health.claim.manager.api.claim;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.claim.manager.api.requestmodels.claim.CreateClaim;
import uk.gov.dwp.health.claim.manager.api.ApiTest;
import uk.gov.dwp.health.claim.manager.api.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.claim.manager.api.utils.UrlBuilderUtil.buildPostClaimUrl;

public class PostClaimIT extends ApiTest {
  String url = buildPostClaimUrl();

  @Test
  public void shouldReturn201StatusCode() {
    CreateClaim createClaim = CreateClaim.builder().claimantId(RandomStringUtil.generate(24)).build();

    int actualResponseCode = postRequest(url, createClaim).statusCode();

    assertThat(actualResponseCode).isEqualTo(201);
  }

  @Test
  public void shouldReturn200StatusCodeIfClaimAlreadyExists() {
    CreateClaim createClaim = CreateClaim.builder().claimantId(RandomStringUtil.generate(24)).build();

    postRequest(url, createClaim);

    int actualResponseCode = postRequest(url, createClaim).statusCode();

    assertThat(actualResponseCode).isEqualTo(200);
  }
}