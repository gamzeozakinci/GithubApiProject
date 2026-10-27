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

public class CommentTests extends BaseTest {

    private final String owner = "gamzeozakinci";
    private String repoName;
    private int issueNumber;

    @BeforeClass
    public void setUp() {
        repoName = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(repoName).then().statusCode(201);

        issueNumber = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"issue for comments\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .extract().path("number");
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    private long createComment(int issue, String text) {
        return given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issue)
                .body("{\"body\": \"" + text + "\"}")
                .when()
                .post(Endpoints.ISSUE_COMMENTS)
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test(description = "C001 - Add comment",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void C001_addComment() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .body("{\"body\": \"first comment\"}")
                .when()
                .post(Endpoints.ISSUE_COMMENTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("body", equalTo("first comment"))
                .body("user.login", equalTo(owner));
    }

    @Test(description = "C002 - Add comment with empty body",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void C002_addCommentWithEmptyBody() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .body("{\"body\": \"\"}")
                .when()
                .post(Endpoints.ISSUE_COMMENTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", equalTo("Validation Failed"))
                .body("errors[0].message", equalTo("Body cannot be blank"));
    }

    @Test(description = "C003 - List comments",
            groups = {TestGroups.REGRESSION})
    public void C003_listComments() {
        String text = "comment for the list " + System.currentTimeMillis();
        createComment(issueNumber, text);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", issueNumber)
                .when()
                .get(Endpoints.ISSUE_COMMENTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("body", hasItem(text));
    }

    @Test(description = "C004 - Update comment",
            groups = {TestGroups.REGRESSION})
    public void C004_updateComment() {
        long commentId = createComment(issueNumber, "old text");

        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("id", commentId)
                .body("{\"body\": \"new text\"}")
                .when()
                .patch(Endpoints.ISSUE_COMMENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("body", equalTo("new text"));

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("id", commentId)
                .when()
                .get(Endpoints.ISSUE_COMMENT)
                .then()
                .statusCode(200)
                .body("body", equalTo("new text"));
    }

    @Test(description = "C005 - Delete comment",
            groups = {TestGroups.REGRESSION})
    public void C005_deleteComment() {
        long commentId = createComment(issueNumber, "comment to delete");

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("id", commentId)
                .when()
                .delete(Endpoints.ISSUE_COMMENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(204);

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("id", commentId)
                .when()
                .get(Endpoints.ISSUE_COMMENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404);
    }

    @Test(description = "C006 - Comment count updates on issue",
            groups = {TestGroups.REGRESSION})
    public void C006_commentCountUpdatesOnIssue() {
        int ownIssue = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .body("{\"title\": \"issue for the comment count\"}")
                .when()
                .post(Endpoints.REPO_ISSUES)
                .then()
                .statusCode(201)
                .body("comments", equalTo(0))
                .extract().path("number");

        createComment(ownIssue, "the only comment");

        given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", ownIssue)
                .when()
                .get(Endpoints.REPO_ISSUE)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("comments", equalTo(1));
    }

    @Test(description = "C007 - Comment on non-existent issue",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void C007_commentOnNonExistentIssue() {
        given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("number", 999999)
                .body("{\"body\": \"comment on a missing issue\"}")
                .when()
                .post(Endpoints.ISSUE_COMMENTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", equalTo("Not Found"));
    }
}
