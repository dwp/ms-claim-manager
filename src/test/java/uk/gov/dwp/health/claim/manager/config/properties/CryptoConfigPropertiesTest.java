package uk.gov.dwp.health.claim.manager.config.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoConfigPropertiesTest {

  @Test
  void testKmsDataKeyPropIsRequired() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CryptoConfigProperties cut = new CryptoConfigProperties();
    cut.setMessageDataKeyId("");
    cut.setKmsOverride("");
    assertThat(validator.validate(cut).size()).isEqualTo(1);
    assertThat(cut.isKmsKeyCache()).isFalse();
  }

  @Test
  @DisplayName("test all getter and setter")
  void testAllGetterAndSetter() {
    CryptoConfigProperties cut = new CryptoConfigProperties();
    cut.setMessageDataKeyId("mock-data-key");
    cut.setKmsOverride("mock-kms-override");
    cut.setKmsKeyCache(true);
    assertThat(cut.getMessageDataKeyId()).isEqualTo("mock-data-key");
    assertThat(cut.getKmsOverride()).isEqualTo("mock-kms-override");
    assertThat(cut.isKmsKeyCache()).isTrue();
  }
}
