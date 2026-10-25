package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

public class ContentTests extends BaseTest {

    @Test(enabled = false, description = "F001 - Create file",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void F001_createFile() {
    }

    @Test(enabled = false, description = "F002 - Read file",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void F002_readFile() {
    }

    @Test(enabled = false, description = "F003 - Update file with sha",
            groups = {TestGroups.REGRESSION})
    public void F003_updateFileWithSha() {
    }

    @Test(enabled = false, description = "F004 - Update file without sha",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F004_updateFileWithoutSha() {
    }

    @Test(enabled = false, description = "F005 - Delete file",
            groups = {TestGroups.REGRESSION})
    public void F005_deleteFile() {
    }

    @Test(enabled = false, description = "F006 - Read non-existent file",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F006_readNonExistentFile() {
    }

    @Test(enabled = false, description = "F007 - List directory",
            groups = {TestGroups.REGRESSION})
    public void F007_listDirectory() {
    }

    @Test(enabled = false, description = "F008 - Non-base64 content",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void F008_nonBase64Content() {
    }
}
