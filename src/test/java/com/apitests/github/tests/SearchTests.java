package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class SearchTests extends BaseTest {

    @Test(description = "S001 - Search repositories by keyword",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void S001_searchRepositoriesByKeyword() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", "rest-assured")
                .when()
                .get("/search/repositories")
                .then()
                .statusCode(200)
                .body("total_count", greaterThan(0))
                .body("items.size()", greaterThan(0));
    }

    @Test(description = "S002 - Search with language qualifier",
            groups = {TestGroups.REGRESSION})
    public void S002_searchWithLanguageQualifier() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", "rest-assured language:java")
                .when()
                .get("/search/repositories")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("items.size()", greaterThan(0))
                .body("items.language", everyItem(anyOf(equalTo("Java"), nullValue())));
    }

    @Test(description = "S003 - Sort by stars descending",
            groups = {TestGroups.REGRESSION})
    public void S003_sortByStarsDescending() {
        Response response = given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", "rest-assured")
                .queryParam("sort", "stars")
                .queryParam("order", "desc")
                .when()
                .get("/search/repositories");

        response.then().statusCode(200);

        List<Integer> stars = response.jsonPath().getList("items.stargazers_count");

        List<Integer> sortedDescending = new ArrayList<>(stars);
        sortedDescending.sort(Collections.reverseOrder());

        assertFalse(stars.isEmpty());
        assertEquals(stars, sortedDescending);
    }

    @Test(description = "S004 - Missing q",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void S004_missingQ() {
        given()
                .spec(RequestSpecs.authSpec())
                .when()
                .get("/search/repositories")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Validation Failed"))
                .body("errors[0].field", equalTo("q"))
                .body("errors[0].code", equalTo("missing"));
    }

    @Test(description = "S005 - No results",
            groups = {TestGroups.REGRESSION})
    public void S005_noResults() {
        String keyword = UUID.randomUUID().toString().replace("-", "");

        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", keyword)
                .when()
                .get("/search/repositories")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("total_count", equalTo(0))
                .body("items", empty());
    }

    @Test(description = "S006 - Search users",
            groups = {TestGroups.REGRESSION})
    public void S006_searchUsers() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", "octocat")
                .when()
                .get("/search/users")
                .then()
                .statusCode(200)
                .body("total_count", greaterThan(0))
                .body("items.login", hasItem("octocat"));
    }

    @Test(description = "S007 - Pagination",
            groups = {TestGroups.REGRESSION})
    public void S007_pagination() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("q", "java")
                .queryParam("per_page", 5)
                .when()
                .get("/search/repositories")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("items.size()", equalTo(5));
    }
}
