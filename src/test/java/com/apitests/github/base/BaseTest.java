package com.apitests.github.base;

import com.apitests.github.constants.Endpoints;
import com.apitests.github.specs.RequestSpecs;

import static io.restassured.RestAssured.given;

public class BaseTest {

    private static String login;

    // The account of the token, so anyone can run the tests with their own GITHUB_TOKEN.
    protected final String owner = currentLogin();

    private static synchronized String currentLogin() {
        if (login == null) {
            login = given()
                    .spec(RequestSpecs.authSpec())
                    .when()
                    .get(Endpoints.USER)
                    .then()
                    .statusCode(200)
                    .extract().path("login");
        }
        return login;
    }
}
