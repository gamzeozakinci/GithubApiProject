package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-14 (optional extension). Test cases: G001 .. G006.
 */
public class GistTests extends BaseTest {

    @Test(enabled = false, description = "G001 - Create gist",
            groups = {TestGroups.REGRESSION})
    public void G001_createGist() {
    }

    @Test(enabled = false, description = "G002 - Create gist without files",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void G002_createGistWithoutFiles() {
    }

    @Test(enabled = false, description = "G003 - Get gist",
            groups = {TestGroups.REGRESSION})
    public void G003_getGist() {
    }

    @Test(enabled = false, description = "G004 - Update gist",
            groups = {TestGroups.REGRESSION})
    public void G004_updateGist() {
    }

    @Test(enabled = false, description = "G005 - Delete gist",
            groups = {TestGroups.REGRESSION})
    public void G005_deleteGist() {
    }

    @Test(enabled = false, description = "G006 - Create gist without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void G006_createGistWithoutAuth() {
    }
}
