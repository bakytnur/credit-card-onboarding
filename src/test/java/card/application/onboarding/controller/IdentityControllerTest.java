package card.application.onboarding.controller;

import card.application.common.constants.VerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.service.IdentityService;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdentityController.class)
public class IdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentityService identityService;

    @Test
    void testVerifyIdentity_withValidRequest() throws Exception {
        IdentityVerificationRequest request = new IdentityVerificationRequest();
        request.setEmiratesId("784198612345670");
        request.setFullName("John Doe");

        when(identityService.verifyIdentity(request)).thenReturn(VerificationStatus.IDENTITY_VERIFIED);

        mockMvc.perform(post("/api/identity/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emiratesId\":\"784198612345670\",\"fullName\":\"John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + VerificationStatus.IDENTITY_VERIFIED.name() + "\""));
    }

    @Test
    void testVerifyIdentity_withInvalidEmiratesId() throws Exception {
        IdentityVerificationRequest request = new IdentityVerificationRequest();
        request.setEmiratesId("invalid-emirates-id");
        request.setFullName("John Doe");

        when(identityService.verifyIdentity(request)).thenThrow(new InputValidationException("Invalid Emirates Id"));

        mockMvc.perform(post("/api/identity/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emiratesId\":\"invalid-emirates-id\",\"fullName\":\"John Doe\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyIdentity_withInvalidFullName() throws Exception {
        IdentityVerificationRequest request = new IdentityVerificationRequest();
        request.setEmiratesId("784198612345670");
        request.setFullName("Invalid Name!@#");

        when(identityService.verifyIdentity(request)).thenThrow(new InputValidationException("Full name is invalid"));

        mockMvc.perform(post("/api/identity/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emiratesId\":\"784198612345670\",\"fullName\":\"Invalid Name!@#\"}"))
                .andExpect(status().isBadRequest());
    }
}