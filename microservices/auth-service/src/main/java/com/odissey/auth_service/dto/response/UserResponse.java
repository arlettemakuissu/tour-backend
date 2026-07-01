package com.odissey.auth_service.dto.response;

import com.odissey.auth_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserResponse {

    private int id;
    private String username;
    private String email;
    private String roles;
    private String displayName;

    public static UserResponse fromEntityToDto(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getDisplayName()
        );
    }
}
