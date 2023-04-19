package uk.gov.dwp.health.claim.manager.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class ApiTest {
  static RequestSpecification requestSpec;

  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = getEnv("HOST", "http://localhost");
    RestAssured.port = Integer.parseInt(getEnv("PORT", "9931"));
    RestAssured.defaultParser = Parser.JSON;

    requestSpec =
        new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();
  }

  protected <T> T extractPostRequest(
          String path, Object bodyPayload, Class<T> responseClass) {
    return given()
        .spec(requestSpec)
        .body(bodyPayload)
        .when()
        .post(path)
        .then()
        .extract()
        .as(responseClass);
  }

  protected Response postRequest(String path, Object bodyPayload) {
    return given()
        .spec(requestSpec)
        .body(bodyPayload)
        .when()
        .post(path);
  }

  protected <T> T extractPatchRequest(
          String path, Object bodyPayload, Class<T> responseClass) {
    return given()
            .spec(requestSpec)
            .body(bodyPayload)
            .when()
            .patch(path)
            .then()
            .extract()
            .as(responseClass);
  }

  protected Response patchRequest(String path, Object bodyPayload) {
    return given()
            .spec(requestSpec)
            .body(bodyPayload)
            .when()
            .patch(path);
  }

  protected <T> T extractGetRequest(String path, Class<T> responseClass) {
    return given()
        .spec(requestSpec)
        .when()
        .get(path)
        .then()
        .extract()
        .as(responseClass);
  }

  protected Response getRequest(String path) {
    return given().spec(requestSpec).when().get(path);
  }

  private static String getEnv(String name, String defaultValue) {
    String env = System.getenv(name);
    return env == null ? defaultValue : env;
  }
}
