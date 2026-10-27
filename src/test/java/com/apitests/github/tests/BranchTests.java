package com.apitests.github.tests;

import com.apitests.github.api.RepositoryApi;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BranchTests extends BaseTest {

    private String repoName;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        repoName = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(repoName, true).then().statusCode(201);
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    @Test(description = "H001 - List branches",
            groups = {TestGroups.REGRESSION})
    public void H001_listBranches() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.LIST_BRANCHES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", hasItem("main"));
    }

    @Test(description = "H002 - Get single branch",
            groups = {TestGroups.REGRESSION})
    public void H002_getSingleBranch() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", "main")
                .when()
                .get(Endpoints.BRANCH)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", equalTo("main"))
                .body("commit.sha", notNullValue());
    }

    @Test(description = "H003 - Get non-existent branch",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void H003_getNonExistentBranch() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", "testBranch")
                .when()
                .get(Endpoints.BRANCH)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", equalTo("Branch not found"));
    }

    @Test(description = "H004 - List commits",
            groups = {TestGroups.REGRESSION})
    public void H004_listCommits() {
        String filePath = "commit-file-" + System.currentTimeMillis() + ".txt";
        String content = Base64.getEncoder().encodeToString("hello".getBytes());

        String commitSha = given()
                .contentType(ContentType.JSON)
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .body("{\"message\":\"Test message\", \"content\":\"" + content + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .statusCode(201)
                .extract()
                .path("commit.sha");

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .when()
                .get(Endpoints.COMMITS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].sha", equalTo(commitSha));
    }

    @Test(description = "H005 - Create branch via git ref",
            groups = {TestGroups.REGRESSION})
    public void H005_createBranchViaGitRef() {
        String branchName = "feature-" + System.currentTimeMillis();
        String mainSha = given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", "main")
                .when()
                .get(Endpoints.BRANCH)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .extract().path("commit.sha");

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"ref\": \"refs/heads/" + branchName + "\", \"sha\": \"" + mainSha + "\"}")
                .when()
                .post(Endpoints.GIT_REFS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("ref", equalTo("refs/heads/" + branchName))
                .body("object.sha", equalTo(mainSha));
    }

    @Test(description = "H006 - Create duplicate ref",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void H006_createDuplicateRef() {
        String branchName = "duplicate-" + System.currentTimeMillis();
        createBranch(branchName).then().statusCode(201);

        createBranch(branchName)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Reference already exists"));
    }

    @Test(description = "H007 - Delete branch",
            groups = {TestGroups.REGRESSION})
    public void H007_deleteBranch() {
        String branchName = "delete-" + System.currentTimeMillis();
        createBranch(branchName).then().statusCode(201);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", branchName)
                .when()
                .delete(Endpoints.GIT_BRANCH_REF)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", branchName)
                .when()
                .get(Endpoints.BRANCH)
                .then()
                .statusCode(404);
    }

    private Response createBranch(String branchName) {
        String mainSha = given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("branch", "main")
                .when()
                .get(Endpoints.BRANCH)
                .then()
                .statusCode(200)
                .extract().path("commit.sha");

        return given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"ref\": \"refs/heads/" + branchName + "\", \"sha\": \"" + mainSha + "\"}")
                .when()
                .post(Endpoints.GIT_REFS);
    }
}
