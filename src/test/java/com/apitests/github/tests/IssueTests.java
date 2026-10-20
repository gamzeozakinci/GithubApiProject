package com.apitests.github.tests;

import com.apitests.github.api.RepositoryApi;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class IssueTests extends BaseTest {

    @Test(description = "I001 - Create issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I001_createIssue() {
        String repoName = "test_repo4";
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", repoName)
                .body("{\"title\": \"Test issue\", \"body\": \"created by test\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("title", equalTo("Test issue"))
                .body("state", equalTo("open"))
                .body("number", equalTo(1));
    }

    @Test(description = "I002 - Create issue without title",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I002_createIssueWithoutTitle() {
        String repoName = "test_repo4";
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", repoName)
                .body("{\"body\": \"created by test\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Invalid request.\n\n\"title\" wasn't supplied."));
    }

    @Test(description = "I003 - Create issue with labels and assignee",
            groups = {TestGroups.REGRESSION})
    public void I003_createIssueWithLabelsAndAssignee() {
        String repoName = "test_repo4";
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", repoName)
                .body("{\"title\": \"Issue with labels\", \"labels\": [\"bug\"], \"assignees\": [\"gamzeozakinci\"]}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("labels[0].name", equalTo("bug"))
                .body("assignee.login", equalTo("gamzeozakinci"));
    }

    @Test(description = "I004 - Get issue by number",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I004_getIssueByNumber() {
        String owner = "gamzeozakinci";
        String repoName = "test-repo4";

        RepositoryApi.create(repoName).then().statusCode(201);

        int issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"Test issue\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .extract().path("number");

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("number", equalTo(issueNumber))
                .body("title", equalTo("Test issue"));
    }

    @Test(description = "I005 - Get non-existent issue",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I005_getNonExistentIssue() {
        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", "gamzeozakinci")
                .pathParam("repo", "GratisProject")
                .pathParam("number", 999999)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", equalTo("Not Found"));
    }


    @Test(description = "I006 - Update title and body",
            groups = {TestGroups.REGRESSION})
    public void I006_updateTitleAndBody() {
        String owner = "gamzeozakinci";
        String repoName = "test-repo-" + System.currentTimeMillis();

        RepositoryApi.create(repoName).then().statusCode(201);

        int issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"old title\", \"body\": \"old body\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .extract().path("number");

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .body("{\"title\": \"new title\", \"body\": \"new body\"}")
                .when()
                .patch(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("title", equalTo("new title"))
                .body("body", equalTo("new body"));

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .statusCode(200)
                .body("title", equalTo("new title"))
                .body("body", equalTo("new body"));

        //sonra logbody ekle
    }


    @Test(description = "I007 - Close issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I007_closeIssue() {
        String owner = "gamzeozakinci";
        String repoName = "test-repo10";

        RepositoryApi.create(repoName).then().statusCode(201);

        int issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"issue to close\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .body("state", equalTo("open"))
                .extract().path("number");

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .body("{\"state\": \"closed\", \"state_reason\": \"completed\"}")
                .when()
                .patch(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("state", equalTo("closed"))
                .body("closed_at", notNullValue());

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .statusCode(200)
                .body("state", equalTo("closed"));
    }

    @Test(enabled = false, description = "I008 - Reopen issue",
            groups = {TestGroups.REGRESSION})
    public void I008_reopenIssue() {
    }

    @Test(enabled = false, description = "I009 - List open issues",
            groups = {TestGroups.REGRESSION})
    public void I009_listOpenIssues() {
    }

    @Test(enabled = false, description = "I010 - List closed issues",
            groups = {TestGroups.REGRESSION})
    public void I010_listClosedIssues() {
    }

    @Test(enabled = false, description = "I011 - Filter by label",
            groups = {TestGroups.REGRESSION})
    public void I011_filterByLabel() {
    }

    @Test(enabled = false, description = "I012 - Issue list excludes nothing created by others in a fresh repo",
            groups = {TestGroups.REGRESSION})
    public void I012_issueListExcludesNothingCreatedByOthersInAFreshRepo() {
    }

    @Test(enabled = false, description = "I013 - Create issue without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I013_createIssueWithoutAuth() {
    }

    @Test(enabled = false, description = "I014 - Issue number increments",
            groups = {TestGroups.REGRESSION})
    public void I014_issueNumberIncrements() {
    }

    @Test(enabled = false, description = "I015 - Response matches issue schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void I015_responseMatchesIssueSchema() {
    }
}
