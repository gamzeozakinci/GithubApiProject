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

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BranchTests extends BaseTest {

    private final String owner = "gamzeozakinci";
    private String repoName;

    @BeforeClass
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

    @Test(enabled = false, description = "H005 - Create branch via git ref",
            groups = {TestGroups.REGRESSION})
    public void H005_createBranchViaGitRef() {
    }

    @Test(enabled = false, description = "H006 - Create duplicate ref",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void H006_createDuplicateRef() {
    }

    @Test(enabled = false, description = "H007 - Delete branch",
            groups = {TestGroups.REGRESSION})
    public void H007_deleteBranch() {
    }
}
