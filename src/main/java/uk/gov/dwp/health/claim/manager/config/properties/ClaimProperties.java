package uk.gov.dwp.health.claim.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Configuration
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.claim")
public class ClaimProperties {

  @Min(value = 1, message = "Active duration must be grater or equals 1")
  private int activeDuration;
}
