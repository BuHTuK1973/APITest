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
    @DisplayName("�������� �������� �������") // ��� �����
    public void createCourierSuccess() {
        courier = CourierPOJO.getRandom();
        response = courierAPI.sendPostRequestCreateCourier(courier);
        created = courierCreatedStatus201(response);
        Assert.assertTrue("������ �� ��� ������", created);
    }

    @Test
    @DisplayName("�������� ������ ��� �������� ������� ��� ������") // ��� �����
    public void createCourierWithoutCredBadRequest400() {
        String expected = "������������ ������ ��� �������� ������� ������";
        courier = new CourierPOJO();
        response = courierAPI.sendPostRequestCreateCourier(courier);
        String responseMessage = courierNotCreatedStatus400(response);
        Assert.assertEquals("��������� ��������� �� ������", expected, responseMessage);
    }

    @Test
    @DisplayName("�������� ������ ��� �������� ������� � ��� ������������ �������") // ��� �����
    public void createCourierCantBeTheSameLogin409() {
        String expected = "���� ����� ��� ������������. ���������� ������.";
        courier = new CourierPOJO("1061n", "1061n", null);
        //�������� �������
        response = courierAPI.sendPostRequestCreateCourier(courier);
        //�������� �� ��, ��� ������ ������
        created = courierCreatedStatus201(response);
        //�������� �������
        response = courierAPI.sendPostRequestCreateCourier(courier);
        //������� ��������� � ���������
        String responseMessage = courierConflict409(response);
        Assert.assertEquals("��������� ��������� � ���, ��� �� � ����� ������� ��� ����", expected, responseMessage);
    }

    @Step("�������� ������ �� �������� �������� ������� - 201")
    public boolean courierCreatedStatus201(Response response) {
        return response.then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("ok");
    }

    @Step("�������� ������ � ���, ��� ����� ����� ��� ���������� �� ������� - 409")
    public String courierConflict409(Response response) {
        return response.then()
                .assertThat()
                .statusCode(409)
                .extract()
                .path("message");
    }

    @Step("�������� ������ � ���, ��� ������ �� ��� ������ - 400")
    public String courierNotCreatedStatus400(Response response) {
        return response.then()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
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

    @Step("�������� ������ �� �������� �������� �������")
    public void deleteCourierSuccess(Response response) {
        response.then().log().all()
                .assertThat()
                .statusCode(200);
    }
}


