package uk.gov.dwp.health.claim.manager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.health.claim.manager.entity.Claim;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, String> {

  Optional<Claim> findClaimByClaimantIdAndBenefitCode(String claimantId, String benefitCode);

  Optional<Claim> findClaimById(String claimId);

  List<Claim> findClaimByClaimantId(String claimantId);

  @Override
  Claim save(Claim claim);
}
