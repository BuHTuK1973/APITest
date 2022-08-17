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
    @DisplayName("�������� �����������") // ��� �����
    public void loginCourier() {
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        Assert.assertNotEquals("����������� �� ������, ��������� ����� � id ������ 0", 0, createdCourierId);
    }

    @Test
    @DisplayName("�������� ������ ��� ����������� ��� ������") // ��� �����
    public void loginCourierWithoutLoginBadRequest400() {
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        String expected = "������������ ������ ��� �����";
        CourierCredentials credentialsWithoutLogin = CourierCredentials.withoutLogin(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentialsWithoutLogin);
        String responseMessage = courierLoginNotEnoughDataStatus400(loginResponse);
        Assert.assertEquals("��������� ��������� � �������� ������ ��� �����", expected, responseMessage);
    }

    @Test
    @DisplayName("�������� ������ ��� ����������� ��� ������")
    public void loginCourierWithoutPasswordBadRequest400(){
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        createdCourierId = courierLoginSuccessId(loginResponse);
        String expected = "������������ ������ ��� �����";
        CourierCredentials credentialsWithoutPassword = CourierCredentials.withoutPassword(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentialsWithoutPassword);
        String responseMessage = courierLoginNotEnoughDataStatus400(loginResponse);
        Assert.assertEquals("��������� ��������� � �������� ������ ��� �����", expected, responseMessage);
    }

    @Test
    @DisplayName("�������� ������ ��� ����������� �� ������������ �������") // ��� �����
    public void loginCourierDoesNotExistBadRequest404() {
        String expected = "������� ������ �� �������";
        credentials = CourierCredentials.from(courier);
        loginResponse = courierAPI.sendPostLoginCourier(credentials);
        String responseMessage = courierLoginCourierDoesNotExistStatus400(loginResponse);
        Assert.assertEquals("��������� ��������� � ���, ��� �� ������� ������� ������", expected, responseMessage);
    }

    @Step("�������� ID ������� ����� �����������")
    public int courierLoginSuccessId(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Step("�������� ������ � ���, ��� �� ������� ������ ��� ����������� - 400")
    public String courierLoginNotEnoughDataStatus400(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
    }

    @Step("�������� ������ � ���, ��� ������ ������� �� ���������� - 404")
    public String courierLoginCourierDoesNotExistStatus400(Response response) {
        return response
                .then().log().all()
                .assertThat()
                .statusCode(404)
                .extract()
                .path("message");
    }

    @Step("�������� ������ �� �������� �������� ������� - 201")
    public boolean courierCreatedStatus201(Response response) {
        return response.then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("ok");
    }

    @Step("�������� �������")
    public void deleteCourierSuccess(Response response) {
        response.then().log().all()
                .assertThat()
                .statusCode(200);
    }
}