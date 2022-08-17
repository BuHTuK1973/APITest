package api;

import pojo.OrderPOJO;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

public class OrderAPI extends MainAPI {
    @Step("������� POST ������ �� ����� /orders")
    public Response sendPostRequestCreateOrder(OrderPOJO orderPOJO) {
        return reqSpec.body(orderPOJO)
                .when()
                .post("/orders");
    }

    @Step("�������� ������ ��������� �������� ������ - 201")
    public int responseCreatedOrderStatus201(Response response) {
        return response.then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("track");
    }

    @Step("������� GET ������ �� ����� /orders, ����� �������� ������ �������")
    public List<Object[]> sendGetRequestGetOrderList() {
        return reqSpec.get("/orders")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("orders");
    }

}