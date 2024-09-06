package card.application.orchestration.controller;

import card.application.common.constants.IdentityVerificationStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestration")
public class OrchestrationController {
    @PostMapping("/aml")
    public IdentityVerificationStatus verifyIdentity() {
        return IdentityVerificationStatus.IDENTITY_VERIFIED;
    }
}
