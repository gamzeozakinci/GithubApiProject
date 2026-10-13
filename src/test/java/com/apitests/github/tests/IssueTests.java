package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-07. Test cases: I001 .. I015.
 */
public class IssueTests extends BaseTest {

    @Test(enabled = false, description = "I001 - Create issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I001_createIssue() {
    }

    @Test(enabled = false, description = "I002 - Create issue without title",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I002_createIssueWithoutTitle() {
    }

    @Test(enabled = false, description = "I003 - Create issue with labels and assignee",
            groups = {TestGroups.REGRESSION})
    public void I003_createIssueWithLabelsAndAssignee() {
    }

    @Test(enabled = false, description = "I004 - Get issue by number",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I004_getIssueByNumber() {
    }

    @Test(enabled = false, description = "I005 - Get non-existent issue",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I005_getNonExistentIssue() {
    }

    @Test(enabled = false, description = "I006 - Update title and body",
            groups = {TestGroups.REGRESSION})
    public void I006_updateTitleAndBody() {
    }

    @Test(enabled = false, description = "I007 - Close issue",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void I007_closeIssue() {
    }

    @Test(enabled = false, description = "I008 - Reopen issue",
            groups = {TestGroups.REGRESSION})
    public void I008_reopenIssue() {
    }

    @Test(enabled = false, description = "I009 - List open issues",
            groups = {TestGroups.REGRESSION})
    public void I009_listOpenIssues() {
    }

    @Test(enabled = false, description = "I010 - List closed issues",
            groups = {TestGroups.REGRESSION})
    public void I010_listClosedIssues() {
    }

    @Test(enabled = false, description = "I011 - Filter by label",
            groups = {TestGroups.REGRESSION})
    public void I011_filterByLabel() {
    }

    @Test(enabled = false, description = "I012 - Issue list excludes nothing created by others in a fresh repo",
            groups = {TestGroups.REGRESSION})
    public void I012_issueListExcludesNothingCreatedByOthersInAFreshRepo() {
    }

    @Test(enabled = false, description = "I013 - Create issue without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void I013_createIssueWithoutAuth() {
    }

    @Test(enabled = false, description = "I014 - Issue number increments",
            groups = {TestGroups.REGRESSION})
    public void I014_issueNumberIncrements() {
    }

    @Test(enabled = false, description = "I015 - Response matches issue schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void I015_responseMatchesIssueSchema() {
    }
}
