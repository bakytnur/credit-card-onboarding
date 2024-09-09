package card.application.onboarding.service;

import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.response.KycResponse;
import card.application.onboarding.service.mock.MockComplianceService;
import card.application.onboarding.service.mock.MockEmploymentVerificationService;
import card.application.onboarding.service.mock.MockRiskEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static card.application.common.constants.Constants.*;
import static card.application.common.constants.VerificationStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KycServiceTest {

    @Mock
    private MockEmploymentVerificationService employmentVerificationService;

    @Mock
    private MockComplianceService complianceService;

    @Mock
    private MockRiskEvaluationService riskEvaluationService;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private KycService kycService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckUserKyc_withVerifiedIdentity() throws Exception {
        // Mock the request and existing user in the DB
        KycRequest request = new KycRequest("123456789012345",
                "John Doe", "0561234567", "Employer123", "UAE");
        CardUser existingUser = new CardUser(IDENTITY_VERIFIED.getState());
        existingUser.setScore(IDENTITY_VERIFICATION_SCORE);

        // Mock the identity service to return the existing user
        when(identityService.getExistingIdentityForUser(any())).thenReturn(existingUser);

        // Mock the services' responses
        when(employmentVerificationService.verifyEmployment(any())).thenReturn(true);
        when(complianceService.performComplianceCheck(any())).thenReturn(true);
        when(riskEvaluationService.evaluateRisk(any())).thenReturn(50.0);

        // Run the method under test
        KycResponse response = kycService.checkUserKyc(request);

        // Verify the KYC response
        assertEquals(IDENTITY_VERIFIED.getState() | EMPLOYMENT_VERIFIED.getState() | COMPLIANCE_CHECKED.getState() | RISK_EVALUATED.getState(), response.getStatus());
        // 20 + 20 + 20 + 20 * (50/100)
        assertEquals(IDENTITY_VERIFICATION_SCORE + EMPLOYMENT_VERIFICATION_SCORE + COMPLIANCE_CHECK_SCORE + 10, response.getTotalScore());

        // Verify that the user identity is saved
        verify(identityService).saveUserIdentity(any());
    }

    @Test
    void testCheckUserKyc_withUnknownIdentity() throws Exception {
        // Mock the request and no existing user in the DB
        KycRequest request = new KycRequest("123456789012345", "John Doe",
                "0561234567","Employer123",  "UAE");

        // Mock the identity service to return null
        when(identityService.getExistingIdentityForUser(any())).thenReturn(null);

        // Run the method under test
        KycResponse response = kycService.checkUserKyc(request);

        // Verify the KYC response for unknown identity
        assertEquals(0, response.getTotalScore());
        assertEquals(IDENTITY_UNKNOWN.getState(), response.getStatus());

        // Verify that the user identity is not saved
        verify(identityService, never()).saveUserIdentity(any());
    }

    @Test
    void testCheckUserKyc_withServiceExceptions() throws Exception {
        // Mock the request and existing user in the DB
        KycRequest request = new KycRequest("123456789012345", "John Doe",
                "0561234567", "Employer123", "UAE");
        CardUser existingUser = new CardUser(IDENTITY_VERIFIED.getState());
        existingUser.setScore(IDENTITY_VERIFICATION_SCORE);

        // Mock the identity service to return the existing user
        when(identityService.getExistingIdentityForUser(any())).thenReturn(existingUser);

        // Mock the services to throw exceptions
        when(employmentVerificationService.verifyEmployment(any())).thenThrow(new RuntimeException("Employment service error"));
        when(complianceService.performComplianceCheck(any())).thenThrow(new RuntimeException("Compliance service error"));
        when(riskEvaluationService.evaluateRisk(any())).thenThrow(new RuntimeException("Risk evaluation service error"));

        // Run the method under test and catch the exception
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> kycService.checkUserKyc(request));

        // Verify the exception message
        assertEquals("java.lang.RuntimeException: Employment service error", exception.getMessage());
    }
}
