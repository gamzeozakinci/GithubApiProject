package com.apitests.github.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    private static final String ACCEPT = "application/vnd.github+json";
    private static final String API_VERSION = "2022-11-28";

    public static RequestSpecification publicSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .addHeader("Accept", ACCEPT)
                .addHeader("X-GitHub-Api-Version", API_VERSION)
                .build();
    }

    public static RequestSpecification authSpec() {
        return authSpec(API_VERSION);
    }

    /**
     * Authenticated spec with a chosen API version, to test how the API reacts to other versions.
     */
    public static RequestSpecification authSpec(String apiVersion) {
        String token = System.getenv("GITHUB_TOKEN");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN environment variable is not set");
        }

        return new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .addHeader("Accept", ACCEPT)
                .addHeader("X-GitHub-Api-Version", apiVersion)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
