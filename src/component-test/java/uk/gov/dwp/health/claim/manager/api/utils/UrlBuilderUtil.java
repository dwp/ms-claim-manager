package uk.gov.dwp.health.claim.manager.api.utils;

import static io.restassured.RestAssured.baseURI;

public class UrlBuilderUtil {
    public static String buildPostClaimUrl() {
        return  baseURI + "/v1/claim";
    }

    public static String buildPatchClaimUpdateUrl() {
        return baseURI + "/v1/claim/update";
    }

    public static String buildPostClaimCompleteUrl() {
        return baseURI + "/v1/claim/complete";
    }

    public static String buildGetClaimStatusUrl(String claimId) {
        return baseURI + "/v1/claim/status/" +  claimId;
    }
}
