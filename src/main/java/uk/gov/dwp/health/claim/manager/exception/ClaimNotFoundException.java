package uk.gov.dwp.health.claim.manager.exception;

public class ClaimNotFoundException extends RuntimeException {
  public ClaimNotFoundException(final String msg) {
    super(msg);
  }
}
