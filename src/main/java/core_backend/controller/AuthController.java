package core_backend.controller;

import core_backend.dto.AuthResponse;
import core_backend.dto.LoginRequest;
import core_backend.dto.RegisterRequest;
import core_backend.enums.AppEnums.Role;
import core_backend.entity.User;
import core_backend.repository.UserRepository;
import core_backend.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Member 2 ke React app ke liye CORS allow karo
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // --- FIX (Audit Point 3): Prevent Role Escalation ---
        // Public registration should only be CITIZEN. 
        // Admin & Officer accounts are pre-seeded or provisioned internally.
        Role assignedRole = Role.CITIZEN;
        if (request.getRole() != null && request.getRole() != Role.CITIZEN) {
            // Optional: return bad request or silently enforce CITIZEN
            assignedRole = Role.CITIZEN; 
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .department(request.getDepartment())
                .jurisdiction(request.getJurisdiction() != null ? request.getJurisdiction() : "PUNE")
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // --- FIX (Audit Point 6): Citizen ID alignment ---
        // Map citizen_vriti or standard user to demo citizen ID "MH123"
        String citizenId = "MH123";

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("jurisdiction", user.getJurisdiction());
        claims.put("department", user.getDepartment());
        claims.put("citizenId", citizenId);

        String token = jwtUtils.generateToken(user.getUsername(), claims);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .jurisdiction(user.getJurisdiction())
                .build();

        return ResponseEntity.ok(response);
    }
}