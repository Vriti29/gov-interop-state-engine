package core_backend.entity;

import core_backend.enums.AppEnums.ConsentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String citizenId;

    @Column(nullable = false)
    private String consumerDepartment;

    @Column(nullable = false)
    private String providerDepartment;

    @Column(nullable = false)
    private String purpose;

    @ElementCollection
    private List<String> allowedFields;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;
}