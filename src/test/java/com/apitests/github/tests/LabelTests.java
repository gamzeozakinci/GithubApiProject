package com.apitests.github.tests;

import com.apitests.github.api.RepositoryApi;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.http.ContentType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LabelTests extends BaseTest {

    private String repoName;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        repoName = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(repoName).then().statusCode(201);
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    @Test(description = "B001 - Create label",
            groups = {TestGroups.REGRESSION})
    public void B001_createLabel() {
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"test-label\",  \"color\": \"ff6666\", \"description\": \"test_label description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("id", notNullValue())
                .body("color", equalTo("ff6666"))
                .body("description", equalTo("test_label description"));
    }

    @Test(description = "B002 - Duplicate label",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void B002_duplicateLabel() {
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"dup_label\", \"color\": \"ff6666\", \"description\": \"dup_label description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"dup_label\", \"color\": \"ff6666\", \"description\": \"dup_label description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Validation Failed"))
                .body("errors[0].code", equalTo("already_exists"));
    }

    @Test(description = "B003 - Invalid color",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void B003_invalidColor() {
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"invalid_color\", \"color\": \"ff666226\", \"description\": \"invalid_color description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Validation Failed"));
    }

    @Test(description = "B004 - List labels",
            groups = {TestGroups.REGRESSION})
    public void B004_listLabels() {
        String labelName1 = "list-label-1-" + System.currentTimeMillis();
        String labelName2 = "list-label-2-" + System.currentTimeMillis();
        String labelName3 = "list-label-3-" + System.currentTimeMillis();

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"" + labelName1 + "\", \"color\": \"ff6666\", \"description\": \"list label 1\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"" + labelName2 + "\", \"color\": \"ff6666\", \"description\": \"list label 2\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"" + labelName3 + "\", \"color\": \"ff6666\", \"description\": \"list label 3\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .statusCode(201);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", hasItems(labelName1, labelName2, labelName3));
    }

    @Test(description = "B005 - Delete label",
            groups = {TestGroups.REGRESSION})
    public void B005_deleteLabel() {
        String labelName = "delete-label-" + System.currentTimeMillis();
        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"" + labelName + "\",\"color\": \"ff6666\", \"description\": \"test_label description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("name", labelName)
                .when()
                .delete(Endpoints.LABEL)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("name", labelName)
                .when()
                .get(Endpoints.LABEL)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404);
    }

    @Test(description = "B006 - Update label",
            groups = {TestGroups.REGRESSION})
    public void B006_updateLabel() {
        String labelName = "update-label-" + System.currentTimeMillis();

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"name\": \"" + labelName + "\", \"color\": \"ff6666\", \"description\": \"old description\"}")
                .when()
                .post(Endpoints.LABELS)
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("name", labelName)
                .body("{\"color\": \"00ff00\", \"description\": \"new description\"}")
                .when()
                .patch(Endpoints.LABEL)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", equalTo(labelName))
                .body("color", equalTo("00ff00"))
                .body("description", equalTo("new description"));

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("name", labelName)
                .when()
                .get(Endpoints.LABEL)
                .then()
                .statusCode(200)
                .body("color", equalTo("00ff00"))
                .body("description", equalTo("new description"));
    }
}
