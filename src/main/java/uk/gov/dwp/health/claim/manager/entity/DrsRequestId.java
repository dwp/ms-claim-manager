package uk.gov.dwp.health.claim.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
@Builder
public class DrsRequestId {

  @Field(value = "drs_request_id")
  private String drsRequestId;

  @Field(value = "time_stamp")
  private LocalDateTime timestamp;
}
