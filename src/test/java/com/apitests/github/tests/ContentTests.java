package com.apitests.github.tests;

import com.apitests.github.api.RepositoryApi;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class ContentTests extends BaseTest {

    private final String owner = "gamzeozakinci";
    private String repoName;
    private final String path = "file-" + System.currentTimeMillis() + ".txt";

    @BeforeClass
    public void setUp() {
        repoName = "test-repo-" + System.currentTimeMillis();
        RepositoryApi.create(repoName, true).then().statusCode(201);
    }

    @AfterClass(alwaysRun = true)
    public void deleteRepo() {
        RepositoryApi.delete(owner, repoName);
    }

    private Response createFile(String filePath) {
        String content = Base64.getEncoder().encodeToString("hello".getBytes());

        return given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Test message\", \"content\":\"" + content + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    @Test(description = "F001 - Create file",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void F001_createFile() {
        String content = Base64.getEncoder().encodeToString("hello".getBytes());

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", path)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Test message\", \"content\":\"" + content + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(201)
                .body("content.path", equalTo(path))
                .body("commit.sha", notNullValue());
    }

    @Test(description = "F002 - Read file",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void F002_readFile() {
        String filePath = "read-file-" + System.currentTimeMillis() + ".txt";
        String content = Base64.getEncoder().encodeToString("hello".getBytes());

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Test message\", \"content\":\"" + content + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .statusCode(201);

        String encodedContent = given()
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", equalTo(filePath))
                .body("encoding", equalTo("base64"))
                .extract()
                .path("content");

        String decodedContent = new String(Base64.getMimeDecoder().decode(encodedContent));
        Assert.assertEquals(decodedContent, "hello");
    }

    @Test(description = "F003 - Update file with sha",
            groups = {TestGroups.REGRESSION})
    public void F003_updateFileWithSha() {
        String filePath = "update-file-" + System.currentTimeMillis() + ".txt";
        Response created = createFile(filePath);
        String fileSha = created.path("content.sha");
        String firstCommitSha = created.path("commit.sha");
        String newContent = Base64.getEncoder().encodeToString("hello updated".getBytes());

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Update message\", \"content\":\"" + newContent + "\", \"sha\":\"" + fileSha + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("commit.sha", notNullValue())
                .body("commit.sha", not(equalTo(firstCommitSha)));
    }

    @Test(description = "F004 - Update file without sha",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F004_updateFileWithoutSha() {
        String filePath = "no-sha-file-" + System.currentTimeMillis() + ".txt";
        createFile(filePath);
        String newContent = Base64.getEncoder().encodeToString("hello updated".getBytes());

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Update message\", \"content\":\"" + newContent + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422)
                .body("message", containsString("sha"));
    }

    @Test(description = "F005 - Delete file",
            groups = {TestGroups.REGRESSION})
    public void F005_deleteFile() {
        String filePath = "delete-file-" + System.currentTimeMillis() + ".txt";
        String fileSha = createFile(filePath).path("content.sha");

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Delete message\", \"sha\":\"" + fileSha + "\"}")
                .when()
                .delete(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("commit", notNullValue());

        given()
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.CONTENT)
                .then()
                .statusCode(404);
    }

    @Test(description = "F006 - Read non-existent file",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F006_readNonExistentFile() {
        String filePath = "missing-file-" + System.currentTimeMillis() + ".txt";
        given()
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(404)
                .body("message", equalTo("Not Found"));
    }

    @Test(description = "F007 - List directory",
            groups = {TestGroups.REGRESSION})
    public void F007_listDirectory() {
        given()
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .spec(RequestSpecs.authSpec())
                .when()
                .get(Endpoints.CONTENTS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("name", hasItem("README.md"));
    }

    @Test(description = "F008 - Non-base64 content",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F008_nonBase64Content() {
        String filePath = "invalid-content-" + System.currentTimeMillis() + ".txt";
        String content = "%%%";

        given()
                .contentType(ContentType.JSON)
                .pathParam("owner", owner)
                .pathParam("repo", repoName)
                .pathParam("path", filePath)
                .spec(RequestSpecs.authSpec())
                .body("{\"message\":\"Test message\", \"content\":\"" + content + "\"}")
                .when()
                .put(Endpoints.CONTENT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(422);
    }
}
