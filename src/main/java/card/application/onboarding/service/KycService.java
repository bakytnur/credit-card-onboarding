package card.application.onboarding.service;

import card.application.common.Helper;
import card.application.common.constants.VerificationStatus;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.response.KycResponse;
import card.application.onboarding.service.external.ComplianceService;
import card.application.onboarding.service.external.EmploymentVerificationService;
import card.application.onboarding.service.external.RiskEvaluationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static card.application.common.constants.Constants.*;
import static card.application.common.constants.VerificationStatus.*;

@Service
public class KycService {
    private final EmploymentVerificationService employmentVerificationService;
    private final ComplianceService complianceService;
    private final RiskEvaluationService riskEvaluationService;
    private final IdentityService identityService;

    public KycService(EmploymentVerificationService employmentVerificationService,
                      ComplianceService complianceService,
                      RiskEvaluationService riskEvaluationService,
                      IdentityService identityService) {
        this.employmentVerificationService = employmentVerificationService;
        this.complianceService = complianceService;
        this.riskEvaluationService = riskEvaluationService;
        this.identityService = identityService;
    }

    public KycResponse checkUserKyc(KycRequest request) {
        Helper.validateRequest(request);
        // Check identity verification complete
        var cardUser = identityService.getExistingIdentityForUser(request);
        KycResponse kycResponse = new KycResponse();
        if (cardUser == null) {
            kycResponse.setTotalScore(0);
            kycResponse.setStatus(VerificationStatus.IDENTITY_UNKNOWN.getState());
            return kycResponse;
        }

        // set initial score and state
        kycResponse.setStatus(VerificationStatus.IDENTITY_VERIFIED.getState());
        kycResponse.setTotalScore(IDENTITY_VERIFICATION_SCORE);

        // Run the employment verification in a separate thread
        CompletableFuture<Boolean> employmentCheckFuture = CompletableFuture.supplyAsync(() ->
                employmentVerificationService.verifyEmployment(request)
        );

        // Run the compliance check in a separate thread
        CompletableFuture<Boolean> complianceCheckFuture = CompletableFuture.supplyAsync(() ->
                complianceService.performComplianceCheck(request)
        );

        // Run the risk evaluation in a separate thread
        CompletableFuture<Integer> riskEvaluationFuture = CompletableFuture.supplyAsync(() ->
                riskEvaluationService.evaluateRisk(request)
        );

        // Combine the results of all three futures
        CompletableFuture.allOf(employmentCheckFuture, complianceCheckFuture, riskEvaluationFuture)
                .thenAccept(v -> {
                    try {
                        // Get the results of all futures
                        boolean employmentCheck = employmentCheckFuture.get();
                        boolean complianceCheck = complianceCheckFuture.get();
                        int riskEvaluation = riskEvaluationFuture.get();
                        if (employmentCheck) {
                            kycResponse.setTotalScore(kycResponse.getTotalScore() + EMPLOYMENT_VERIFICATION_SCORE);
                            kycResponse.setStatus(kycResponse.getStatus() | EMPLOYMENT_VERIFIED.getState());
                        }

                        if (complianceCheck) {
                            kycResponse.setTotalScore(kycResponse.getTotalScore() + COMPLIANCE_CHECK_SCORE);
                            kycResponse.setStatus(kycResponse.getStatus() | COMPLIANCE_CHECKED.getState());
                        }

                        // is risk evaluated
                        if (riskEvaluation > 0) {
                            kycResponse.setTotalScore(kycResponse.getTotalScore() + RISK_EVALUATED.getState());
                            kycResponse.setStatus(kycResponse.getStatus() | RISK_EVALUATED.getState());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing identity", e);
                    }
                });

        cardUser.setScore(kycResponse.getTotalScore());
        cardUser.setStatus(kycResponse.getStatus());
        identityService.saveUserIdentity(cardUser);
        return kycResponse;
    }
}
