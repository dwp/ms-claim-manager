package uk.gov.dwp.health.claim.manager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import support.TestFixtures;
import uk.gov.dwp.health.claim.manager.entity.Claim;
import uk.gov.dwp.health.claim.manager.openapi.model.BenefitType.BenefitTypeEnum;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus.ClaimStatusEnum;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=5.0.5")
class ClaimRepositoryTest {

  @Autowired private ClaimRepository cut;

  @BeforeEach
  void setup() {
    cut.deleteAll();
  }

  private void createFixture() {
    final LocalDate currentDate = LocalDate.now();
    Claim claim =
        Claim.builder()
            .claimantId("12345")
            .id("claimId")
            .effectiveFrom(currentDate)
            .benefitCode("PIP")
            .applicationData("{}")
            .effectiveTo(currentDate.plusDays(93))
            .claimStatus(ClaimStatusEnum.CLAIM_STARTED.toString())
            .build();
    cut.save(claim);
  }

  @Nested
  @DisplayName("Tests for query claim")
  class QueryClaim {

    @Test
    @DisplayName("Test find a claim by claimant id and benefit code")
    void testFindAClaimByClaimantIdAndBenefitCode() throws Exception {
      createFixture();
      Claim actual =
          cut.findClaimByClaimantIdAndBenefitCode(
                  TestFixtures.CLAIMANT_ID, BenefitTypeEnum.PIP.getValue())
              .orElseThrow(() -> new Exception("Test failed"));
      assertClaim(actual);
    }

    @Test
    @DisplayName("Test find a claim by claim id")
    void testFindAClaimByClaimId() throws Exception {
      createFixture();
      Claim actual = cut.findClaimById("claimId").orElseThrow(() -> new Exception("Test failed"));
      assertClaim(actual);
    }

    private void assertClaim(Claim actual) {
      assertAll(
          "assert claim found",
          () -> assertEquals(TestFixtures.CLAIMANT_ID, actual.getClaimantId()),
          () -> assertEquals("claimId", actual.getId()),
          () -> assertEquals(ClaimStatusEnum.CLAIM_STARTED.toString(), actual.getClaimStatus()),
          () -> assertEquals(LocalDate.now(), actual.getEffectiveFrom()),
          () -> assertEquals("PIP", actual.getBenefitCode()),
          () -> assertEquals("{}", actual.getApplicationData()),
          () -> assertEquals(LocalDate.now().plusDays(93), actual.getEffectiveTo()));
    }
  }

  @Nested
  @DisplayName("Test find a claim by claimant id and benefit code")
  class CreateUpdateClaim {
    @BeforeEach
    void setup() {
      cut.deleteAll();
    }

    @Test
    @DisplayName("Test save a claim in the mongo db and claim returned")
    void testSaveAClaimInTheMongoDbAndClaimReturned() throws Exception {
      Claim claim = Claim.builder().build();
      claim.setClaimantId(TestFixtures.CLAIMANT_ID);
      claim.setApplicationData("{}");
      claim.setBenefitCode(BenefitTypeEnum.PIP.toString());
      claim.setEffectiveFrom(LocalDate.now());
      claim.setEffectiveTo(LocalDate.now().plusDays(93));
      claim.setClaimStatus(ClaimStatusEnum.CLAIM_STARTED.toString());
      cut.save(claim);
      Claim actual =
          cut.findClaimByClaimantIdAndBenefitCode(
                  TestFixtures.CLAIMANT_ID, BenefitTypeEnum.PIP.toString())
              .orElseThrow(() -> new Exception("Test failed"));
      assertAll(
          "assert claim found",
          () -> assertNotNull(actual.getId()),
          () -> assertEquals(TestFixtures.CLAIMANT_ID, actual.getClaimantId()),
          () -> assertEquals(ClaimStatusEnum.CLAIM_STARTED.toString(), actual.getClaimStatus()),
          () -> assertEquals(LocalDate.now(), actual.getEffectiveFrom()),
          () -> assertEquals("PIP", actual.getBenefitCode()),
          () -> assertEquals("{}", actual.getApplicationData()),
          () -> assertEquals(LocalDate.now().plusDays(93), actual.getEffectiveTo()));
    }
  }

  @Test
  void should_find_all_claims_belongs_to_claimant() {
    createFixture();
    assertThat(cut.findClaimByClaimantId("12345")).hasSize(1);
  }
}
