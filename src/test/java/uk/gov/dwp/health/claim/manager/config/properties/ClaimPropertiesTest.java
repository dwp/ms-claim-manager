package uk.gov.dwp.health.claim.manager.config.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimPropertiesTest {

  private static Validator validator;
  private ClaimProperties underTest;

  @BeforeAll
  static void setupSpec() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @BeforeEach
  void setup() {
    underTest = new ClaimProperties();
  }

  @Test
  void testNoValueValidationConstraintOnFields() {
    assertThat(validator.validate(underTest).size()).isEqualTo(1);
  }

  @Test
  void testMinValidationConstraintOnFields() {
    underTest.setActiveDuration(0);
    assertThat(validator.validate(underTest).size()).isEqualTo(1);
  }

  @Test
  void testGetSetActiveDuration() {
    underTest.setActiveDuration(93);
    assertThat(underTest.getActiveDuration()).isEqualTo(93);
  }
}
