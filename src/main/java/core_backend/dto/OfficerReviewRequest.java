package core_backend.dto;

import lombok.Data;

@Data
public class OfficerReviewRequest {
    private String action; // "APPROVE" or "REJECT"
    private String remarks;
}