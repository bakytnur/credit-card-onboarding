package card.application.orchestration.controller;

import card.application.common.constants.VerificationStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestration")
public class OrchestrationController {
    @PostMapping("/aml")
    public VerificationStatus verifyIdentity() {
        return VerificationStatus.IDENTITY_VERIFIED;
    }
}
