package card.application.onboarding.controller;

import card.application.onboarding.service.KycService;
import card.onboarding.model.IdentityVerificationStatus;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc")
public class KycController {
    @Autowired
    private KycService kycService;

    @PostMapping("/verification")
    public IdentityVerificationStatus verifyIdentity(@RequestBody IdentityVerificationRequest request) {
        return kycService.verifyIdentity(request);
    }

}
