package core_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GatewayClientService {

    private final RestTemplate restTemplate;

    @Value("${gateway.base-url:https://interoperability-hub-sih.onrender.com}")
    private String baseUrl;

    @Value("${gateway.department-id:SCHOLARSHIP}")
    private String departmentId;

    @Value("${gateway.api-key:scholarship-secret-key-123}")
    private String apiKey;

    @Value("${gateway.secret:scholarship-secret-key-123}")
    private String secretKey;

    public GatewayClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Map> fetchConsolidatedData(
            String citizenId,
            Long applicationId,
            String providerDepartment,
            String purpose,
            String officerJurisdiction,
            List<String> requestedFields
    ) {
        try {
            // 1. Generate unique Request-ID and Unix timestamp in seconds
            String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
            long timestamp = Instant.now().getEpochSecond();

            // 2. Build message exactly: departmentId|requestId|timestamp
            String message = departmentId + "|" + requestId + "|" + timestamp;

            // 3. Compute HMAC-SHA256 Hex Signature
            String signature = computeHmacSha256(message, secretKey);

            // 4. Set exact required headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Department-Id", departmentId);
            headers.set("X-Api-Key", apiKey);
            headers.set("X-Request-Id", requestId);
            headers.set("X-Timestamp", String.valueOf(timestamp));
            headers.set("X-Signature", signature);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // 5. Build URL with query params
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/gateway/citizen/" + citizenId + "/consolidated")
                    .queryParam("applicationId", applicationId)
                    .queryParam("purpose", purpose)
                    .queryParam("providerDepartment", providerDepartment)
                    .queryParam("officerJurisdiction", officerJurisdiction);

            for (String field : requestedFields) {
                uriBuilder.queryParam("requestedFields", field);
            }

            String targetUrl = uriBuilder.toUriString();
            System.out.println("Calling Gateway URL: " + targetUrl);
            System.out.println("Timestamp: " + timestamp + " | Signature: " + signature);

            // 6. Call Gateway
            return restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, Map.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Gateway returned error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "status", e.getStatusCode().value(),
                    "gatewayError", e.getResponseBodyAsString()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage()
            ));
        }
    }

    private String computeHmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }
}