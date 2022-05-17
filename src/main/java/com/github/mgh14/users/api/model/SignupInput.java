package com.github.mgh14.users.api.model;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
// TODO: validate
public class SignupInput {
  String email;
  String password;
  String firstName;
  String lastName;
}
