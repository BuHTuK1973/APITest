package api;

import io.qameta.allure.Step;
import pojo.CourierCredentials;
import pojo.CourierPOJO;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CourierAPITest {
    private CourierPOJO courier;
    private CourierAPI courierAPI;
    private int createdCourierId;
    private boolean created;
    private Response response;
    private Response loginResponse;
    private Response deleteResponse;

    @Before
    public void setup() {
        courierAPI = new CourierAPI();
    }

    @After
    public void teardown() {
        if (created) {
            CourierCredentials credentials = CourierCredentials.from(courier);
            loginResponse = courierAPI.sendPostLoginCourier(credentials);
            createdCourierId = courierLoginSuccessId(loginResponse);
            deleteResponse = courierAPI.sendDeleteCourier(createdCourierId);
            deleteCourierSuccess(deleteResponse);
        }
    }

    @Test
    @DisplayName("Проверка создания курьера") // имя теста
    public void createCourierSuccess() {
        courier = CourierPOJO.getRandom();
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        Assert.assertTrue("Курьер не был создан", created);
    }

    @Test
    @DisplayName("Проверка ошибки при создании курьера без данных") // имя теста
    public void createCourierWithoutCredBadRequest400() {
        String expected = "Недостаточно данных для создания учетной записи";
        courier = new CourierPOJO();
        response = courierAPI.sendPostRequestCreateCourier(courier);
        String responseMessage = courierNotCreatedStatus400(response);
        Assert.assertEquals("Ожидается сообщение об ошибке", expected, responseMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при создании курьера с уже существующим логином") // имя теста
    public void createCourierCantBeTheSameLogin409() {
        String expected = "Этот логин уже используется. Попробуйте другой.";
        courier = new CourierPOJO("1061n", "1061n", null);
        //создание курьера
        response = courierAPI.sendPostRequestCreateCourier(courier);
        //проверка на то, что курьер создан
        created = courierCreatedStatus201(response);
        //создание курьера
        response = courierAPI.sendPostRequestCreateCourier(courier);
        //проврка сообщения о конфликет
        String responseMessage = courierConflict409(response);
        Assert.assertEquals("Ожидается сообщение о том, что УЗ с таким логином уже есть", expected, responseMessage);
    }

    @Step("Получить статус об успешном создании курьера - 201")
    public boolean courierCreatedStatus201(Response response) {
        return response.then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("ok");
    }

    @Step("Получить ошибку о том, что такой логин уже существует на сервере - 409")
    public String courierConflict409(Response response) {
        return response.then()
                .assertThat()
                .statusCode(409)
                .extract()
                .path("message");
    }

    @Step("Получить ошибку о том, что курьер не был создан - 400")
    public String courierNotCreatedStatus400(Response response) {
        return response.then()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
    }

    @Step("Получить ID курьера после авторизации")
    public int courierLoginSuccessId(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Step("Получить статус об успешном удалении курьера")
    public void deleteCourierSuccess(Response response) {
        response.then().log().all()
                .assertThat()
                .statusCode(200);
    }
}


