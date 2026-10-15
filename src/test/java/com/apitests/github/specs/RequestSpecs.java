package com.apitests.github.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    public static RequestSpecification publicSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .build();
    }

    public static RequestSpecification authSpec() {
        String token = System.getenv("GITHUB_TOKEN");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN environment variable is not set");
        }

        return new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
