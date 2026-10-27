package com.apitests.github.tests;

import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.Endpoints;
import com.apitests.github.constants.TestGroups;
import com.apitests.github.specs.RequestSpecs;
import com.apitests.github.specs.ResponseSpecs;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertTrue;

public class RateLimitTests extends BaseTest {

    @Test(description = "L001 - Rate limit endpoint returns quota",
            groups = {TestGroups.REGRESSION})
    public void L001_rateLimitEndpointReturnsQuota() {
        given()
                .spec(RequestSpecs.publicSpec())
                .when()
                .get(Endpoints.RATE_LIMIT)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200);
    }

    @Test(description = "L002 - Rate limit headers present on normal responses",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void L002_rateLimitHeadersPresentOnNormalResponses() {
        given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .queryParam("per_page", 1)
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .body("X-RateLimit", notNullValue());
    }

    @Test(description = "L003 - Remaining decreases between calls",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void L003_usedIncreasesBetweenCalls() {
        int used1 = getUsedRateLimit();
        int used2 = getUsedRateLimit();

        assertTrue(used2 > used1);
    }

    private int getUsedRateLimit() {
        String used = given()
                .spec(RequestSpecs.publicSpec())
                .pathParam("username", "gamzeozakinci")
                .when()
                .get(Endpoints.REPOS)
                .then()
                .spec(ResponseSpecs.logAll())
                .statusCode(200)
                .extract().header("X-RateLimit-Used");

        return Integer.parseInt(used);
    }
}
