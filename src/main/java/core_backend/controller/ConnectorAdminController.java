package core_backend.controller;

import core_backend.dto.ConnectorActivateRequest;
import core_backend.entity.ConnectorConfig;
import core_backend.repository.ConnectorConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/connectors")
@CrossOrigin(origins = "*")
public class ConnectorAdminController {

    private final ConnectorConfigRepository repository;

    public ConnectorAdminController(ConnectorConfigRepository repository) {
        this.repository = repository;
    }

    // Step G: Member 2 UI / Member 6 Harness sends validated mapping here
    @PostMapping("/activate")
    public ResponseEntity<ConnectorConfig> activateConnector(@RequestBody ConnectorActivateRequest request) {
        ConnectorConfig config = ConnectorConfig.builder()
                .departmentId(request.getDepartmentId())
                .departmentName(request.getDepartmentName())
                .protocol(request.getProtocol() != null ? request.getProtocol() : "REST")
                .endpoint(request.getEndpoint())
                .mappingJson(request.getMappingJson())
                .active(true)
                .activatedAt(LocalDateTime.now())
                .build();

        ConnectorConfig saved = repository.save(config);
        return ResponseEntity.ok(saved);
    }

    // Member 4 (Gateway) queries this at runtime to transform legacy payloads
    @GetMapping("/{departmentId}/config")
    public ResponseEntity<ConnectorConfig> getConnectorConfig(@PathVariable String departmentId) {
        return repository.findById(departmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // List all active connectors
    @GetMapping("/active")
    public ResponseEntity<List<ConnectorConfig>> getActiveConnectors() {
        return ResponseEntity.ok(repository.findByActiveTrue());
    }
}