package com.marek.onlinebookstore.dto;

import com.marek.onlinebookstore.annotation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword",
        message = "The password fields must match")
public record UserRegistrationRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Length(min = 6, max = 20) String password,
        @NotBlank @Length(min = 6, max = 20) String repeatPassword,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String shippingAddress
) {}

