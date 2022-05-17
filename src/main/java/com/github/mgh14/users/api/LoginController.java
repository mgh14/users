package com.github.mgh14.users.api;

import com.github.mgh14.users.api.model.LoginInput;
import com.github.mgh14.users.api.model.SignupInput;
import com.github.mgh14.users.api.model.Token;
import com.github.mgh14.users.repository.entity.UserInfo;
import com.github.mgh14.users.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Add OpenAPI 3 Spec Info
// TODO: add parameter checking to endpoints

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class LoginController {

  private final UserService userService;

  @PostMapping(value = "/signup", consumes = "application/json")
  public ResponseEntity<Token> signup(@RequestBody SignupInput signupInfo) {
    var newUser = userService.signupUser(signupInfo);

    var token = generateTokenForUser(newUser);
    // TODO: add endpoint that would go in this URI
    return ResponseEntity.created(null).body(token);
  }

  @PostMapping(value = "/login", consumes = "application/json")
  public ResponseEntity<Token> login(@RequestBody LoginInput loginInput) {
    var token = new Token(userService.authenticate(loginInput.getEmail(),
        loginInput.getPassword()));
    return ResponseEntity.ok().body(token);
  }

  private Token generateTokenForUser(UserInfo result) {
    var token = userService.authenticate(result.getUserCredential().getEmail(),
        result.getUserCredential().getPassword());
    return new Token(token);
  }
}
