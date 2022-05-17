package com.github.mgh14.users.user;

import com.github.mgh14.users.api.model.PartialUserInfo;
import com.github.mgh14.users.api.model.SignupInput;
import com.github.mgh14.users.api.model.UserNames;
import com.github.mgh14.users.api.model.UserNamesInput;
import com.github.mgh14.users.auth.JwtHelper;
import com.github.mgh14.users.repository.CurrentTokenRepository;
import com.github.mgh14.users.repository.UserCredentialRepository;
import com.github.mgh14.users.repository.UserInfoRepository;
import com.github.mgh14.users.repository.entity.CurrentToken;
import com.github.mgh14.users.repository.entity.UserCredential;
import com.github.mgh14.users.repository.entity.UserInfo;
import io.micrometer.core.instrument.util.StringUtils;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserCredentialRepository userCredsRepo;
  private final UserInfoRepository userInfoRepo;
  private final CurrentTokenRepository currentTokenRepo;

  @Transactional
  public UserInfo signupUser(SignupInput signupInfo) {
    log.debug("Signing up new anonymous user ...");
    var uuid = UUID.randomUUID();
    var temporaryAnonymousId = "anonymousUser-" + uuid;
    log.info("Anonymous ID for signing user: {}", temporaryAnonymousId);

    var userEmail = signupInfo.getEmail();
    if (emailInUse(userEmail)) {
      throw new IllegalArgumentException("Email '" + userEmail + "' is already in use");
    }

    final OffsetDateTime currentOffsetTimestamp = OffsetDateTime.now();
    // TODO: auditable fields
    var userCred = new UserCredential().setEmail(userEmail)
        .setPassword(signupInfo.getPassword())
        .setCreatedAt(currentOffsetTimestamp)
        .setCreatedBy(temporaryAnonymousId)
        .setModifiedAt(currentOffsetTimestamp)
        .setModifiedBy(temporaryAnonymousId);
    var result = userCredsRepo.save(userCred);
    log.debug("Created new user cred (ID = {})", result);

    var userInfo = new UserInfo()
        .setExternalId(uuid)
        .setUserCredential(result)
        .setFirstName(signupInfo.getFirstName())
        .setLastName(signupInfo.getLastName())
        .setCreatedAt(currentOffsetTimestamp)
        .setCreatedBy(temporaryAnonymousId)
        .setModifiedAt(currentOffsetTimestamp)
        .setModifiedBy(temporaryAnonymousId);
    var userInfoResult = userInfoRepo.save(userInfo);
    log.info("Created new user with ID {} (anonymous user {})", userInfoResult.getUserInfoId(),
        temporaryAnonymousId);
    return userInfoResult;
  }

  @Transactional
  public String authenticate(String email, String password) {
    var userCred = userCredsRepo.findOneByEmail(email).orElseThrow(() -> new RuntimeException("Access denied"));
    // TODO: hash password and check against (stored) hashed password
    if (!Objects.equals(password, userCred.getPassword())) {
      throwAccessDenied();
    }
    var jwt = JwtHelper.createJWT(userCred.getUserInfo().getExternalId().toString(),
        System.currentTimeMillis());

    // TODO: use DateTime instead of adding a set amount
    long oneHourMillis = System.currentTimeMillis() + 60 * 60 * 1000;
    currentTokenRepo.save(new CurrentToken().setUserCredential(userCred).setJwt(jwt).setExpiry(oneHourMillis));
    log.debug("Successfully authenticated user {}", userCred.getUserInfo().getExternalId());
    return jwt;
  }

  @Transactional
  public List<PartialUserInfo> getUsers(String jwt) {
    var currentToken = getCurrentUser(jwt).orElse(null);
    if (currentToken == null) {
      throwAccessDenied();
    }

    log.info("Users info requested by user {}", currentToken.getUserCredential().getUserInfo().getExternalId());
    return userCredsRepo.findAll().stream().map(
        userCreds -> new PartialUserInfo(userCreds.getEmail(), userCreds.getUserInfo().getFirstName(),
            userCreds.getUserInfo().getLastName()))
        .collect(Collectors.toList());
  }

  @Transactional
  public UserNames updateUser(UserNamesInput userNamesInput, String jwt) {
    var currentToken = getCurrentUser(jwt).orElse(null);
    if (currentToken == null) {
      throwAccessDenied();
    }

    // validate new given names
    if (userNamesInput == null || StringUtils.isBlank(userNamesInput.getFirstName()) ||
        StringUtils.isBlank(userNamesInput.getLastName())) {
      throw new IllegalArgumentException("Blank or empty names not allowed");
    }

    var userInfo = currentToken.getUserCredential().getUserInfo();
    userInfo.setFirstName(userNamesInput.getFirstName());
    userInfo.setLastName(userNamesInput.getLastName());
    var result = userInfoRepo.save(userInfo);
    log.info("Username successfully updated for user {}", userInfo.getExternalId());
    return new UserNames(result.getFirstName(), result.getLastName());
  }

  private Optional<UserInfo> getCurrentUser(String jwt) {
    // TODO: pull subject from the JWT to check against the user's email address
    var currentToken = currentTokenRepo.findByJwt(jwt)
        .orElseThrow(() -> new RuntimeException("invalid token"));
    return Optional.of(currentToken.getUserCredential().getUserInfo());
  }

  private void throwAccessDenied() {
    throw new RuntimeException("access denied");
  }

  private boolean emailInUse(String email) {
    return userCredsRepo.findOneByEmail(email).isPresent();
  }
}
