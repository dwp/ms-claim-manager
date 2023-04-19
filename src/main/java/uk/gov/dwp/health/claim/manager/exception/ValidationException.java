package uk.gov.dwp.health.claim.manager.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(final String msg) {
    super(msg);
  }
}
