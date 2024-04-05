package usertest;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserAuth;
import user.UserClient;
import user.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static user.UserGenerator.faker;

public class UserDataTest {
    public String accessToken;
    private User user;
    private UserClient userClient;

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
    @Description("Обновление пользовательских данных")
    public void UpdateUser() {
        userClient.userCreate(user);
        ValidatableResponse loginResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginResponse.extract().path("accessToken");
        User updateUser = new User();
        updateUser.setName(faker.name().name());
        updateUser.setPassword(faker.internet().password());
        updateUser.setEmail(faker.internet().emailAddress());
        ValidatableResponse updateResponse = userClient.updateUserData(accessToken, updateUser);
        updateResponse
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updateUser.getName()))
                .body("user.email", equalTo(updateUser.getEmail()));
    }

    @Test
    @Description("Обновление пользовательских данных без авторизации")
    public void UpdateWithoutAuth() {
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken");
        User updateUser = new User();
        updateUser.setName(faker.name().name());
        updateUser.setEmail(faker.internet().emailAddress());
        updateUser.setPassword(faker.internet().password());
        ValidatableResponse updateResponse = userClient.updateUserData("", updateUser);
        updateResponse
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Description("Обновление данных со старой почтой")
    public void UpdateUserWithSameEmail() {
        userClient.userCreate(user);
        ValidatableResponse loginResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginResponse.extract().path("accessToken");
        User updateUser = new User();
        updateUser.setName(faker.name().name());
        updateUser.setPassword(faker.internet().password());
        ValidatableResponse updateResponse = userClient.updateUserData(accessToken, updateUser);
        updateResponse
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));


    }
}
