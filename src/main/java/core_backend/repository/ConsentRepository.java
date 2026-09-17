package core_backend.repository;

import core_backend.entity.Consent;
import core_backend.enums.AppEnums.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    
    // YEH LINE ADD KARO:
    List<Consent> findByCitizenId(String citizenId);

    // Aur yeh check karo ki yeh method pehle se hai ya nahi:
    Optional<Consent> findByCitizenIdAndConsumerDepartmentAndProviderDepartmentAndStatus(
            String citizenId, String consumerDepartment, String providerDepartment, ConsentStatus status);
}