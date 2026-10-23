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
        String body = "{\"name\": \"" + name + "\", \"auto_init\": " + autoInit + "}";

        Response response = given()
                .spec(RequestSpecs.authSpec())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(Endpoints.CREATEREPO);

        if (response.statusCode() != 201) {
            System.out.println("Repository creation failed for '" + name + "': HTTP " + response.statusCode()
                    + ", retry-after=" + response.getHeader("Retry-After")
                    + ", ratelimit-remaining=" + response.getHeader("X-RateLimit-Remaining")
                    + ", ratelimit-resource=" + response.getHeader("X-RateLimit-Resource")
                    + ", body=" + response.asString());
        }

        return response;
    }

    /**
     * GET /repos/{owner}/{repo}.
     */
    public static Response get(String owner, String repo) {
        return given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repo)
                .when()
                .get(Endpoints.REPO);
    }

    /**
     * DELETE /repos/{owner}/{repo}. Needs a token with the delete_repo scope.
     */
    public static Response delete(String owner, String repo) {
        return given()
                .spec(RequestSpecs.authSpec())
                .pathParam("owner", owner)
                .pathParam("repo", repo)
                .when()
                .delete(Endpoints.REPO);
    }
}
