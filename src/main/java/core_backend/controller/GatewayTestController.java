package core_backend.controller;

import core_backend.service.GatewayClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gateway-integration")
public class GatewayTestController {

    private final GatewayClientService gatewayClientService;

    public GatewayTestController(GatewayClientService gatewayClientService) {
        this.gatewayClientService = gatewayClientService;
    }

    // 1. REVENUE (Income)
    @PostMapping("/test-fetch-revenue")
    public ResponseEntity<Map> testFetchRevenue(
            @RequestParam(defaultValue = "citizen_vriti") String citizenId,
            @RequestParam(defaultValue = "1") Long applicationId
    ) {
        return gatewayClientService.fetchConsolidatedData(
                citizenId,
                applicationId,
                "REVENUE",
                "INCOME_VERIFICATION",
                "PUNE",
                List.of("citizenId", "annualIncome")
        );
    }

    // 2. IDENTITY (Aadhaar / Citizen Profile)
    @PostMapping("/test-fetch-identity")
    public ResponseEntity<Map> testFetchIdentity(
            @RequestParam(defaultValue = "citizen_vriti") String citizenId,
            @RequestParam(defaultValue = "1") Long applicationId
    ) {
        return gatewayClientService.fetchConsolidatedData(
                citizenId,
                applicationId,
                "IDENTITY",
                "IDENTITY_VERIFICATION",
                "PUNE",
                List.of("citizenId", "fullName", "dob", "gender")
        );
    }

    // 3. EDUCATION (Degree / Academic Details)
    @PostMapping("/test-fetch-education")
    public ResponseEntity<Map> testFetchEducation(
            @RequestParam(defaultValue = "citizen_vriti") String citizenId,
            @RequestParam(defaultValue = "1") Long applicationId
    ) {
        return gatewayClientService.fetchConsolidatedData(
                citizenId,
                applicationId,
                "EDUCATION",
                "ACADEMIC_VERIFICATION",
                "PUNE",
                List.of("citizenId", "degree", "institution", "gpa")
        );
    }
}