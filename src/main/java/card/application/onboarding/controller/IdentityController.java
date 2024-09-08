package card.application.onboarding.controller;

import card.application.common.constants.VerificationStatus;
import card.application.onboarding.service.IdentityService;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/identity")
public class IdentityController {
    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/verification")
    public VerificationStatus verifyIdentity(@RequestBody IdentityVerificationRequest request) {
        return identityService.verifyIdentity(request);
    }
}
