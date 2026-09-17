package core_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCheckRequest {
    private Long applicationId;
    private String citizenId;
    private String requestingDepartment;
    private String providerDepartment;
    private String purpose;
    private List<String> requestedFields;
    private String officerJurisdiction;

    // Fail-safe Getters & Setters for environments where Lombok processing fails
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getRequestingDepartment() { return requestingDepartment; }
    public void setRequestingDepartment(String requestingDepartment) { this.requestingDepartment = requestingDepartment; }

    public String getProviderDepartment() { return providerDepartment; }
    public void setProviderDepartment(String providerDepartment) { this.providerDepartment = providerDepartment; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public List<String> getRequestedFields() { return requestedFields; }
    public void setRequestedFields(List<String> requestedFields) { this.requestedFields = requestedFields; }

    public String getOfficerJurisdiction() { return officerJurisdiction; }
    public void setOfficerJurisdiction(String officerJurisdiction) { this.officerJurisdiction = officerJurisdiction; }
}