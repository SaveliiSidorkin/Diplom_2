package usertest;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserAuth;
import user.UserClient;
import user.UserGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static user.UserGenerator.faker;

public class LoginUserTest {
    private final String errorMessage = "email or password are incorrect";
    private User user;
    private UserClient userClient;
    private String accessToken;
    private String randomEmail = faker.internet().emailAddress();
    private String randomPassword = faker.internet().password();

    @Before
    public void createTestData() {
        userClient = new UserClient();
        UserGenerator userGenerator = new UserGenerator();
        user = userGenerator.createNewRandomUser();
    }

    @After
    public void deleteTestData() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизация существующим пользователем")
    public void loginUser() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        loginUserResponse.statusCode(SC_OK);
        accessToken = loginUserResponse.extract().path("accessToken");
        loginUserResponse
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация несуществующим пользователем")
    public void loginNotExistUser() {
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(randomEmail, randomPassword));
        loginUserResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo(errorMessage))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с путым паролем")
    public void loginEmptyPassword() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), ""));
        loginUserResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo(errorMessage))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с некорректным паролем")
    public void loginIncorrectPassword() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), randomPassword));
        loginUserResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo(errorMessage))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с пустым email")
    public void loginEmptyEmail() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth("", user.getEmail()));
        loginUserResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo(errorMessage))
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с некорректным email")
    public void loginIncorrectEmail() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(randomEmail, user.getPassword()));
        loginUserResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo(errorMessage))
                .body("success", equalTo(false));
    }
}
