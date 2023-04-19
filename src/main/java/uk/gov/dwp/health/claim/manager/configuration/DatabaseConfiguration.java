package uk.gov.dwp.health.claim.manager.configuration;

import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;

@Setter
@Configuration
public class DatabaseConfiguration {

  private static final ServerApiVersion API_VERSION = ServerApiVersion.V1;

  @Value("${feature.mongo.stable.api.enabled:true}")
  private boolean isMongoStableApiEnabled;

  @Bean
  public MongoClientSettingsBuilderCustomizer mongoSettings() {
    return builder -> {
      if (isMongoStableApiEnabled) {
        builder.serverApi(buildServerApi());
      }
    };
  }

  @Bean
  public MongoCustomConversions mongoJsrConversions() {
    return MongoCustomConversions.create(
        MongoConverterConfigurationAdapter::useNativeDriverJavaTimeCodecs);
  }

  private ServerApi buildServerApi() {
    return ServerApi.builder().strict(true).version(API_VERSION).build();
  }
}
