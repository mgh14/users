package com.github.mgh14.users.api.model;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
// TODO: validate
public class LoginInput {
  String email;
  String password;
}
