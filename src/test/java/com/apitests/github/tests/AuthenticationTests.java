package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import com.apitests.github.utils.SchemaPaths;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationTests extends BaseTest {

    @Test(description = "A001 - Get authenticated user with valid token",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void A001_getAuthenticatedUserWithValidToken() {
        given()
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("login", notNullValue())
                .body("id", notNullValue());
    }

    @Test(description = "A002 - Request without token",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void A002_requestWithoutToken() {
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401)
                .body("message", equalTo("Requires authentication"));
    }

    @Test(description = "A003 - Request with invalid token",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void A003_requestWithInvalidToken() {
        given()
                .spec(RequestSpecs.publicSpec())
                .header("Authorization", "Bearer WRONGTOKEN")
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401)
                .body("message", equalTo("Bad credentials"));
    }

    @Test(description = "A004 - Public resource works without token",
            groups = {TestGroups.REGRESSION})
    public void A004_publicResourceWorksWithoutToken() {
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get(Endpoints.EMOJIS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200);
    }

    @Test(description = "A005 - Response body matches user schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void A005_responseBodyMatchesUserSchema() {
        given()
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.USER)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(SchemaPaths.USER));
    }
}
