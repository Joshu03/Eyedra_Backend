package com.eyedra.backend.model;

import com.eyedra.backend.util.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotNull(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number")
    private String mobile;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    private Role role = Role.DEFAULT; // Default value
}