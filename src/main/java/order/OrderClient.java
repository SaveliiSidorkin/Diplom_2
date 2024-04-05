package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import restclient.BaseApiClient;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static restclient.Url.*;

public class OrderClient extends BaseApiClient {
    @Step("Получение списка доступных ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(INGREDIENTS)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(getSpec())
                .header("authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS)
                .then();
    }

    @Step("Получение id доступных ингридиентов")
    public ArrayList<String> getIngredientsIds() {
        return getIngredients().extract().path("data._id");
    }

    @Step("Получение рандомных id ингредиентов")
    public ArrayList<String> getRandomIdsForOrder(int count) {
        ArrayList<String> randomIds = new ArrayList<>();
        ArrayList<String> availableIngredientId = getIngredientsIds();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(availableIngredientId.size());
            randomIds.add(availableIngredientId.get(randomIndex));
        }
        return randomIds;
    }

    @Step("Получение фэйковых id ингредиентов")
    public ArrayList<String> getFakeIdsForOrder(int count) {
        ArrayList<String> orderIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orderIds.add(RandomStringUtils.random(24, true, true));
        }
        return orderIds;
    }

    @Step("Получение списка заказов пользователя")
    public ValidatableResponse getUserOrders(String accessToken) {
        return given()
                .spec(getSpec())
                .header("authorization", accessToken)
                .when()
                .get(ORDERS)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getSpec())
                .when()
                .get(ALL_ORDER)
                .then();
    }
}

