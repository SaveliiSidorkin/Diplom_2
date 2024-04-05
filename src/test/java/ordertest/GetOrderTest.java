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

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {
    OrderClient orderClient = new OrderClient();
    Random random = new Random();
    private UserClient userClient;
    private User user;
    private String accessToken;
    private int count;
    private Order order;

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
    @DisplayName("Получение заказов пользователя")
    public void getOrdersWithAuth() {
        userClient.userCreate(user);
        ValidatableResponse loginUserResponse = userClient.userLogin(new UserAuth(user.getEmail(), user.getPassword()));
        accessToken = loginUserResponse.extract().path("accessToken");
        orderClient.getIngredientsIds();
        count = random.nextInt(orderClient.getIngredientsIds().size());
        ArrayList<String> idForOrder = orderClient.getRandomIdsForOrder(count);
        order = new Order(idForOrder);
        orderClient.createOrder(order, accessToken);
        ValidatableResponse ordersResponse = orderClient.getUserOrders(accessToken);
        ordersResponse
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    public void getAllOrdersWithoutAuth() {
        ValidatableResponse allOrderResponse = orderClient.getAllOrders();
        allOrderResponse
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }
}
