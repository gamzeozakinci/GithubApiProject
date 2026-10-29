package com.apitests.github.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {

    public static ResponseSpecification logAll() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification logBody() {
        return new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .build();
    }

    public static ResponseSpecification logHeaders() {
        return new ResponseSpecBuilder()
                .log(LogDetail.HEADERS)
                .build();
    }
}
