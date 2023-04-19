package uk.gov.dwp.health.claim.manager.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.dwp.health.claim.manager.exception.ClaimNotFoundException;
import uk.gov.dwp.health.claim.manager.openapi.model.ErrorResponseObject;

import javax.validation.ConstraintViolationException;

@Component
@ControllerAdvice
public class AppControllerAdvise {

  private static Logger log = LoggerFactory.getLogger(AppControllerAdvise.class);

  @ExceptionHandler({ClaimNotFoundException.class})
  public ResponseEntity<Void> handle401(ClaimNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(
      value = {
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class
      })
  public ResponseEntity<ErrorResponseObject> handle400(Exception ex) {
    log.warn("Request validation failed");
    var body = new ErrorResponseObject();
    body.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler({IncorrectResultSizeDataAccessException.class})
  public ResponseEntity<ErrorResponseObject> handleNonUniqueResult(
      IncorrectResultSizeDataAccessException ex) {
    log.warn("None unique result");
    var body = new ErrorResponseObject();
    body.setMessage("Found more than 1 claim");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Void> handleUnknown(Exception ex) {
    log.error("Unknown server error");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
