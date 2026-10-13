package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-09. Test cases: B001 .. B005.
 */
public class LabelTests extends BaseTest {

    @Test(enabled = false, description = "B001 - Create label",
            groups = {TestGroups.REGRESSION})
    public void B001_createLabel() {
    }

    @Test(enabled = false, description = "B002 - Duplicate label",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void B002_duplicateLabel() {
    }

    @Test(enabled = false, description = "B003 - Invalid color",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void B003_invalidColor() {
    }

    @Test(enabled = false, description = "B004 - List labels",
            groups = {TestGroups.REGRESSION})
    public void B004_listLabels() {
    }

    @Test(enabled = false, description = "B005 - Delete label",
            groups = {TestGroups.REGRESSION})
    public void B005_deleteLabel() {
    }
}
