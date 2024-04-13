package user;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;

public class UserGenerator {
    public static Faker faker = new Faker();

    @Step("Создание нового пользователя с рандомными данными")
    public User createNewRandomUser() {
        return new User(faker.internet().emailAddress(), faker.name().username(), faker.internet().password());
    }
}