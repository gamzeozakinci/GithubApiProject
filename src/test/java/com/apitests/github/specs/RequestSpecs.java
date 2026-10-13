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
        return new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .addHeader("Authorization", "Bearer " + System.getenv("GITHUB_TOKEN"))
                .build();
    }
}
