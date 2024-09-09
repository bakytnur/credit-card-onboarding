package card.application.onboarding.service;

import card.application.common.constants.VerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.request.IdentityVerificationRequest;
import card.application.onboarding.model.response.EcaResponse;
import card.application.onboarding.repository.IdentityRepository;
import card.application.onboarding.service.mock.MockEcaService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdentityServiceTest {

    @Mock
    private MockEcaService mockEcaService;

    @Mock
    private IdentityRepository identityRepository;

    @InjectMocks
    private IdentityService identityService;

    private IdentityVerificationRequest request;

    @BeforeEach
    void setUp() {
        request = new IdentityVerificationRequest();
        request.setEmiratesId("784198612345670");
        request.setFullName("John Doe");
    }

    @SneakyThrows
    @Test
    void testVerifyIdentity_withValidExistingUser() {
        CardUser existingUser = new CardUser();
        existingUser.setExpiryDate(LocalDate.now().plusDays(1));
        existingUser.setStatus(VerificationStatus.IDENTITY_VERIFIED.getState());

        when(identityRepository.findByEmiratesId(anyString())).thenReturn(Optional.of(existingUser));

        VerificationStatus status = identityService.verifyIdentity(request);

        assertEquals(VerificationStatus.IDENTITY_VERIFIED, status);
        verify(mockEcaService, never()).getMockUserIdentity(anyString(), anyString());
    }

    @SneakyThrows
    @Test
    void testVerifyIdentity_withInvalidEmiratesId() {
        request.setEmiratesId("invalid-emirates-id");

        assertThrows(InputValidationException.class, () -> identityService.verifyIdentity(request));
        verify(mockEcaService, never()).getMockUserIdentity(anyString(), anyString());
    }

    @SneakyThrows
    @Test
    void testVerifyIdentity_withInvalidFullName() {
        request.setFullName("Invalid Name!@#");

        assertThrows(InputValidationException.class, () -> identityService.verifyIdentity(request));
        verify(mockEcaService, never()).getMockUserIdentity(anyString(), anyString());
    }

    @Test
    void testVerifyIdentity_withNewUser() throws IOException, InterruptedException {
        EcaResponse ecaResponse = new EcaResponse();
        ecaResponse.setValid(true);
        ecaResponse.setExpiryDate("2024-09-07");

        when(identityRepository.findByEmiratesId(anyString())).thenReturn(Optional.empty());
        when(mockEcaService.getMockUserIdentity(anyString(), anyString())).thenReturn(ecaResponse);

        VerificationStatus status = identityService.verifyIdentity(request);

        assertEquals(VerificationStatus.IDENTITY_VERIFIED, status);
        verify(identityRepository, times(1)).save(any(CardUser.class));
    }

    @Test
    void testVerifyIdentity_withNewUser_invalidIdentity() throws IOException, InterruptedException {
        EcaResponse ecaResponse = new EcaResponse();
        ecaResponse.setValid(false);
        ecaResponse.setExpiryDate("2024-09-09");

        when(identityRepository.findByEmiratesId(anyString())).thenReturn(Optional.empty());
        when(mockEcaService.getMockUserIdentity(anyString(), anyString())).thenReturn(ecaResponse);

        VerificationStatus status = identityService.verifyIdentity(request);

        assertEquals(VerificationStatus.IDENTITY_UNKNOWN, status);
        verify(identityRepository, times(1)).save(any(CardUser.class));
    }
}