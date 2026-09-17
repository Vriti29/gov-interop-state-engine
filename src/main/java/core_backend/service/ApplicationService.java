package core_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import core_backend.dto.ApplicationStatusResponse;
import core_backend.dto.ApplicationSubmitRequest;
import core_backend.dto.OfficerReviewRequest;
import core_backend.entity.Application;
import core_backend.enums.AppEnums.ApplicationStatus;
import core_backend.enums.AppEnums.StepStatus;
import core_backend.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // 1. Submit Application
    public Application submitApplication(ApplicationSubmitRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Application application = Application.builder()
                .citizenId(request.getCitizenId())
                .serviceType(request.getServiceType() != null ? request.getServiceType() : "STUDENT_SCHOLARSHIP")
                .overallStatus(ApplicationStatus.OFFICER_REVIEW)
                .identityStep(StepStatus.PENDING)
                .revenueStep(StepStatus.PENDING)
                .educationStep(StepStatus.PENDING)
                .submittedAt(now)
                .deadlineAt(now.plusDays(7)) // Right to Services SLA: 7 days timer
                .build();

        return applicationRepository.save(application);
    }

    // 2. Fetch Granular Status for Tracking Screen
    public ApplicationStatusResponse getApplicationStatus(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        boolean isBreached = LocalDateTime.now().isAfter(app.getDeadlineAt());

        return ApplicationStatusResponse.builder()
                .applicationId(app.getId())
                .citizenId(app.getCitizenId())
                .serviceType(app.getServiceType())
                .overallStatus(app.getOverallStatus())
                .identityStep(app.getIdentityStep())
                .revenueStep(app.getRevenueStep())
                .educationStep(app.getEducationStep())
                .submittedAt(app.getSubmittedAt())
                .deadlineAt(app.getDeadlineAt())
                .slaBreached(isBreached)
                .reviewedBy(app.getReviewedBy())
                .build();
    }

    // 3. Update Individual Department Steps (Edge-case safe & Frozen Protected)
    public Application updateDepartmentStep(Long applicationId, String department, StepStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        // FROZEN STATE MACHINE GUARD
        if (app.getOverallStatus() == ApplicationStatus.APPROVED || app.getOverallStatus() == ApplicationStatus.REJECTED) {
            throw new IllegalStateException("Workflow is FROZEN: Application has reached terminal state (" + app.getOverallStatus() + ") and cannot be modified.");
        }

        switch (department.toUpperCase()) {
            case "IDENTITY" -> app.setIdentityStep(status);
            case "REVENUE" -> {
                app.setRevenueStep(status);
                if (status == StepStatus.IN_PROGRESS) {
                    app.setRevenueStartedAt(LocalDateTime.now());
                }
            }
            case "EDUCATION" -> app.setEducationStep(status);
            default -> throw new IllegalArgumentException("Unknown department: " + department);
        }

        // State Machine Resolution Logic
        if (app.getIdentityStep() == StepStatus.REJECTED || 
            app.getRevenueStep() == StepStatus.REJECTED || 
            app.getEducationStep() == StepStatus.REJECTED) {
            
            app.setOverallStatus(ApplicationStatus.REJECTED);

        } else if (app.getIdentityStep() == StepStatus.FAILED_EXTERNAL || 
                   app.getRevenueStep() == StepStatus.FAILED_EXTERNAL || 
                   app.getEducationStep() == StepStatus.FAILED_EXTERNAL ||
                   app.getIdentityStep() == StepStatus.RETRY_PENDING || 
                   app.getRevenueStep() == StepStatus.RETRY_PENDING || 
                   app.getEducationStep() == StepStatus.RETRY_PENDING) {
            
            // Technical failure maps to WAITING_FOR_DEPARTMENT (Not false rejection)
            app.setOverallStatus(ApplicationStatus.WAITING_FOR_DEPARTMENT);

        } else if (app.getIdentityStep() == StepStatus.COMPLETED && 
                   app.getRevenueStep() == StepStatus.COMPLETED && 
                   app.getEducationStep() == StepStatus.COMPLETED) {
            
            app.setOverallStatus(ApplicationStatus.OFFICER_REVIEW);
        } else {
            app.setOverallStatus(ApplicationStatus.VERIFICATION_IN_PROGRESS);
        }

        return applicationRepository.save(app);
    }

    // 4. Officer Approval / Rejection Action (Frozen Protected + State Guard)
    public Application reviewApplication(Long applicationId, OfficerReviewRequest review, String officerId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        // FROZEN STATE MACHINE GUARD
        if (app.getOverallStatus() == ApplicationStatus.APPROVED || app.getOverallStatus() == ApplicationStatus.REJECTED) {
            throw new IllegalStateException("Workflow is FROZEN: Application is already finalized (" + app.getOverallStatus() + ").");
        }

        // --- FIX (Audit Point 10): Ensure application is actually ready for officer review ---
        if (app.getOverallStatus() != ApplicationStatus.OFFICER_REVIEW) {
            throw new IllegalStateException("Cannot review application. It is currently in state: " + app.getOverallStatus() + ". Must be in OFFICER_REVIEW.");
        }

        if ("APPROVE".equalsIgnoreCase(review.getAction())) {
            app.setOverallStatus(ApplicationStatus.APPROVED);
        } else {
            app.setOverallStatus(ApplicationStatus.REJECTED);
        }

        app.setReviewedBy(officerId);
        return applicationRepository.save(app);
    }

    // 5. List Applications By Citizen User
    public List<Application> getApplicationsByCitizen(String citizenId) {
        return applicationRepository.findByCitizenId(citizenId);
    }

    // 6. --- FIX FOR MUSKAN (OFFICER REVIEW QUEUE): List All Applications ---
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}
