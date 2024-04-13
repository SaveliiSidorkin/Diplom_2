package ordertest;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.Order;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserAuth;
import user.UserClient;
import user.UserGenerator;

import java.util.ArrayList;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    Random random = new Random();
    OrderClient orderClient = new OrderClient();
    private Order order;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private int count;


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
    @DisplayName("Созданиек заказа с авторизацией и ингредиентами")
    public void createOrderTest() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginUserResponse.extract().path("accessToken");
        orderClient.getIngredientsIds();
        count = random.nextInt(orderClient.getIngredientsIds().size());
        ArrayList<String> idForOrder = orderClient.getRandomIdsForOrder(count);
        order = new Order(idForOrder);
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, accessToken);
        createOrderResponse
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Созданиек заказа с авторизацией без ингредиентов")
    public void createOrderWithoutIngredients() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginUserResponse.extract().path("accessToken");
        orderClient.getIngredientsIds();
        ArrayList<String> idForOrder = new ArrayList<>();
        order = new Order(idForOrder);
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, accessToken);
        createOrderResponse
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Созданиек заказа без авторизации с ингредиентами")
    public void createOrderWithoutAuthWithIngredients() {
        orderClient.getIngredientsIds();
        count = random.nextInt(orderClient.getIngredientsIds().size());
        ArrayList<String> idForOrder = orderClient.getRandomIdsForOrder(count);
        order = new Order(idForOrder);
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, "");
        createOrderResponse
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Созданиек заказа без авторизации без ингредиентов")
    public void createOrderWithoutAuthAndIngredients() {
        orderClient.getIngredientsIds();
        ArrayList<String> idForOrder = new ArrayList<>();
        order = new Order(idForOrder);
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, "");
        createOrderResponse
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Созданиек заказа с фейковым id")
    public void createOrderWithFakeIngredients() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginUserResponse.extract().path("accessToken");
        orderClient.getIngredientsIds();
        count = random.nextInt(orderClient.getIngredientsIds().size());
        ArrayList<String> idForOrder = orderClient.getFakeIdsForOrder(count);
        order = new Order(idForOrder);
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, accessToken);
        createOrderResponse.statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}

