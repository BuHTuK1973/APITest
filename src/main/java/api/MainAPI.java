package api;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class MainAPI {
    protected final String URL = "http://qa-scooter.praktikum-services.ru/api/v1";
    protected final RequestSpecification reqSpec = given()
            .header("Content-Type", "application/json")
            .baseUri(URL);
}