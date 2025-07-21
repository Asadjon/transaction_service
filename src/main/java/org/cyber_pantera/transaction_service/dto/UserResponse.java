package org.cyber_pantera.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
