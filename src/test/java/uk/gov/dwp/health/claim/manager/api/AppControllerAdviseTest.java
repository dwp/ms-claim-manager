package uk.gov.dwp.health.claim.manager.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.claim.manager.exception.ClaimNotFoundException;
import uk.gov.dwp.health.claim.manager.openapi.model.ErrorResponseObject;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AppControllerAdviseTest {

  private static AppControllerAdvise underTest;
  private TestLogger testLogger = TestLoggerFactory.getTestLogger(AppControllerAdvise.class);

  @BeforeAll
  static void setupSpec() {
    underTest = new AppControllerAdvise();
  }

  @BeforeEach
  void setup() {
    testLogger.clearAll();
    ReflectionTestUtils.setField(underTest, "log", testLogger);
  }

  @Test
  void testHandle400ClaimNotFoundException() {
    ClaimNotFoundException exp = new ClaimNotFoundException("given claim not found");
    ResponseEntity<Void> actual = underTest.handle401(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(actual.getBody()).isNull();
  }

  @Test
  @DisplayName("Test handle 400 validation failure")
  void testHandle400ValidationFailure() {
    Exception exp = new Exception("fail validation");
    ResponseEntity<ErrorResponseObject> actual = underTest.handle400(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(actual.getBody()).getMessage()).isEqualTo("fail validation");
    assertThat(testLogger.getLoggingEvents())
        .containsExactly(new LoggingEvent(Level.WARN, "Request validation failed"));
  }

  @Test
  void should_return_conflict_on_none_unique_result() {
    var exp = mock(IncorrectResultSizeDataAccessException.class);
    ResponseEntity<ErrorResponseObject> actual = underTest.handleNonUniqueResult(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(Objects.requireNonNull(actual.getBody()).getMessage())
        .isEqualTo("Found more than 1 claim");
  }
}
