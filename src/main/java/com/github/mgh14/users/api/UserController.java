package com.github.mgh14.users.api;

import com.github.mgh14.users.api.model.PartialUserInfo;
import com.github.mgh14.users.api.model.UserNames;
import com.github.mgh14.users.api.model.UserNamesInput;
import com.github.mgh14.users.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Add OpenAPI 3 Spec Info
// TODO: leverage Spring Security instead of rolling my own jwt implementation

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/users", consumes = "application/json")
  public ResponseEntity<List<PartialUserInfo>> getUsers(@RequestHeader(name = "x-authentication-token") String jwt) {
    var users = userService.getUsers(jwt);
    return ResponseEntity.ok().body(users);
  }

  @PutMapping(value = "/users", consumes = "application/json")
  public ResponseEntity<UserNames> updateUser(@RequestBody UserNamesInput userNamesInput, @RequestHeader(name = "x-authentication-token") String jwt) {
    var updatedUserNames = userService.updateUser(userNamesInput, jwt);
    return ResponseEntity.ok().body(updatedUserNames);
  }
}
