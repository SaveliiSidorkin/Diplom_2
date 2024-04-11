package user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import restclient.BaseApiClient;
import restclient.Url;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseApiClient {
    @Step("Создание юзера")
    public ValidatableResponse userCreate(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(Url.CREATE_USER)
                .then();
    }

    @Step("Авторизация юзера")
    public ValidatableResponse userLogin(UserAuth auth) {
        return given()
                .spec(getSpec())
                .body(auth)
                .when()
                .post(Url.LOGIN_USER)
                .then();
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse updateUserData(String accessToken, User user) {
        return given()
                .spec(getSpec())
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch(Url.USER)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .header("authorization", accessToken)
                .when()
                .delete(Url.USER)
                .then();
    }
}
