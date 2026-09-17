package core_backend.entity;

import core_backend.enums.AppEnums.ApplicationStatus;
import core_backend.enums.AppEnums.StepStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String citizenId;

    @Column(nullable = false)
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus overallStatus;

    @Enumerated(EnumType.STRING)
    private StepStatus identityStep;

    @Enumerated(EnumType.STRING)
    private StepStatus revenueStep;

    @Enumerated(EnumType.STRING)
    private StepStatus educationStep;

    private LocalDateTime submittedAt;
    private LocalDateTime deadlineAt;
    private LocalDateTime identityStartedAt;
    private LocalDateTime revenueStartedAt;
    private LocalDateTime educationStartedAt;
        
    private String reviewedBy;

    // Fail-safe Getters & Setters for Neha's build
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public ApplicationStatus getOverallStatus() { return overallStatus; }
    public void setOverallStatus(ApplicationStatus overallStatus) { this.overallStatus = overallStatus; }

    public StepStatus getIdentityStep() { return identityStep; }
    public void setIdentityStep(StepStatus identityStep) { this.identityStep = identityStep; }

    public StepStatus getRevenueStep() { return revenueStep; }
    public void setRevenueStep(StepStatus revenueStep) { this.revenueStep = revenueStep; }

    public StepStatus getEducationStep() { return educationStep; }
    public void setEducationStep(StepStatus educationStep) { this.educationStep = educationStep; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(LocalDateTime deadlineAt) { this.deadlineAt = deadlineAt; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
}