package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

public class SearchTests extends BaseTest {

    @Test(enabled = false, description = "S001 - Search repositories by keyword",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void S001_searchRepositoriesByKeyword() {
    }

    @Test(enabled = false, description = "S002 - Search with language qualifier",
            groups = {TestGroups.REGRESSION})
    public void S002_searchWithLanguageQualifier() {
    }

    @Test(enabled = false, description = "S003 - Sort by stars descending",
            groups = {TestGroups.REGRESSION})
    public void S003_sortByStarsDescending() {
    }

    @Test(enabled = false, description = "S004 - Missing q",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void S004_missingQ() {
    }

    @Test(enabled = false, description = "S005 - No results",
            groups = {TestGroups.REGRESSION})
    public void S005_noResults() {
    }

    @Test(enabled = false, description = "S006 - Search users",
            groups = {TestGroups.REGRESSION})
    public void S006_searchUsers() {
    }

    @Test(enabled = false, description = "S007 - Pagination",
            groups = {TestGroups.REGRESSION})
    public void S007_pagination() {
    }
}
