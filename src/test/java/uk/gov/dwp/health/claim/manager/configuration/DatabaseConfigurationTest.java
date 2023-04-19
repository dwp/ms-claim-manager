package uk.gov.dwp.health.claim.manager.configuration;

import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;

@Tag("unit")
class DatabaseConfigurationTest {

  private DatabaseConfiguration databaseConfiguration;

  @BeforeEach
  public void beforeEach() {
    this.databaseConfiguration = new DatabaseConfiguration();
  }

  @Test
  @DisplayName("Mongo client does not return server api when stable api enabled is false")
  void buildMongoClientStableApiEnabledFalse() {
    databaseConfiguration.setMongoStableApiEnabled(false);
    Assertions.assertNull(build().getServerApi());
  }

  @Test
  @DisplayName("Mongo client returns strict server api when stable api enabled is true")
  void buildMongoClientStableApiEnabledTrue() {
    databaseConfiguration.setMongoStableApiEnabled(true);
    Assertions.assertTrue(build().getServerApi().getStrict().orElseThrow());
  }

  private MongoClientSettings build() {
    final MongoClientSettingsBuilderCustomizer clientSettingsBuilderCustomizer =
        databaseConfiguration.mongoSettings();
    final MongoClientSettings.Builder builder = MongoClientSettings.builder();
    clientSettingsBuilderCustomizer.customize(builder);
    return builder.build();
  }
}
