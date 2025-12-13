package com.odissay.tour.model.dto.reponse;

import com.odissay.tour.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserDetailResponse extends UserResponse {

    private String username;
    private String role;
    private boolean verified;
    private boolean enable;

    public UserDetailResponse(int id, String email, String firstname, String lastname, String username, String role, boolean verified, boolean enable) {
        super(id, email, firstname, lastname);
        this.username = username;
        this.role = role;
        this.verified = verified;
        this.enable = enable;
    }
    public static  UserDetailResponse fromEntityToDto(User user){
        return new UserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getRole().name(),
                user.isVerified(),
                user.isEnabled()

        );

    }
}
