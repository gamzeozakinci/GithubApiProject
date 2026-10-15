package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotEquals;

public class UserTests extends BaseTest {

    @Test(description = "U001 - Get existing user",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void U001_getExistingUser() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("username", "gamzeozakinci")
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("login", notNullValue())
                .body("id", notNullValue());
    }

    @Test(description = "U002 - Get non-existent user",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void U002_getNonExistentUser() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("username", "nonexistinguser")
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", equalTo("Not Found"));
    }

    @Test(description = "U003 - List public repos of a user",
            groups = {TestGroups.REGRESSION})
    public void U003_listPublicReposOfAUser() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("username", "gamzeozakinci")
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", notNullValue())
                .body("id", notNullValue());
    }

    @Test(description = "U004 - Pagination with per_page",
            groups = {TestGroups.REGRESSION})
    public void U004_paginationWithPerPage() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("username", "gamzeozakinci")
                .queryParam("per_page", 2)
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", notNullValue())
                .body("id", notNullValue());
    }

    @Test(description = "U005 - Pagination page returns different data",
            groups = {TestGroups.REGRESSION})
    public void U005_paginationPageReturnsDifferentData() {
        long idOnPage1 = getFirstRepoId(1);
        long idOnPage2 = getFirstRepoId(2);

        assertNotEquals(idOnPage1, idOnPage2);
    }

    private long getFirstRepoId(int page) {
        return given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .queryParam("per_page", 1)
                .queryParam("page", page)
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .extract().jsonPath().getLong("[0].id");
    }

    @Test(description = "U006 - Link header exposes pagination",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void U006_linkHeaderExposesPagination() {
        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .queryParam("per_page", 1)
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .header("Link", containsString("rel=\"next\""))
                .header("Link", containsString("rel=\"last\""));
    }

    @Test(description = "U007 - Conditional request with ETag",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void U007_conditionalRequestWithETag() {
        String etag = given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .extract().header("ETag");

        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .header("If-None-Match", etag)
                .when()
                .get(Endpoints.USER_BY_NAME)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(304)
                .body(emptyOrNullString());
    }
}
