package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Cross-cutting checks (content type, error body shape, API version). Test cases: X001 .. X006.
 */
public class ContractTests extends BaseTest {

    @Test(enabled = false, description = "X001 - Content type",
            groups = {TestGroups.CONTRACT})
    public void X001_contentType() {
    }

    @Test(enabled = false, description = "X002 - Response time",
            groups = {TestGroups.CONTRACT})
    public void X002_responseTime() {
    }

    @Test(enabled = false, description = "X003 - Error body shape",
            groups = {TestGroups.CONTRACT})
    public void X003_errorBodyShape() {
    }

    @Test(enabled = false, description = "X004 - Unsupported method",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X004_unsupportedMethod() {
    }

    @Test(enabled = false, description = "X005 - Wrong API version header",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X005_wrongAPIVersionHeader() {
    }

    @Test(enabled = false, description = "X006 - Unknown route",
            groups = {TestGroups.NEGATIVE, TestGroups.CONTRACT})
    public void X006_unknownRoute() {
    }
}
