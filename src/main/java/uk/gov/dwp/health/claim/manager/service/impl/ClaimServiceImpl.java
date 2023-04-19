package uk.gov.dwp.health.claim.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.claim.manager.config.properties.ClaimProperties;
import uk.gov.dwp.health.claim.manager.entity.Claim;
import uk.gov.dwp.health.claim.manager.entity.DrsRequestId;
import uk.gov.dwp.health.claim.manager.exception.ClaimNotFoundException;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCompleteObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimCreateObject;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimUpdateObject;
import uk.gov.dwp.health.claim.manager.repository.ClaimRepository;
import uk.gov.dwp.health.claim.manager.service.ClaimService;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.APPLICATION_SUBMITTED;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED;
import static uk.gov.dwp.health.claim.manager.openapi.model.ClaimRecordReturn.ClaimStatusEnum.fromValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

  private final ClaimRepository claimRepository;
  private final ClaimProperties claimProperties;
  private final WatcherConfigProperties watcherConfig;

  @Override
  public ResponseEntity<ClaimRecordReturn> returnOrCreateClaim(ClaimCreateObject requestBody) {
    final var claimantId = requestBody.getClaimantId();
    final var benefitType = requestBody.getBenefitType().toString();
    log.info(
        "Retrieve or Create new claim for claimant ID {} and benefit type {}",
        claimantId,
        benefitType);
    AtomicBoolean isNew = new AtomicBoolean(false);
    Claim claim =
        claimRepository
            .findClaimByClaimantIdAndBenefitCode(claimantId, benefitType)
            .orElseGet(
                () -> {
                  log.info("New claim being created for claimant ID {}", claimantId);
                  Claim newClaim = startNewClaim(requestBody);
                  isNew.set(true);
                  setChangeStreamChannel(newClaim);
                  log.info("No existing claim found and new claim created");
                  try {
                    return claimRepository.save(newClaim);
                  } catch (DuplicateKeyException ex) {
                    log.warn("A duplicate claim insert attempted {}", ex.getMessage());
                    log.info(
                        "Search on claimant id {} and benefit type {} 2nd attempt",
                        claimantId,
                        benefitType);
                    return claimRepository
                        .findClaimByClaimantIdAndBenefitCode(claimantId, benefitType)
                        .orElseThrow(
                            () ->
                                new ClaimNotFoundException(
                                    String.format(
                                        "No claim found on claimant ID %s on 2nd attempt",
                                        claimantId)));
                  }
                });
    var claimDto = new ClaimRecordReturn();
    claimDto.claimId(claim.getId());
    claimDto.setClaimStatus(fromValue(claim.getClaimStatus()));
    claimDto.setFormData(claim.getApplicationData());
    claimDto.setSubmissionId(claim.getSubmissionId() != null ? claim.getSubmissionId() : null);
    if (isNew.get()) {
      log.info("New claim created for claimant id {}", claimantId);
      return ResponseEntity.status(HttpStatus.CREATED).body(claimDto);
    }
    log.info("Existing claim found for claimant id {}", claimantId);
    return ResponseEntity.ok(claimDto);
  }

  private Claim startNewClaim(ClaimCreateObject body) {
    var newClaim = Claim.builder().build();
    LocalDate now = LocalDate.now();
    newClaim.setEffectiveFrom(now);
    newClaim.setEffectiveTo(now.plusDays(claimProperties.getActiveDuration()));
    newClaim.setClaimantId(body.getClaimantId());
    newClaim.setBenefitCode(body.getBenefitType().toString());
    newClaim.setApplicationData("{}");
    newClaim.setClaimStatus(CLAIM_STARTED.toString());
    return newClaim;
  }

  @Override
  public ResponseEntity<ClaimRecordReturn> updateClaim(ClaimUpdateObject requestBody) {
    log.info("Updating a claim");
    var claim =
        claimRepository
            .findClaimById(requestBody.getClaimId())
            .map(
                it -> {
                  it.setApplicationData(requestBody.getFormData());
                  setChangeStreamChannel(it);
                  return claimRepository.save(it);
                })
            .orElseThrow(
                () ->
                    new ClaimNotFoundException(
                        String.format("Claim id = %s not found", requestBody.getClaimId())));
    var claimDto = new ClaimRecordReturn();
    claimDto.claimId(claim.getId());
    claimDto.setClaimStatus(fromValue(claim.getClaimStatus()));
    claimDto.setFormData(claim.getApplicationData());
    claimDto.setSubmissionId(claim.getSubmissionId() != null ? claim.getSubmissionId() : null);
    return ResponseEntity.ok(claimDto);
  }

  @Override
  public ResponseEntity<Void> completeClaim(ClaimCompleteObject requestBody) {
    log.info("Completing claim");
    claimRepository
        .findClaimById(requestBody.getClaimId())
        .ifPresentOrElse(
            claim -> {
              claim.setClaimStatus(APPLICATION_SUBMITTED.getValue());
              claim.setSubmissionId(requestBody.getSubmissionId());
              if (requestBody.getDrsRequestId() != null
                  && !requestBody.getDrsRequestId().isBlank()) {
                claim.addDrsRequest(
                    DrsRequestId.builder().drsRequestId(requestBody.getDrsRequestId()).build());
              }
              claim.setCompleted(LocalDate.now());
              setChangeStreamChannel(claim);
              claimRepository.save(claim);
            },
            () -> {
              throw new ClaimNotFoundException(
                  String.format("Claim id = %s not found", requestBody.getClaimId()));
            });
    return ResponseEntity.ok().build();
  }

  @Override
  @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
  public ResponseEntity<ClaimStatus> queryClaimStatus(final String claimId) {
    log.info("Query claim");
    Claim claim =
        claimRepository
            .findClaimById(claimId)
            .orElseThrow(
                () -> {
                  throw new ClaimNotFoundException(
                      String.format("Claim id = %s not found", claimId));
                });
    var statusDto = new ClaimStatus();
    statusDto.setClaimStatus(
        uk.gov.dwp.health.claim.manager.openapi.model.ClaimStatus.ClaimStatusEnum.fromValue(
            claim.getClaimStatus()));
    return ResponseEntity.ok().body(statusDto);
  }

  private void setChangeStreamChannel(Claim claim) {
    watcherConfig.setChangeStreamChannel(claim, "claim");
  }
}
