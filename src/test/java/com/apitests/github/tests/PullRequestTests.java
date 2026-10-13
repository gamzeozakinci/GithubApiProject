package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-12 (optional extension). Test cases: P001 .. P006.
 */
public class PullRequestTests extends BaseTest {

    @Test(enabled = false, description = "P001 - Open PR",
            groups = {TestGroups.REGRESSION})
    public void P001_openPR() {
    }

    @Test(enabled = false, description = "P002 - Open PR without changes",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void P002_openPRWithoutChanges() {
    }

    @Test(enabled = false, description = "P003 - Get PR",
            groups = {TestGroups.REGRESSION})
    public void P003_getPR() {
    }

    @Test(enabled = false, description = "P004 - Merge PR",
            groups = {TestGroups.REGRESSION})
    public void P004_mergePR() {
    }

    @Test(enabled = false, description = "P005 - Merge already merged PR",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void P005_mergeAlreadyMergedPR() {
    }

    @Test(enabled = false, description = "P006 - Close PR without merge",
            groups = {TestGroups.REGRESSION})
    public void P006_closePRWithoutMerge() {
    }
}
