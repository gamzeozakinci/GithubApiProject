package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-02. Test cases: L001 .. L003.
 */
public class RateLimitTests extends BaseTest {

    @Test(enabled = false, description = "L001 - Rate limit endpoint returns quota",
            groups = {TestGroups.REGRESSION})
    public void L001_rateLimitEndpointReturnsQuota() {
    }

    @Test(enabled = false, description = "L002 - Rate limit headers present on normal responses",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void L002_rateLimitHeadersPresentOnNormalResponses() {
    }

    @Test(enabled = false, description = "L003 - Remaining decreases between calls",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void L003_remainingDecreasesBetweenCalls() {
    }
}
