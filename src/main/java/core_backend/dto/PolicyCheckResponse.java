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
public class PolicyCheckResponse {
    private boolean allowed;
    private List<String> allowedFields;
    private boolean riskFlag;
    private String statusMessage;

    // Fail-safe Getters & Setters
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public List<String> getAllowedFields() { return allowedFields; }
    public void setAllowedFields(List<String> allowedFields) { this.allowedFields = allowedFields; }

    public boolean isRiskFlag() { return riskFlag; }
    public void setRiskFlag(boolean riskFlag) { this.riskFlag = riskFlag; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    // Explicit static builder fallback if Lombok builder generation is skipped by compiler
    public static PolicyCheckResponseBuilder builder() {
        return new PolicyCheckResponseBuilder();
    }

    public static class PolicyCheckResponseBuilder {
        private boolean allowed;
        private List<String> allowedFields;
        private boolean riskFlag;
        private String statusMessage;

        public PolicyCheckResponseBuilder allowed(boolean allowed) {
            this.allowed = allowed;
            return this;
        }

        public PolicyCheckResponseBuilder allowedFields(List<String> allowedFields) {
            this.allowedFields = allowedFields;
            return this;
        }

        public PolicyCheckResponseBuilder riskFlag(boolean riskFlag) {
            this.riskFlag = riskFlag;
            return this;
        }

        public PolicyCheckResponseBuilder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public PolicyCheckResponse build() {
            return new PolicyCheckResponse(allowed, allowedFields, riskFlag, statusMessage);
        }
    }
}