package com.guardian.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ProfileRequest {

    private String realName;
    private String phone;

    @Email(message = "格式不正确")
    private String email;
}
