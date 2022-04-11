package requests;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Requests {
    public static RequestSpecification userAuthURI = given()
            .contentType(ContentType.JSON)
            .basePath("auth/authorize");

    public static RequestSpecification userRefreshURI = given()
            .contentType(ContentType.JSON)
            .basePath("auth/refresh");

    public static RequestSpecification campaignURI = given()
            .contentType(ContentType.JSON)
            .basePath("campaigns/");
}
