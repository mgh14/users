package com.github.mgh14.users;

import com.github.mgh14.users.api.LoginController;
import com.github.mgh14.users.api.model.LoginInput;
import com.github.mgh14.users.api.model.PartialUserInfo;
import com.github.mgh14.users.api.model.SignupInput;
import com.github.mgh14.users.api.model.Token;
import com.github.mgh14.users.api.UserController;
import com.github.mgh14.users.api.model.UserNames;
import com.github.mgh14.users.api.model.UserNamesInput;
import com.github.mgh14.users.repository.UserCredentialRepository;
import com.github.mgh14.users.repository.UserInfoRepository;
import com.github.mgh14.users.repository.entity.UserCredential;
import com.github.mgh14.users.repository.entity.UserInfo;
import com.github.mgh14.users.user.UserService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@Sql(scripts = { "/test-cleanup.sql" })
class UsersComponentTest {

	// Classes under test:
	@Autowired
	@SuppressWarnings("unused")
	private LoginController loginController;
	@Autowired
	@SuppressWarnings("unused")
	private UserController userController;

	// For verification:
	@Autowired
	@SuppressWarnings("unused")
	private UserService userService;
	@Autowired
	@SuppressWarnings("unused")
	private UserCredentialRepository userCredsRepo;
	@Autowired
	@SuppressWarnings("unused")
	private UserInfoRepository userInfoRepo;

	@Test
	void loginController_signsUpUser() {
		var result = loginController.signup(new SignupInput("test-email@very-bogus-domain-name-here.com", "user-pass", "Harry", "Potter"));
		Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(result.getBody())
				.isNotNull()
				.hasOnlyFields("token")
				.extracting(Token::getToken).asString().hasSizeGreaterThan(25);
	}

	@Test
	void loginController_userAuthenticates() {
		// pre-work: add a user
		var currentDateTime = OffsetDateTime.now();
		var componentTestString = "ComponentTest";
		var loginEmail = "test-email2@very-bogus-domain-name-here.com";
		var loginPassword = "password";
		var preResult = userCredsRepo.save(new UserCredential().setEmail(loginEmail)
				.setPassword(loginPassword)
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));
		userInfoRepo.save(new UserInfo().setUserCredential(preResult)
				.setExternalId(UUID.randomUUID())
				.setFirstName("Ronald")
				.setLastName("Weasley")
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));

		// test the method:
		var result = loginController.login(new LoginInput(loginEmail, loginPassword));
		Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(result.getBody()).isNotNull().hasOnlyFields("token")
				.extracting(Token::getToken).asString().hasSizeGreaterThan(25);
	}

	@Test
	void loginController_invalidTokenRejected() {
		// todo: write
	}

	@Test
	void userController_getsUsers() {
		// pre-work: create users to get
		createUsers();

		// pre-work: login user
		var jwt = userService.authenticate("test-email2@very-bogus-domain-name-here.com", "password");

		// test the method:
		var result = userController.getUsers(jwt);
		Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(result.getBody())
				.containsExactlyInAnyOrder(
						new PartialUserInfo("test-email2@very-bogus-domain-name-here.com", "Ronald", "Weasley"),
						new PartialUserInfo("test-email3@very-bogus-domain-name-here.com", "Hermione", "Granger"));
	}

	@Test
	void userController_updatesUserNames() {
		// pre-work: create users so one can be updated
		createUsers();

		// pre-work: login user
		var jwt = userService.authenticate("test-email2@very-bogus-domain-name-here.com", "password");

		// test the method:
		var result = userController.updateUser(new UserNamesInput("Luna", "Lovegood"), jwt);
		Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(result.getBody())
				.isNotNull()
				.isEqualTo(new UserNames("Luna", "Lovegood"));
	}

	private void createUsers() {
		var currentDateTime = OffsetDateTime.now();
		var componentTestString = "ComponentTest";
		var userEmail1 = "test-email2@very-bogus-domain-name-here.com";
		var userPassword1 = "password";
		var userResult1 = userCredsRepo.save(new UserCredential().setEmail(userEmail1)
				.setPassword(userPassword1)
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));
		userInfoRepo.save(new UserInfo().setUserCredential(userResult1)
				.setExternalId(UUID.randomUUID())
				.setFirstName("Ronald")
				.setLastName("Weasley")
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));

		var userEmail2 = "test-email3@very-bogus-domain-name-here.com";
		var userPassword2 = "password";
		var userResult2 = userCredsRepo.save(new UserCredential().setEmail(userEmail2)
				.setPassword(userPassword2)
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));
		userInfoRepo.save(new UserInfo().setUserCredential(userResult2)
				.setExternalId(UUID.randomUUID())
				.setFirstName("Hermione")
				.setLastName("Granger")
				.setCreatedAt(currentDateTime)
				.setCreatedBy(componentTestString)
				.setModifiedAt(currentDateTime)
				.setModifiedBy(componentTestString));
	}
}
