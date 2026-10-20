package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import com.apitests.github.utils.SchemaPaths;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class RepositoryTests extends BaseTest {

    @Test(description = "R001 - Create repo with minimal body",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R001_createRepoWithMinimalBody() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"test_repo1\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("name", equalTo("test_repo1"))
                .body("owner.login", equalTo("gamzeozakinci"));

    }

    @Test(description = "R002 - Create private repo with description",
            groups = {TestGroups.REGRESSION})
    public void R002_createPrivateRepoWithDescription() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"test_repo2\", \"private\": true}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("private", equalTo(true));
    }

    @Test(description = "R003 - Create repo with auto_init",
            groups = {TestGroups.REGRESSION})
    public void R003_createRepoWithAutoInit() {
        String repoName = "test_repo6";
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + repoName + "\", \"private\": true, \"auto_init\": true}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        //check auto_init
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", repoName)
                .when()
                .get("/repos/{owner}/{repo}/contents/README.md")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200);
    }

    @Test(description = "R004 - Create repo without name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R004_createRepoWithoutName() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"private\": true}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("New repository name must not be blank"));
    }

    @Test(description = "R005 - Create duplicate repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R005_createDuplicateRepo() {
        String repoName = "test_repo6";
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + repoName + "\", \"private\": true, \"auto_init\": true}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("errors[0].message", containsString("name already exists"));
    }

    @Test(description = "R006 - Create repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R006_createRepoWithoutAuth() {
        given()
                .spec(RequestSpecs.publicSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"test_repo7\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401)
                .body("message", containsString("Requires authentication"));

    }

    @Test(description = "R007 - Create repo with invalid characters in name",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R007_createRepoWithInvalidCharactersInName() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"test_repo2***\", \"private\": true}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);
        //repo oluşturuyor ama isimdeki * kldırıp - koyuyor sonra bak
    }

    @Test(description = "R008 - Response matches repo schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void R008_responseMatchesRepoSchema() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"test_repo8\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(SchemaPaths.REPOSITORY));
    }

    @Test(description = "R009 - Get repo by owner/name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R009_getRepoByOwnerName() {
        String repoName = "GratisProject";
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", repoName)
                .when()
                .get("/repos/{owner}/{repo}")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200);
    }

    @Test(description = "R010 - Get non-existent repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R010_getNonExistentRepo() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", "nonexistRepo")
                .when()
                .get("/repos/{owner}/{repo}")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", containsString("Not Found"));
    }

    @Test(description = "R011 - Get private repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R011_getPrivateRepoWithoutAuth() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", "test_repo3")
                .when()
                .get("/repos/{owner}/{repo}")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(400);
    }

    @Test(description = "R012 - List my repos contains created repo",
            groups = {TestGroups.REGRESSION})
    public void R012_listMyReposContainsCreatedRepo() {
        String repoName = "test_repo9";

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + repoName + "\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .statusCode(201);

        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("sort", "created")
                .queryParam("direction", "desc")
                .queryParam("per_page", 100)
                .when()
                .get(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", hasItem(repoName));
    }

    @Test(description = "R013 - Filter my repos by visibility",
            groups = {TestGroups.REGRESSION})
    public void R013_filterMyReposByVisibility() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("visibility", "private")
                .queryParam("per_page", 100)
                .when()
                .get(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("private", everyItem(equalTo(true)));
    }

    @Test(description = "R014 - Update description",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R014_updateDescription() {
        String owner = "gamzeozakinci";
        String repoName = "test-repo10";
        String newDescription = "updated description";

        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + repoName + "\"}")
                .when().post(Endpoints.CREATEREPO)
                .then().statusCode(201)
                .spec(ResponseSpecs.logAll());

        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"description\": \"" + newDescription + "\"}")
                .when().patch(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("description", equalTo(newDescription));

        given().spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when().get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("description", equalTo(newDescription));
    }

    @Test(description = "R015 - Rename repo",
            groups = {TestGroups.REGRESSION})
    public void R015_renameRepo() throws InterruptedException {
        String owner = "gamzeozakinci";
        String oldName = "test-repo16";
        String newName = "test-repo16-renamed";

        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + oldName + "\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        Response response = null;

        for (int attempt = 1; attempt <= 6; attempt++) {
            response = given().spec(RequestSpecs.authSpec())
                    .contentType(ContentType.JSON)
                    .pathParam("owner", owner)
                    .pathParam("repo", oldName)
                    .body("{\"name\": \"" + newName + "\"}")
                    .when().patch(Endpoints.REPO);

            if (response.statusCode() == 200) {
                break;
            }
            Thread.sleep(2000);
        }

        response.then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", equalTo(newName));

        given().spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", newName)
                .when()
                .get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200);

        given().spec(RequestSpecs.authSpec())
                .redirects().follow(false)
                .pathParam("owner", owner)
                .pathParam("repo", oldName)
                .when().get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(301);
    }

    @Test(description = "R016 - Archive repo",
            groups = {TestGroups.REGRESSION})
    public void R016_archiveRepo() {
        String owner = "gamzeozakinci";
        String repoName = "test-repo13";

        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + repoName + "\"}")
                .when()
                .post(Endpoints.CREATEREPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"archived\": true}")
                .when().patch(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("archived", equalTo(true));

        given().spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("archived", equalTo(true));
    }

    @Test(description = "R017 - Update repo owned by someone else",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R017_updateRepoOwnedBySomeoneElse() {
        given().spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", "octocat")
                .pathParam("repo", "Hello-World")
                .body("{\"description\": \"should not be allowed\"}")
                .when().patch(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(anyOf(equalTo(403), equalTo(404)));
    }

    @Test(description = "R018 - Delete repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R018_deleteRepo() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", "test_repo2")
                .when()
                .delete("/repos/{owner}/{repo}")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);
    }

    @Test(description = "R019 - Delete already deleted repo",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R019_deleteAlreadyDeletedRepo() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", "test_repo2")
                .when()
                .delete("/repos/{owner}/{repo}")
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", containsString("Not Found"));
    }
}