package core_backend.dto;

import lombok.Data;

@Data
public class ConnectorActivateRequest {
    private String departmentId;
    private String departmentName;
    private String protocol;
    private String endpoint;
    private String mappingJson; // The validated mapping contract
}