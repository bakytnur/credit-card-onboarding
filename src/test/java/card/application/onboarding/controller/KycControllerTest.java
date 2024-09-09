package card.application.onboarding.controller;

import card.application.common.exception.InputValidationException;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.response.KycResponse;
import card.application.onboarding.service.KycService;
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

@WebMvcTest(KycController.class)
public class KycControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KycService kycService;

    @Test
    void testVerifyIdentity_withValidRequest() throws Exception {
        KycRequest request = new KycRequest();
        request.setEmiratesId("784198612345670");
        request.setFullName("John Doe");
        request.setNationality("UAE");
        request.setEmployerId("Emp1");
        request.setMobileNumber("0586606996");

        when(kycService.checkUserKyc(request)).thenReturn(new KycResponse("784198612345670", 70, 15));

        mockMvc.perform(post("/api/kyc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emiratesId\":\"784198612345670\",\"fullName\":\"John Doe\",\"mobileNumber\":\"0586606996\",\"employerId\":\"Emp1\",\"nationality\":\"UAE\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"emiratesId\":\"784198612345670\",\"totalScore\":70,\"status\":15}"));
    }

    @Test
    void testVerifyIdentity_withInvalidFullName() throws Exception {
        KycRequest request = new KycRequest();
        request.setEmiratesId("784198612345670");
        request.setFullName("Invalid Name!@#");

        when(kycService.checkUserKyc(request)).thenThrow(new InputValidationException("Full name is invalid"));

        mockMvc.perform(post("/api/kyc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emiratesId\":\"784198612345670\",\"fullName\":\"Invalid Name!@#\"}"))
                .andExpect(status().isBadRequest());
    }
}
