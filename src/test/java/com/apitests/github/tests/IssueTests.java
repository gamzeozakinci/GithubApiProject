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
import static org.testng.AssertJUnit.assertEquals;

public class IssueTests extends BaseTest {

    private final String owner = "gamzeozakinci";
    private String repoName;

    // GitHub blocks content creation (secondary rate limit) when many repositories are created
    // in a short time, so the tests share one repository. Only the tests that need an empty
    // repository (I001, I012) create their own and delete it themselves.
    @BeforeClass
    public void createRepo() {
        repoName = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(repoName).then().statusCode(201);
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    @Test(description = "I001 - Create issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I001_createIssue() {
        String freshRepo = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(freshRepo).then().statusCode(201);

        try {
            given()
                    .spec(RequestSpecs.authSpec())
                    .contentType(ContentType.JSON)
                    .pathParam("owner", owner)
                    .pathParam("repo", freshRepo)
                    .body("{\"title\": \"Test issue\", \"body\": \"created by test\"}")
                    .when()
                    .post(Endpoints.REPO_ISSUES)
                    .then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(201)
                    .body("title", equalTo("Test issue"))
                    .body("state", equalTo("open"))
                    .body("number", equalTo(1));
        } finally {
            RepositoryApi.delete(owner, freshRepo);
        }
    }

    @Test(description = "I002 - Create issue without title",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I002_createIssueWithoutTitle() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
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
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
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
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
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
    }

    @Test(description = "I007 - Close issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I007_closeIssue() {
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

    @Test(description = "I008 - Reopen issue",
            groups = {TestGroups.REGRESSION})
    public void I008_reopenIssue() {
        int issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"issue to reopen\"}")
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
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .body("{\"state\": \"open\"}")
                .when()
                .patch(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("state", equalTo("open"))
                .body("closed_at", nullValue());

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .statusCode(200)
                .body("state", equalTo("open"));
    }

    @Test(description = "I009 - List open issues",
            groups = {TestGroups.REGRESSION})
    public void I009_listOpenIssues() throws InterruptedException {
        String issueTitle = "open issue " + System.currentTimeMillis();

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"" + issueTitle + "\", \"body\": \"created by test\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201);

        Response openList = null;
        boolean found = false;

        for (int attempt = 1; attempt <= 6 && !found; attempt++) {
            openList = given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("owner", owner)
                    .pathParam("repo", repoName)
                    .queryParam("state", "open")
                    .when()
                    .get(Endpoints.REPO_ISSUES);

            found = openList.statusCode() == 200
                    && openList.jsonPath().getList("title").contains(issueTitle);

            if (!found) {
                Thread.sleep(2000);
            }
        }

        openList.then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("title", hasItem(issueTitle))
                .body("state", everyItem(equalTo("open")));
    }

    @Test(description = "I010 - List closed issues",
            groups = {TestGroups.REGRESSION})
    public void I010_listClosedIssues() throws InterruptedException {
        String issueTitle = "closed issue " + System.currentTimeMillis();

        int issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"" + issueTitle + "\"}")
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
                .body("{\"state\": \"closed\", \"state_reason\": \"completed\"}")
                .when()
                .patch(Endpoints.REPO_ISSUE)
                .then()
                .statusCode(200)
                .body("state", equalTo("closed"));

        Response closedList = null;
        boolean found = false;

        for (int attempt = 1; attempt <= 6 && !found; attempt++) {
            closedList = given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("owner", owner)
                    .pathParam("repo", repoName)
                    .queryParam("state", "closed")
                    .when()
                    .get(Endpoints.REPO_ISSUES);

            found = closedList.statusCode() == 200
                    && closedList.jsonPath().getList("title").contains(issueTitle);

            if (!found) {
                Thread.sleep(2000);
            }
        }

        closedList.then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("title", hasItem(issueTitle))
                .body("state", everyItem(equalTo("closed")));
    }

    @Test(description = "I011 - Filter by label",
            groups = {TestGroups.REGRESSION})
    public void I011_filterByLabel() throws InterruptedException {
        String labeledTitle = "with label " + System.currentTimeMillis();
        String plainTitle = "without label " + System.currentTimeMillis();

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"" + labeledTitle + "\", \"labels\": [\"bug\"]}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .body("labels[0].name", equalTo("bug"));

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"" + plainTitle + "\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201);

        Response list = null;
        boolean found = false;
        for (int attempt = 1; attempt <= 6 && !found; attempt++) {
            list = given()
                    .spec(RequestSpecs.authSpec())
                    .pathParam("owner", owner)
                    .pathParam("repo", repoName)
                    .queryParam("labels", "bug")
                    .when()
                    .get(Endpoints.REPO_ISSUES);

            found = list.statusCode() == 200
                    && list.jsonPath().getList("title").contains(labeledTitle);
            if (!found) {
                Thread.sleep(2000);
            }
        }

        list.then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("title", hasItem(labeledTitle))
                .body("title", not(hasItem(plainTitle)))
                .body("labels.name", everyItem(hasItem("bug")));
    }

    @Test(description = "I012 - Issue list excludes nothing created by others in a fresh repo",
            groups = {TestGroups.REGRESSION})
    public void I012_issueListExcludesNothingCreatedByOthersInAFreshRepo() throws InterruptedException {
        String freshRepo = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(freshRepo).then().statusCode(201);

        try {
            int createdCount = 3;

            int lastNumber = 0;
            for (int i = 1; i <= createdCount; i++) {
                lastNumber = given()
                        .spec(RequestSpecs.authSpec())
                        .contentType(ContentType.JSON)
                        .pathParam("owner", owner)
                        .pathParam("repo", freshRepo)
                        .body("{\"title\": \"issue " + i + "\"}")
                        .when()
                        .post(Endpoints.REPO_ISSUES)
                        .then()
                        .statusCode(201)
                        .extract().path("number");
            }

            given()
                    .spec(RequestSpecs.authSpec())
                    .contentType(ContentType.JSON)
                    .pathParam("owner", owner)
                    .pathParam("repo", freshRepo)
                    .pathParam("number", lastNumber)
                    .body("{\"state\": \"closed\"}")
                    .when()
                    .patch(Endpoints.REPO_ISSUE)
                    .then()
                    .statusCode(200);

            Response list = null;
            for (int attempt = 1; attempt <= 6; attempt++) {
                list = given()
                        .spec(RequestSpecs.authSpec())
                        .pathParam("owner", owner)
                        .pathParam("repo", freshRepo)
                        .queryParam("state", "all")
                        .when()
                        .get(Endpoints.REPO_ISSUES);

                if (list.statusCode() == 200 && list.jsonPath().getList("number").size() == createdCount) {
                    break;
                }
                Thread.sleep(2000);
            }

            list.then()
                    .spec(ResponseSpecs.logAll())
                    .statusCode(200)
                    .body("size()", equalTo(createdCount));
        } finally {
            RepositoryApi.delete(owner, freshRepo);
        }
    }

    @Test(description = "I013 - Create issue without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I013_createIssueWithoutAuth() {
        given()
                .spec(RequestSpecs.publicSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"Test issue\", \"body\": \"created by test\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(401)
                .body("message", equalTo("Requires authentication"));
    }

    @Test(description = "I014 - Issue number increments",
            groups = {TestGroups.REGRESSION})
    public void I014_issueNumberIncrements() {
        int firstNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"first issue\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .extract().path("number");

        int secondNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"second issue\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .extract().path("number");

        assertEquals(secondNumber, firstNumber + 1);
    }

    @Test(description = "I015 - Response matches issue schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void I015_responseMatchesIssueSchema() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"Schema issue\", \"body\": \"created by test\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(SchemaPaths.ISSUE));
    }
}
