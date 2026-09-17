package core_backend.dto;

import core_backend.enums.AppEnums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private Role role;
    private String jurisdiction;
}