package uk.gov.dwp.health.claim.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationExceptionTest {

  @Test
  @DisplayName("Test validation exception")
  void testValidationException() {
    ValidationException actual = new ValidationException("validation failed");
    assertThat(actual.getMessage()).isEqualTo("validation failed");
  }
}
