package uk.gov.dwp.health.claim.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimNotFoundExceptionTest {

  @Test
  @DisplayName("Test claim not found exception")
  void testClaimNotFoundException() {
    ClaimNotFoundException cut = new ClaimNotFoundException("Claim not found");
    assertThat(cut.getMessage()).isEqualTo("Claim not found");
  }
}
