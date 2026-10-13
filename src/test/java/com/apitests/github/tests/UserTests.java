package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-03. Test cases: U001 .. U008.
 */
public class UserTests extends BaseTest {

    @Test(enabled = false, description = "U001 - Get existing user",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void U001_getExistingUser() {
    }

    @Test(enabled = false, description = "U002 - Get non-existent user",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void U002_getNonExistentUser() {
    }

    @Test(enabled = false, description = "U003 - List public repos of a user",
            groups = {TestGroups.REGRESSION})
    public void U003_listPublicReposOfAUser() {
    }

    @Test(enabled = false, description = "U004 - Pagination with per_page",
            groups = {TestGroups.REGRESSION})
    public void U004_paginationWithPerPage() {
    }

    @Test(enabled = false, description = "U005 - Pagination page returns different data",
            groups = {TestGroups.REGRESSION})
    public void U005_paginationPageReturnsDifferentData() {
    }

    @Test(enabled = false, description = "U006 - Link header exposes pagination",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void U006_linkHeaderExposesPagination() {
    }

    @Test(enabled = false, description = "U007 - per_page above the maximum is capped",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void U007_perPageAboveTheMaximumIsCapped() {
    }

    @Test(enabled = false, description = "U008 - Conditional request with ETag",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void U008_conditionalRequestWithETag() {
    }
}
