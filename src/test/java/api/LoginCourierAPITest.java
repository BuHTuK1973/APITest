package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.CourierCredentials;
import pojo.CourierPOJO;

public class LoginCourierAPITest {

    private CourierPOJO courier;
    private CourierAPI courierAPI;
    private int createdCourierId;
    private boolean created;
    private Response response;
    private Response loginResponse;
    private Response deleteResponse;
    private CourierCredentials credentials;

    @Before
    public void setup() {
        courierAPI = new CourierAPI();
        courier = CourierPOJO.getRandom();
    }
    @After
    public void teardown() {
        if (created) {
            deleteResponse = courierAPI.sendDeleteCourier(createdCourierId);
            deleteCourierSuccess(deleteResponse);
        }
    }

    @Test
    @DisplayName("Проверка авторизации") // имя теста
    public void loginCourier() {
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        Assert.assertNotEquals("Авторизация не прошла, ожидается ответ с id больше 0", 0, createdCourierId);
    }

    @Test
    @DisplayName("Проверка ошибки при авторизации без логина") // имя теста
    public void loginCourierWithoutLoginBadRequest400() {
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        String expected = "Недостаточно данных для входа";
        CourierCredentials credentialsWithoutLogin = CourierCredentials.withoutLogin(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentialsWithoutLogin);
        String responseMessage = courierLoginNotEnoughDataStatus400(loginResponse);
        Assert.assertEquals("Ожидается сообщение о нехватке данных для входа", expected, responseMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при авторизации без пароля")
    public void loginCourierWithoutPasswordBadRequest400(){
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        String expected = "Недостаточно данных для входа";
        CourierCredentials credentialsWithoutPassword = CourierCredentials.withoutPassword(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentialsWithoutPassword);
        String responseMessage = courierLoginNotEnoughDataStatus400(loginResponse);
        Assert.assertEquals("Ожидается сообщение о нехватке данных для входа", expected, responseMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при авторизации не существущего курьера") // имя теста
    public void loginCourierDoesNotExistBadRequest404() {
        String expected = "Учетная запись не найдена";
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        String responseMessage = courierLoginCourierDoesNotExistStatus400(loginResponse);
        Assert.assertEquals("Ожидается сообщение о том, что не найдена учетная запись", expected, responseMessage);
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

    @Step("Получить ошибку о том, что не хватает данных для авторизации - 400")
    public String courierLoginNotEnoughDataStatus400(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
    }

    @Step("Получить ошибку о том, что такого курьера не существует - 404")
    public String courierLoginCourierDoesNotExistStatus400(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(404)
                .extract()
                .path("message");
    }

    @Step("Получить статус об успешном создании курьера - 201")
    public boolean courierCreatedStatus201(Response response) {
        return response.then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("ok");
    }

    @Step("Удаление курьера")
    public void deleteCourierSuccess(Response response) {
        response.then().log().all()
                .assertThat()
                .statusCode(200);
    }
}
