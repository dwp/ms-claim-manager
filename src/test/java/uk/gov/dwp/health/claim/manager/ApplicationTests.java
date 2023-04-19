package uk.gov.dwp.health.claim.manager;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.claim.manager.api.v1.ClaimManagerApiImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    properties = {
      "app.claim.active-duration=93",
      "submission.base-url=https://dwp.gov.uk",
      "submission.create-path: /v1/create",
      "aws.encryption.messageDataKeyId=mock-message-data-key",
            "spring.mongodb.embedded.version=5.0.5"
    })
class ApplicationTests {

  @Autowired private ClaimManagerApiImpl claimController;
  @MockBean MongoClient mongoClient;
  @MockBean MongockConnectionDriver mongockConnectionDriver;
  @MockBean MongockSpring5.Builder mongoBuilder;

  @Test
  void contextLoads() {
    assertThat(claimController).isNotNull();
  }
}
