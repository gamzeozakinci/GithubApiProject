package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-11. Test cases: H001 .. H007.
 */
public class BranchTests extends BaseTest {

    @Test(enabled = false, description = "H001 - List branches",
            groups = {TestGroups.REGRESSION})
    public void H001_listBranches() {
    }

    @Test(enabled = false, description = "H002 - Get single branch",
            groups = {TestGroups.REGRESSION})
    public void H002_getSingleBranch() {
    }

    @Test(enabled = false, description = "H003 - Get non-existent branch",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void H003_getNonExistentBranch() {
    }

    @Test(enabled = false, description = "H004 - List commits",
            groups = {TestGroups.REGRESSION})
    public void H004_listCommits() {
    }

    @Test(enabled = false, description = "H005 - Create branch via git ref",
            groups = {TestGroups.REGRESSION})
    public void H005_createBranchViaGitRef() {
    }

    @Test(enabled = false, description = "H006 - Create duplicate ref",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void H006_createDuplicateRef() {
    }

    @Test(enabled = false, description = "H007 - Delete branch",
            groups = {TestGroups.REGRESSION})
    public void H007_deleteBranch() {
    }
}
