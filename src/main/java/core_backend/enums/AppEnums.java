package core_backend.enums;

public class AppEnums {
    public enum Role {
        CITIZEN, OFFICER, ADMIN
    }

    public enum ApplicationStatus {
        SUBMITTED, 
        VERIFICATION_IN_PROGRESS, 
        WAITING_FOR_DEPARTMENT, 
        OFFICER_REVIEW, 
        APPROVED, 
        REJECTED, 
        NEEDS_ATTENTION
    }

    public enum StepStatus {
        PENDING, 
        IN_PROGRESS, 
        COMPLETED, 
        REJECTED, 
        RETRY_PENDING, 
        FAILED_EXTERNAL
    }

    public enum ConsentStatus {
        ACTIVE, 
        REVOKED, 
        EXPIRED
    }
}