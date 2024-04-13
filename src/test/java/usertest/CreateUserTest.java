package usertest;


import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserGenerator;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;
    private String errorMessage = "Email, password and name are required fields";

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
    @DisplayName("Регистрация валидного пользователя")
    public void createUserTest() {
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken");
        response
                .statusCode(SC_OK)
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    public void createUserWithoutEmail() {
        user.setEmail("");
        ValidatableResponse response = userClient.userCreate(user);
        response
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo(errorMessage));
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    public void createUserWithoutName() {
        user.setName("");
        ValidatableResponse response = userClient.userCreate(user);
        response
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo(errorMessage));
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    public void createUserWithoutPassword() {
        user.setPassword("");
        ValidatableResponse response = userClient.userCreate(user);
        response
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo(errorMessage));
    }

    @Test
    @DisplayName("Регистрация существующего пользователя")
    public void createExistUser() {
        ValidatableResponse response = userClient.userCreate(user);
        response.statusCode(SC_OK);
        ValidatableResponse responseExistUser = userClient.userCreate(user);
        responseExistUser
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

}
