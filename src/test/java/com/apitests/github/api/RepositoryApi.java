package com.apitests.github.api;

import com.apitests.github.constants.Endpoints;
import com.apitests.github.specs.RequestSpecs;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RepositoryApi {

    /**
     * POST /user/repos with only a name.
     */
    public static Response create(String name) {
        return create(name, false);
    }

    /**
     * POST /user/repos. With autoInit the repository is created with a README and a main branch.
     */
    public static Response create(String name, boolean autoInit) {
        return create(name, autoInit, false);
    }

    /**
     * POST /user/repos with a name, autoInit and visibility.
     */
    public static Response create(String name, boolean autoInit, boolean isPrivate) {
        return createWithBody("{\"name\": \"" + name + "\", \"auto_init\": " + autoInit
                + ", \"private\": " + isPrivate + "}");
    }

    /**
     * POST /user/repos with a body written by the caller.
     * GitHub asks to wait at least one second between write requests to avoid the secondary rate limit.
     */
    public static Response createWithBody(String body) {
        pause();

        Response response = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(Endpoints.CREATE_REPO);

        if (response.statusCode() != 201) {
            System.out.println("Repository creation failed for body '" + body + "': HTTP " + response.statusCode()
                    + ", retry-after=" + response.getHeader("Retry-After")
                    + ", ratelimit-remaining=" + response.getHeader("X-RateLimit-Remaining")
                    + ", ratelimit-resource=" + response.getHeader("X-RateLimit-Resource")
                    + ", body=" + response.asString());
        }

        return response;
    }

    /**
     * DELETE /repos/{owner}/{repo}. Needs a token with the delete_repo scope.
     */
    public static Response delete(String owner, String repo) {
        pause();

        return given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repo)
                .when()
                .delete(Endpoints.REPO);
    }

    private static void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
