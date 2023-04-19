package uk.gov.dwp.health.claim.manager.migration;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import io.changock.driver.api.lock.LockCheckException;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.claim.manager.entity.Claim;
import uk.gov.dwp.health.claim.manager.repository.ClaimRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.APPLICATION_SUBMITTED;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED;

@TestInstance(PER_CLASS)
@DataMongoTest(properties = {"spring.mongodb.embedded.version=5.0.5"})
class ClaimDuplicationChangeLog001Test {

  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private ClaimRepository claimRepository;

  private static ClaimDuplicationChangeLog001 claimDuplicationChangeLog001;

  @BeforeAll
  void beforeAll() {
    claimRepository.deleteAll();
    claimDuplicationChangeLog001 = new ClaimDuplicationChangeLog001();
  }

  @Test
  void should_find_duplicate_claims_and_remove_them() {
    createDatabaseFixture();
    var lockGuardInvoker = fakeLockGuardInvoker();
    claimDuplicationChangeLog001.removeDuplicateClaims(
        new MongockTemplate(mongoTemplate, lockGuardInvoker), claimRepository);
    assertRemaining(1);
  }

  private LockGuardInvokerImpl fakeLockGuardInvoker() {
    return new LockGuardInvokerImpl(
        new LockManager() {
          @Override
          public void acquireLockDefault() throws LockCheckException {}

          @Override
          public void ensureLockDefault() throws LockCheckException {}

          @Override
          public void releaseLockDefault() {}

          @Override
          public LockManager setLockMaxWaitMillis(long l) {
            return null;
          }

          @Override
          public int getLockMaxTries() {
            return 1;
          }

          @Override
          public LockManager setLockMaxTries(int i) {
            return null;
          }

          @Override
          public LockManager setLockAcquiredForMillis(long l) {
            return null;
          }

          @Override
          public String getOwner() {
            return "fake-owner";
          }

          @Override
          public boolean isLockHeld() {
            return false;
          }

          @Override
          public void close() {}
        });
  }

  private void assertRemaining(int expected) {
    var actual = new AtomicInteger();
    claimRepository.findAll().forEach(claim -> actual.incrementAndGet());
    assertThat(actual.get()).isEqualTo(expected);
  }

  private void createDatabaseFixture() {
    var claim1 =
        Claim.builder()
            .claimantId("1")
            .benefitCode("PIP")
            .claimStatus(APPLICATION_SUBMITTED.toString())
            .applicationData("GOOD-DATA")
            .build();

    var claim2 =
        Claim.builder()
            .claimantId("1")
            .benefitCode("PIP")
            .claimStatus(CLAIM_STARTED.toString())
            .applicationData("{}")
            .build();

    var claim3 =
        Claim.builder()
            .claimantId("1")
            .benefitCode("PIP")
            .claimStatus(CLAIM_STARTED.toString())
            .applicationData("{WITH SOME DATA}")
            .build();
    claimRepository.saveAll(List.of(claim1, claim2, claim3));
  }
}
