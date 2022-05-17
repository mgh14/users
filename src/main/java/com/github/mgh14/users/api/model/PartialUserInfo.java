package com.github.mgh14.users.api.model;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

@Value
public class PartialUserInfo {
  String email;
  String firstName;
  String lastName;
}
