# Card Application Onboarding Service

This package provides services related to user identity verification and Know Your Customer (KYC) processes within the card application onboarding module. It manages identity verification, compliance checks, employment verification, and risk evaluation.

## Overview

The package includes the following components:

1. **`IdentityService`**: Handles user identity verification and storage.
2. **`KycService`**: Manages KYC checks, including employment verification, compliance checks, and risk evaluation.
3. **`IdentityController`**: Provides REST API endpoints for interacting with identity verification services.
4. **`KycController`**: Provides REST API endpoints for performing KYC checks.

## Classes

### `IdentityService`

The `IdentityService` class provides methods for:
- Verifying identities (`verifyIdentity`)
- Saving and retrieving user identity information (`saveUserIdentity`, `getExistingIdentityForUser`)

### `KycService`

The `KycService` class provides methods for conducting KYC checks and evaluating user profiles based on multiple criteria:

- **Dependencies:**
    - `MockEmploymentVerificationService`: A service for verifying employment details.
    - `MockComplianceService`: A service for performing compliance checks.
    - `MockRiskEvaluationService`: A service for evaluating risk.
    - `IdentityService`: Used to verify identity and retrieve existing user information.

- **Methods:**

    - `KycResponse checkUserKyc(KycRequest request)`:
      Performs a comprehensive KYC check for a user based on the provided `KycRequest`. The method:
        - Validates the request.
        - Checks if the user identity is verified. If the user identity is not verified, KYC will be rejected.
        - If verified, initializes the KYC response with a base score and status.
        - Calls `evaluateKycScore` to asynchronously perform employment verification, compliance checks, and risk evaluation.
        - Updates the user's score and status based on the results and saves the updated information.

    - `private void evaluateKycScore(KycRequest request, KycResponse kycResponse)`:
      Evaluates the KYC score by running employment verification, compliance checks, and risk evaluation in parallel using `CompletableFuture`. 
  
    - `private void setKycScoreAndStatus(KycResponse kycResponse, boolean employmentCheck, boolean complianceCheck, double riskEvaluation)`:
      Updates the KYC score and status based on the results of employment verification, compliance checks, and risk evaluation. It adjusts the total score and status flags accordingly.

    - `private int weightedScore(double value)`:
      Calculates a weighted score based on the risk evaluation value. The weighted score is used to update the total KYC score.

### `IdentityController`

The `IdentityController` class exposes a REST API for interacting with identity verification services:

- **Dependencies:**
    - `IdentityService`: Provides methods for identity verification.

- **Endpoints:**

    - `@PostMapping("/verification")`
        - **URL**: `/api/identity/verification`
        - **Method**: POST
        - **Request Body**: `IdentityVerificationRequest`
        - **Response**: `VerificationStatus`
        - **Description**: Verifies the identity of a user by delegating the request to `IdentityService`. Returns the verification status.

### `KycController`

The `KycController` class exposes a REST API for performing KYC checks:

- **Dependencies:**
    - `KycService`: Provides methods for conducting KYC checks.

- **Endpoints:**

    - `@PostMapping()`
        - **URL**: `/api/kyc`
        - **Method**: POST
        - **Request Body**: `KycRequest`
        - **Response**: `KycResponse`
        - **Description**: Performs KYC checks, including employment verification, compliance checks, and risk evaluation. Returns the KYC response.

## Entities

### `CardUser`

Represents a user in the card onboarding system. It includes fields for:
- `id`: The unique identifier of the user.
- `name`: The name of the user.
- `emiratesId`: The Emirates ID of the user (unique and mandatory).
- `status`: A combination of statuses. (The result of the bitwise OR operation)
- `score`: The credit score of the user (default is 0).
- `expiryDate`: The expiry date of the Emirates ID.
- `lastModifiedDate`: The date when the user information was last modified.
- `createdOn`: The date when the user information was created.

## Enum: `VerificationStatus`

The `VerificationStatus` enum represents various states of user verification and checks. It is used to track and manage the status of different verification processes within the application. Each status is associated with an integer value that helps in flagging and querying the state of a user's verification process.

### Enum Constants

- **`IDENTITY_UNKNOWN` (0)**: Indicates that the identity of the user is unknown or unverified.
- **`IDENTITY_VERIFIED` (1)**: Indicates that the user's identity has been successfully verified.
- **`COMPLIANCE_CHECKED` (2)**: Indicates that a compliance check has been performed on the user.
- **`EMPLOYMENT_VERIFIED` (4)**: Indicates that the user's employment status has been verified.
- **`RISK_EVALUATED` (8)**: Indicates that a risk evaluation has been completed for the user.
- **`BEHAVIORAL_ANALYSIS_CHECKED` (16)**: Indicates that a behavioral analysis check has been performed on the user.

### Final User Status

The final user status is a combination of multiple `VerificationStatus` values. These statuses are combined using bitwise `OR` operations to represent a comprehensive view of the user's verification state. For example:

- If a user has passed identity verification, employment verification, and risk evaluation, the combined status might be represented as `IDENTITY_VERIFIED | EMPLOYMENT_VERIFIED | RISK_EVALUATED`, which corresponds to the integer value `1 | 4 | 8 = 13`.

This bitwise approach allows for flexible and scalable management of multiple verification statuses, where each status is represented as a unique bit in an integer value.

### Usage

The `VerificationStatus` enum is used to manage and check the status of various verification processes in the system. The combination of statuses can be managed through bitwise operations to handle cases where multiple verification steps need to be tracked simultaneously.

Example usage:

```java
int combinedStatus = VerificationStatus.IDENTITY_VERIFIED.getState()
        | VerificationStatus.EMPLOYMENT_VERIFIED.getState()
        | VerificationStatus.RISK_EVALUATED.getState();

if ((combinedStatus & VerificationStatus.IDENTITY_VERIFIED.getState()) != 0) {
        // Identity verification has been completed
        }

        if ((combinedStatus & VerificationStatus.EMPLOYMENT_VERIFIED.getState()) != 0) {
        // Employment verification has been completed
        }

        if ((combinedStatus & VerificationStatus.RISK_EVALUATED.getState()) != 0) {
        // Risk evaluation has been completed
        }
```        
# Constants

- **IDENTITY_VERIFICATION_SCORE**: The base score for identity verification.
- **EMPLOYMENT_VERIFICATION_SCORE**: The score added for successful employment verification.
- **COMPLIANCE_CHECK_SCORE**: The score added for successful compliance checks.
- **RISK_EVALUATION_MAX_SCORE**: The maximum possible score for risk evaluation.

## Usage

1. **Initialization**: Create instances of the services (`KycService`, `IdentityService`) and configure the REST controllers (`IdentityController`, `KycController`).

2. **Perform Identity Verification via REST API**:
    - Send a POST request to `/api/identity/verification` with the `IdentityVerificationRequest` body to verify a user's identity.

3. **Perform KYC Check**:
    - Send a POST request to `/api/kyc` with the `KycRequest` body to perform KYC checks and receive the response.


### APIs can be tested using Swagger:
http://localhost:8080/swagger-ui/index.html


## Example
### REST API Usage for Identity Verification
```bash
curl --location 'http://localhost:8080/api/identity/verification' \
--header 'accept: */*' \
--header 'Content-Type: application/json' \
--data '{ "emiratesId": "784199123456781", "fullName": "Ali G"}'
```

```bash
curl --location 'http://localhost:8080/api/kyc' \
--header 'accept: */*' \
--header 'Content-Type: application/json' \
--data '{ "emiratesId": "784199123456789", "fullName": "Ali G", "mobileNumber": "0586606996", "employerId": "1"}'
```

