package com.apitests.github.tests;

import com.apitests.github.api.RepositoryApi;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import com.apitests.github.utils.SchemaPaths;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class RepositoryTests extends BaseTest {

    // Shared private repository for the tests that only read or update a repository (R009, R011-R014).
    // Every other test creates its own repository with a unique name and deletes it again.
    private String repoName;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        repoName = uniqueName("test-repo-");
        RepositoryApi.create(repoName, false, true).then().statusCode(201);
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    @Test(description = "R001 - Create repo with minimal body",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R001_createRepoWithMinimalBody() {
        String name = uniqueName("test-repo-minimal-");

        try {
            RepositoryApi.createWithBody("{\"name\": \"" + name + "\"}")
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201)
                    .body("name", equalTo(name))
                    .body("owner.login", equalTo(owner));
        } finally {
            RepositoryApi.delete(owner, name);
        }
    }

    @Test(description = "R002 - Create private repo with description",
            groups = {TestGroups.REGRESSION})
    public void R002_createPrivateRepoWithDescription() {
        String name = uniqueName("test-repo-private-");

        try {
            RepositoryApi.createWithBody("{\"name\": \"" + name + "\", \"private\": true, \"description\": \"private repo\"}")
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201)
                    .body("private", equalTo(true))
                    .body("description", equalTo("private repo"));
        } finally {
            RepositoryApi.delete(owner, name);
        }
    }

    @Test(description = "R003 - Create repo with auto_init",
            groups = {TestGroups.REGRESSION})
    public void R003_createRepoWithAutoInit() {
        String name = uniqueName("test-repo-autoinit-");

        try {
            RepositoryApi.create(name, true, true)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201);

            //check auto_init
            given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("owner", owner)
                    .pathParam("repo", name)
                    .when()
                    .get(Endpoints.CONTENTS + "/README.md")
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200);
        } finally {
            RepositoryApi.delete(owner, name);
        }
    }

    @Test(description = "R004 - Create repo without name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R004_createRepoWithoutName() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body("{\"private\": true}")
                .when()
                .post(Endpoints.CREATE_REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("New repository name must not be blank"));
    }

    @Test(description = "R005 - Create duplicate repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R005_createDuplicateRepo() {
        String name = uniqueName("test-repo-duplicate-");

        try {
            RepositoryApi.create(name).then().statusCode(201);

            RepositoryApi.create(name)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(422)
                    .body("errors[0].message", containsString("name already exists"));
        } finally {
            RepositoryApi.delete(owner, name);
        }
    }

    @Test(description = "R006 - Create repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R006_createRepoWithoutAuth() {
        given()
                .spec(RequestSpecs.publicSpec())
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + uniqueName("test-repo-noauth-") + "\"}")
                .when()
                .post(Endpoints.CREATE_REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401)
                .body("message", containsString("Requires authentication"));

    }

    @Test(description = "R007 - Create repo with invalid characters in name",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R007_createRepoWithInvalidCharactersInName() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String expectedName = "test_repo7-" + suffix;

        try {
            RepositoryApi.createWithBody("{\"name\": \"test_repo7***" + suffix + "\", \"private\": true}")
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201)
                    .body("name", equalTo(expectedName));
        } finally {
            RepositoryApi.delete(owner, expectedName);
        }
    }

    @Test(description = "R008 - Response matches repo schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void R008_responseMatchesRepoSchema() {
        String name = uniqueName("test-repo-schema-");

        try {
            RepositoryApi.create(name)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201)
                    .body(matchesJsonSchemaInClasspath(SchemaPaths.REPOSITORY));
        } finally {
            RepositoryApi.delete(owner, name);
        }
    }

    @Test(description = "R009 - Get repo by owner/name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R009_getRepoByOwnerName() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", equalTo(repoName));
    }

    @Test(description = "R010 - Get non-existent repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R010_getNonExistentRepo() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", uniqueName("nonexistent-repo-"))
                .when()
                .get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", containsString("Not Found"));
    }

    @Test(description = "R011 - Get private repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R011_getPrivateRepoWithoutAuth() {
        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", containsString("Not Found"));
    }

    @Test(description = "R012 - List my repos contains created repo",
            groups = {TestGroups.REGRESSION})
    public void R012_listMyReposContainsCreatedRepo() {
        given()
                .spec(RequestSpecs.authSpec())
                .queryParam("sort", "created")
                .queryParam("direction", "desc")
                .queryParam("per_page", 100)
                .when()
                .get(Endpoints.CREATE_REPO)
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
                .queryParam("sort", "created")
                .queryParam("direction", "desc")
                .queryParam("per_page", 100)
                .when()
                .get(Endpoints.CREATE_REPO)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", hasItem(repoName))
                .body("private", everyItem(equalTo(true)));
    }

    @Test(description = "R014 - Update description",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R014_updateDescription() {
        String newDescription = "updated description";

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
        String oldName = uniqueName("test-repo-rename-");
        String newName = oldName + "-renamed";
        String nameToDelete = oldName;

        try {
            RepositoryApi.create(oldName).then().statusCode(201);

            Response response = null;

            for (int attempt = 1; attempt <= 6; attempt++) {
                response = given().spec(RequestSpecs.authSpec())
                        .contentType(ContentType.JSON)
                        .pathParam("owner", owner)
                        .pathParam("repo", oldName)
                        .body("{\"name\": \"" + newName + "\"}")
                        .when().patch(Endpoints.REPO);

                if (response.statusCode() == 200) {
                    nameToDelete = newName;
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
        } finally {
            RepositoryApi.delete(owner, nameToDelete);
        }
    }

    @Test(description = "R016 - Archive repo",
            groups = {TestGroups.REGRESSION})
    public void R016_archiveRepo() {
        String name = uniqueName("test-repo-archive-");

        try {
            RepositoryApi.create(name).then().statusCode(201);

            given().spec(RequestSpecs.authSpec())
                    .contentType(ContentType.JSON)
                    .pathParam("owner", owner)
                    .pathParam("repo", name)
                    .body("{\"archived\": true}")
                    .when().patch(Endpoints.REPO)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200)
                    .body("archived", equalTo(true));

            given().spec(RequestSpecs.authSpec())
                    .pathParam("owner", owner)
                    .pathParam("repo", name)
                    .when()
                    .get(Endpoints.REPO)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200)
                    .body("archived", equalTo(true));
        } finally {
            RepositoryApi.delete(owner, name);
        }
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
        String name = uniqueName("test-repo-delete-");
        RepositoryApi.create(name).then().statusCode(201);

        RepositoryApi.delete(owner, name)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);
    }

    @Test(description = "R019 - Delete already deleted repo",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R019_deleteAlreadyDeletedRepo() {
        String name = uniqueName("test-repo-deleted-");
        RepositoryApi.create(name).then().statusCode(201);
        RepositoryApi.delete(owner, name).then().statusCode(204);

        RepositoryApi.delete(owner, name)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", containsString("Not Found"));
    }

    private String uniqueName(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
