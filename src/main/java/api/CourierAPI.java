package api;

import pojo.CourierCredentials;
import pojo.CourierPOJO;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class CourierAPI extends MainAPI {
    @Step("Послать POST запрос на ручку /courier")
    public Response sendPostRequestCreateCourier(CourierPOJO courierPOJO) {
        return reqSpec.body(courierPOJO)
                .when()
                .post("/courier");
    }

    @Step("Авторизация")
    public Response sendPostLoginCourier(CourierCredentials credentials) {
        return reqSpec.body(credentials)
                .when()
                .post("/courier/login");
    }

    @Step("Удаление курьера")
    public Response sendDeleteCourier(int courierId) {
        return reqSpec.pathParam("courierId", courierId)
                .when()
                .delete("/courier/{courierId}");
    }

}
