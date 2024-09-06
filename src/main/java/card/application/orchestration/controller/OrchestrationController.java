package card.application.orchestration.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestration")
public class OrchestrationController {
    @PostMapping("/aml")
    public card.onboarding.model.IdentityVerificationStatus verifyIdentity() {
        return card.onboarding.model.IdentityVerificationStatus.IDENTITY_VERIFIED;
    }
}
