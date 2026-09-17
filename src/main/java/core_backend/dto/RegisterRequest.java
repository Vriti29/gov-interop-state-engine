package core_backend.dto;

import core_backend.enums.AppEnums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
    private String department;
    private String jurisdiction;
}