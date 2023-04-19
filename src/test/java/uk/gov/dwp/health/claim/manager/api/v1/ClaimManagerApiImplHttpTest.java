package uk.gov.dwp.health.claim.manager.api.v1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import support.TestFixtures;
import uk.gov.dwp.health.claim.manager.api.AppControllerAdvise;
import uk.gov.dwp.health.claim.manager.entity.Claim;
import uk.gov.dwp.health.claim.manager.openapi.model.ClaimId;
import uk.gov.dwp.health.claim.manager.repository.ClaimRepository;
import uk.gov.dwp.health.claim.manager.service.impl.ClaimServiceImpl;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(
    classes = {
      ClaimManagerApiImpl.class,
      AppControllerAdvise.class,
      ClaimRepository.class,
      ClaimServiceImpl.class
    })
@WebMvcTest
class ClaimManagerApiImplHttpTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private ClaimRepository claimRepository;
  @MockBean private ClaimServiceImpl claimService;

  @Test
  @DisplayName("Test get claim status")
  void testGetClaimStatus() throws Exception {
    ClaimId claimId = new ClaimId();
    claimId.setClaimId(TestFixtures.CLAIM_ID);
    Claim claim = Claim.builder().build();
    claim.setClaimStatus("CLAIM_STARTED");
    when(claimRepository.findClaimById(TestFixtures.CLAIM_ID)).thenReturn(Optional.of(claim));
    mockMvc.perform(get("/v1/claim/status/" + TestFixtures.CLAIM_ID)).andExpect(status().isOk());
  }
}
