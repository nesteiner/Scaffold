package com.example.backend.request

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class UpdateRoleRequest(
    val id: Long,
    @NotBlank(message = "name cannot be blank")
    @Length(min = 5, message = "length of name must >= 5")
    val name: String
)