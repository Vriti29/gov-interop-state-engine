package core_backend.dto;

import core_backend.enums.AppEnums.ApplicationStatus;
import core_backend.enums.AppEnums.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusResponse {
    private Long applicationId;
    private String citizenId;
    private String serviceType;
    private ApplicationStatus overallStatus;
    
    // Per-department step verification breakdown
    private StepStatus identityStep;
    private StepStatus revenueStep;
    private StepStatus educationStep;

    private LocalDateTime submittedAt;
    private LocalDateTime deadlineAt;
    private boolean slaBreached;
    private String reviewedBy;
}