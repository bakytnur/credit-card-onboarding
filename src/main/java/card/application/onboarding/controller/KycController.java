package card.application.onboarding.controller;

import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.response.KycResponse;
import card.application.onboarding.service.KycService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kyc")
public class KycController {
    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    /**
     * Checks Employment Verification, Compliance Check and Risk Evaluation
     * @param request
     * @return
     */
    @PostMapping()
    public KycResponse checkUserKyc(@RequestBody KycRequest request) {
        return kycService.checkUserKyc(request);
    }
}
