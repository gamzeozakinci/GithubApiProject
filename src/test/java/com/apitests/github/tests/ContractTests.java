package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ContractTests extends BaseTest {

    @Test(description = "X001 - Content type",
            groups = {TestGroups.CONTRACT})
    public void X001_contentType() {
        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", owner)
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logHeaders())
                .statusCode(200)
                .header("Content-Type", equalTo("application/json; charset=utf-8"));

        System.out.println("-----------------------------------------");

        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "this-user-does-not-exist-" + System.currentTimeMillis())
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logHeaders())
                .statusCode(404)
                .header("Content-Type", equalTo("application/json; charset=utf-8"));
    }

    @Test(description = "X002 - Response time",
            groups = {TestGroups.CONTRACT})
    public void X002_responseTime() {
        long maxTimeMs = 5000L;

        Response response = given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", owner)
                .when()
                .get(Endpoints.USER_BY_NAME);

        System.out.println("Expected response time: less than " + maxTimeMs + " ms");
        System.out.println("Actual response time:   " + response.time() + " ms");

        response.then()
                .statusCode(200)
                .time(lessThan(maxTimeMs));
    }

    @Test(description = "X003 - Error body shape",
            groups = {TestGroups.CONTRACT})
    public void X003_errorBodyShape() {
        // 404: the user does not exist
        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "this-user-does-not-exist-" + System.currentTimeMillis())
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(404)
                .body("message", notNullValue())
                .body("documentation_url", notNullValue());

        // 401: no token for an endpoint that needs one
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(401)
                .body("message", notNullValue())
                .body("documentation_url", notNullValue());

        // 422: search without the required q parameter
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get("/search/repositories")
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(422)
                .body("message", notNullValue())
                .body("documentation_url", notNullValue());
    }

    @Test(description = "X004 - Unsupported method",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X004_unsupportedMethod() {
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .delete(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(anyOf(equalTo(404), equalTo(405)));
    }

    @Test(description = "X005 - Wrong API version header",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X005_wrongAPIVersionHeader() {
        given()
                .spec(RequestSpecs.authSpec("1999-01-01"))
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(400)
                .body("message", equalTo("Bad Request"));
    }

    @Test(description = "X006 - Unknown route",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X006_unknownRoute() {
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get("/this-route-does-not-exist")
                .then()
                .spec(ResponseSpecs.logBody())
                .statusCode(404)
                .body("message", equalTo("Not Found"));
    }
}
