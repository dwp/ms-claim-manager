package uk.gov.dwp.health.claim.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.dwp.health.mongo.changestream.extension.MongoChangeStreamIdentifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
@Builder
@Document(collection = "claim")
public class Claim extends MongoChangeStreamIdentifier {

  @Id private String id;

  @Field(value = "claimant_id")
  private String claimantId;

  @Field(value = "benefit_code")
  private String benefitCode;

  @Field(value = "effective_from")
  private LocalDate effectiveFrom;

  @Field(value = "effective_to")
  private LocalDate effectiveTo;

  @Field(value = "application_data")
  private String applicationData;

  @Field(value = "completed")
  private LocalDate completed;

  @Field(value = "claim_status")
  private String claimStatus;

  @Field(value = "submission_id")
  private String submissionId;

  @Field(value = "drs_request_ids")
  private List<DrsRequestId> drsRequestId;

  public void addDrsRequest(final DrsRequestId requestId) {
    if (drsRequestId == null) {
      drsRequestId = new ArrayList<>();
    }
    requestId.setTimestamp(LocalDateTime.now());
    drsRequestId.add(requestId);
  }
}
