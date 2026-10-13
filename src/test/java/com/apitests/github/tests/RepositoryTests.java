package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-04, US-05, US-06. Test cases: R001 .. R020.
 */
public class RepositoryTests extends BaseTest {

    @Test(enabled = false, description = "R001 - Create repo with minimal body",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R001_createRepoWithMinimalBody() {
    }

    @Test(enabled = false, description = "R002 - Create private repo with description",
            groups = {TestGroups.REGRESSION})
    public void R002_createPrivateRepoWithDescription() {
    }

    @Test(enabled = false, description = "R003 - Create repo with auto_init",
            groups = {TestGroups.REGRESSION})
    public void R003_createRepoWithAutoInit() {
    }

    @Test(enabled = false, description = "R004 - Create repo without name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R004_createRepoWithoutName() {
    }

    @Test(enabled = false, description = "R005 - Create duplicate repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R005_createDuplicateRepo() {
    }

    @Test(enabled = false, description = "R006 - Create repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R006_createRepoWithoutAuth() {
    }

    @Test(enabled = false, description = "R007 - Create repo with invalid characters in name",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R007_createRepoWithInvalidCharactersInName() {
    }

    @Test(enabled = false, description = "R008 - Response matches repo schema",
            groups = {TestGroups.REGRESSION, TestGroups.CONTRACT})
    public void R008_responseMatchesRepoSchema() {
    }

    @Test(enabled = false, description = "R009 - Get repo by owner/name",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R009_getRepoByOwnerName() {
    }

    @Test(enabled = false, description = "R010 - Get non-existent repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R010_getNonExistentRepo() {
    }

    @Test(enabled = false, description = "R011 - Get private repo without auth",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R011_getPrivateRepoWithoutAuth() {
    }

    @Test(enabled = false, description = "R012 - List my repos contains created repo",
            groups = {TestGroups.REGRESSION})
    public void R012_listMyReposContainsCreatedRepo() {
    }

    @Test(enabled = false, description = "R013 - Filter my repos by visibility",
            groups = {TestGroups.REGRESSION})
    public void R013_filterMyReposByVisibility() {
    }

    @Test(enabled = false, description = "R014 - Update description",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R014_updateDescription() {
    }

    @Test(enabled = false, description = "R015 - Rename repo",
            groups = {TestGroups.REGRESSION})
    public void R015_renameRepo() {
    }

    @Test(enabled = false, description = "R016 - Archive repo",
            groups = {TestGroups.REGRESSION})
    public void R016_archiveRepo() {
    }

    @Test(enabled = false, description = "R017 - Update repo owned by someone else",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R017_updateRepoOwnedBySomeoneElse() {
    }

    @Test(enabled = false, description = "R018 - Delete repo",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void R018_deleteRepo() {
    }

    @Test(enabled = false, description = "R019 - Delete already deleted repo",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R019_deleteAlreadyDeletedRepo() {
    }

    @Test(enabled = false, description = "R020 - Delete without delete_repo scope",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void R020_deleteWithoutDeleteRepoScope() {
    }
}
