package core_backend.repository;

import core_backend.entity.ConnectorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectorConfigRepository extends JpaRepository<ConnectorConfig, String> {
    List<ConnectorConfig> findByActiveTrue();
}