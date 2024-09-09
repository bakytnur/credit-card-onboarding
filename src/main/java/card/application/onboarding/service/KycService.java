package card.application.onboarding.service;

import card.application.common.Helper;
import card.application.onboarding.model.request.ComplianceCheckRequest;
import card.application.onboarding.model.request.EmploymentRequest;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.request.RiskEvaluationRequest;
import card.application.onboarding.model.response.KycResponse;
import card.application.onboarding.service.mock.MockComplianceService;
import card.application.onboarding.service.mock.MockEmploymentVerificationService;
import card.application.onboarding.service.mock.MockRiskEvaluationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static card.application.common.constants.Constants.*;
import static card.application.common.constants.VerificationStatus.*;

@Service
public class KycService {
    private final MockEmploymentVerificationService employmentVerificationService;
    private final MockComplianceService complianceService;
    private final MockRiskEvaluationService riskEvaluationService;
    private final IdentityService identityService;

    public KycService(MockEmploymentVerificationService employmentVerificationService,
                      MockComplianceService complianceService,
                      MockRiskEvaluationService riskEvaluationService,
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
        var kycResponse = new KycResponse();
        if (cardUser == null) {
            kycResponse.setTotalScore(0);
            kycResponse.setStatus(IDENTITY_UNKNOWN.getState());
            return kycResponse;
        }

        // set initial score and state
        kycResponse.setEmiratesId(request.getEmiratesId());
        kycResponse.setStatus(IDENTITY_VERIFIED.getState());
        kycResponse.setTotalScore(IDENTITY_VERIFICATION_SCORE);

        evaluateKycScore(request, kycResponse);

        cardUser.setScore(kycResponse.getTotalScore());
        cardUser.setStatus(kycResponse.getStatus());
        identityService.saveUserIdentity(cardUser);
        return kycResponse;
    }

    private void evaluateKycScore(KycRequest request, KycResponse kycResponse) {
        // Run the employment verification in a separate thread
        CompletableFuture<Boolean> employmentCheckFuture = CompletableFuture.supplyAsync(() -> {
                    var employmentRequest = new EmploymentRequest(request.getEmiratesId(),
                            request.getFullName(),
                            request.getEmployerId());
                    return employmentVerificationService.verifyEmployment(employmentRequest);
                }
        );

        // Run the compliance check in a separate thread
        CompletableFuture<Boolean> complianceCheckFuture = CompletableFuture.supplyAsync(() -> {
                    var complianceCheckRequest = new ComplianceCheckRequest(request.getEmiratesId(),
                            request.getFullName(),
                            request.getMobileNumber(),
                            request.getNationality());
                    return complianceService.performComplianceCheck(complianceCheckRequest);
                }
        );

        // Run the risk evaluation in a separate thread
        CompletableFuture<Double> riskEvaluationFuture = CompletableFuture.supplyAsync(() -> {
                var riskEvaluationRequest = new RiskEvaluationRequest(request.getEmiratesId(),
                        request.getFullName());
                return riskEvaluationService.evaluateRisk(riskEvaluationRequest);
                }
        );

        // Combine the results of all three futures
        CompletableFuture.allOf(employmentCheckFuture, complianceCheckFuture, riskEvaluationFuture)
                .thenAccept(v -> {
                    try {
                        // Get the results of all futures
                        boolean employmentCheck = employmentCheckFuture.get();
                        boolean complianceCheck = complianceCheckFuture.get();
                        double riskEvaluation = riskEvaluationFuture.get();
                        setKycScoreAndStatus(kycResponse, employmentCheck, complianceCheck, riskEvaluation);
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing identity", e);
                    }
                }).join();
    }

    private void setKycScoreAndStatus(KycResponse kycResponse, boolean employmentCheck, boolean complianceCheck, double riskEvaluation) {
        if (employmentCheck) {
            kycResponse.setTotalScore(kycResponse.getTotalScore() + EMPLOYMENT_VERIFICATION_SCORE);
            kycResponse.setStatus(kycResponse.getStatus() | EMPLOYMENT_VERIFIED.getState());
        }

        if (complianceCheck) {
            kycResponse.setTotalScore(kycResponse.getTotalScore() + COMPLIANCE_CHECK_SCORE);
            kycResponse.setStatus(kycResponse.getStatus() | COMPLIANCE_CHECKED.getState());
        }

        // is risk evaluated
        assert riskEvaluation >= 0 && riskEvaluation <= 100;

        kycResponse.setTotalScore(kycResponse.getTotalScore() + weightedScore(riskEvaluation));
        kycResponse.setStatus(kycResponse.getStatus() | RISK_EVALUATED.getState());
    }

    private int weightedScore(double value) {
        return (int) (value * RISK_EVALUATION_MAX_SCORE / (double )100);
    }
}
