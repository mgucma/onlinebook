package com.marek.onlinebookstore.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserLoginRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 6, max = 20)
    private String password;
}
