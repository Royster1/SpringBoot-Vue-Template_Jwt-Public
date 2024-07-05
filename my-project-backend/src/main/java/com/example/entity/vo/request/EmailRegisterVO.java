package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class EmailRegisterVO {
    @Email
    String email;
    @Length(max = 6, min = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{3,15}$")
    @Length(min = 1, max = 10)
    String username;
    @Length(min = 6, max = 20)
    String password;
}
