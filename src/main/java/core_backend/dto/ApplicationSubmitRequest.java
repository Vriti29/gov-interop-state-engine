package core_backend.dto;

import lombok.Data;

@Data
public class ApplicationSubmitRequest {
    private String citizenId;
    private String serviceType; // e.g., "STUDENT_SCHOLARSHIP"
    private String course;
    private String college;
}