package uk.gov.dwp.health.claim.manager.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.AggregateIterable;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import uk.gov.dwp.health.claim.manager.repository.ClaimRepository;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.gt;
import static java.util.Arrays.asList;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED;

@ChangeLog(order = "001")
@Slf4j
public class ClaimDuplicationChangeLog001 {

  @ChangeSet(order = "001", author = "PIP-apply ms-claim-manager", id = "removeDuplicateClaims")
  public void removeDuplicateClaims(
      MongockTemplate mongoTemplate, ClaimRepository claimRepository) {
    log.info("Migration 001 started");
    var aggregateIterable = findDuplicateClaimByClaimantId(mongoTemplate);
    var it = aggregateIterable.iterator();
    it.forEachRemaining(
        doc -> {
          final var claimantId = (String) doc.get("_id");
          claimRepository
              .findClaimByClaimantId(claimantId)
              .forEach(
                  claim -> {
                    if (CLAIM_STARTED.toString().equals(claim.getClaimStatus())) {
                      log.info(
                          "Migration: delete duplicate claim ID {} for claimant id {}",
                          claim.getId(),
                          claim.getClaimantId());
                      claimRepository.delete(claim);
                      log.info("Migration: deleted");
                    }
                  });
        });
    log.info("Migration 001 completed");
  }

  private AggregateIterable<Document> findDuplicateClaimByClaimantId(MongockTemplate template) {
    return template
        .getDb()
        .getCollection("claim")
        .aggregate(asList(group("$claimant_id", sum("count", 1)), match(gt("count", 1))));
  }
}
