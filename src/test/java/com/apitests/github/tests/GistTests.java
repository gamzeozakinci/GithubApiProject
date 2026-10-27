package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GistTests extends BaseTest {

    @Test(description = "G001 - Create gist",
            groups = {TestGroups.REGRESSION})
    public void G001_createGist() {
        String gistId = given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .body("{\"files\": {\"a.txt\": {\"content\": \"hello\"}}, \"public\": false}")
                .when()
                .post(Endpoints.GISTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("public", equalTo(false))
                .body("files", hasKey("a.txt"))
                .extract().path("id");

        try {
            given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("id", gistId)
                    .when()
                    .get(Endpoints.GIST)
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(gistId))
                    .body("files.'a.txt'.content", equalTo("hello"));
        } finally {
            given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("id", gistId)
                    .when()
                    .delete(Endpoints.GIST);
        }
    }

    @Test(description = "G002 - Create gist without files",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void G002_createGistWithoutFiles() {
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .body("{}")
                .when()
                .post(Endpoints.GISTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422);
    }

    @Test(description = "G003 - Get gist",
            groups = {TestGroups.REGRESSION})
    public void G003_getGist() {
        String gistId = createGist();

        try {
            given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("id", gistId)
                    .when()
                    .get(Endpoints.GIST)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200)
                    .body("id", equalTo(gistId))
                    .body("files.'a.txt'.content", equalTo("hello"));
        } finally {
            deleteGist(gistId);
        }
    }

    @Test(description = "G004 - Update gist",
            groups = {TestGroups.REGRESSION})
    public void G004_updateGist() {
        String gistId = createGist();

        try {
            given()
                    .contentType(ContentType.JSON)
                    .spec(RequestSpecs.authSpec())
                    .pathParam("id", gistId)
                    .body("{\"files\": {\"a.txt\": {\"content\": \"updated\"}}}")
                    .when()
                    .patch(Endpoints.GIST)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200)
                    .body("files.'a.txt'.content", equalTo("updated"));

            given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("id", gistId)
                    .when()
                    .get(Endpoints.GIST)
                    .then()
                    .statusCode(200)
                    .body("files.'a.txt'.content", equalTo("updated"));
        } finally {
            deleteGist(gistId);
        }
    }

    @Test(description = "G005 - Delete gist",
            groups = {TestGroups.REGRESSION})
    public void G005_deleteGist() {
        String gistId = createGist();

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("id", gistId)
                .when()
                .delete(Endpoints.GIST)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("id", gistId)
                .when()
                .get(Endpoints.GIST)
                .then()
                .statusCode(404);
    }

    @Test(description = "G006 - Create gist without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void G006_createGistWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.publicSpec())
                .body("{\"files\": {\"a.txt\": {\"content\": \"hello\"}}, \"public\": false}")
                .when()
                .post(Endpoints.GISTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401);
    }

    private String createGist() {
        return given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .body("{\"files\": {\"a.txt\": {\"content\": \"hello\"}}, \"public\": false}")
                .when()
                .post(Endpoints.GISTS)
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private void deleteGist(String gistId) {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("id", gistId)
                .when()
                .delete(Endpoints.GIST);
    }
}
