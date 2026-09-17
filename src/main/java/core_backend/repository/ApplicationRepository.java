package core_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import core_backend.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCitizenId(String citizenId);
}