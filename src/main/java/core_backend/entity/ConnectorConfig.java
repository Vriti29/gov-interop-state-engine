package core_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "connector_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorConfig {
    @Id
    private String departmentId; // e.g., "REV01", "EDU01"

    @Column(nullable = false)
    private String departmentName;

    private String protocol; // "REST", "XML", "CSV"
    private String endpoint;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mappingJson; // Member 6 se validated JSON string

    private boolean active;
    private LocalDateTime activatedAt;
}