package com.apitests.github.tests;

import org.testng.annotations.Test;
import com.apitests.github.base.BaseTest;
import com.apitests.github.constants.TestGroups;

/**
 * Covers US-08. Test cases: C001 .. C007.
 */
public class CommentTests extends BaseTest {

    @Test(enabled = false, description = "C001 - Add comment",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION})
    public void C001_addComment() {
    }

    @Test(enabled = false, description = "C002 - Add comment with empty body",
            groups = {TestGroups.SMOKE, TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void C002_addCommentWithEmptyBody() {
    }

    @Test(enabled = false, description = "C003 - List comments",
            groups = {TestGroups.REGRESSION})
    public void C003_listComments() {
    }

    @Test(enabled = false, description = "C004 - Update comment",
            groups = {TestGroups.REGRESSION})
    public void C004_updateComment() {
    }

    @Test(enabled = false, description = "C005 - Delete comment",
            groups = {TestGroups.REGRESSION})
    public void C005_deleteComment() {
    }

    @Test(enabled = false, description = "C006 - Comment count updates on issue",
            groups = {TestGroups.REGRESSION})
    public void C006_commentCountUpdatesOnIssue() {
    }

    @Test(enabled = false, description = "C007 - Comment on non-existent issue",
            groups = {TestGroups.REGRESSION, TestGroups.NEGATIVE})
    public void C007_commentOnNonExistentIssue() {
    }
}
