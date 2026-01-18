package com.odissey.auth_service.dto.response;

import com.odissey.auth_service.entity.User;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserStatusResponse extends UserResponse{

    private boolean enabled;

    public UserStatusResponse(int id, String username, String email, String roles,String displayName, boolean enabled) {
        super(id, username, email, roles,displayName);
        this.enabled = enabled;
    }

    public static UserStatusResponse fromEntityToDto(User user){
        return new UserStatusResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getDisplayName(),
                user.isEnabled()
        );
    }
}
