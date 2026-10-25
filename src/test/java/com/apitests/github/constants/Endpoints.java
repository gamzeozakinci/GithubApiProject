package com.apitests.github.constants;

public class Endpoints {

    public static final String USER = "/user";
    public static final String EMOJIS = "/emojis";
    public static final String USER_BY_NAME = "/users/{username}";
    public static final String REPOS = "/users/{username}/repos";
    public static final String RATE_LIMIT = "/rate_limit";
    public static final String CREATE_REPO = "/user/repos";
    public static final String REPO = "/repos/{owner}/{repo}";
    public static final String REPO_ISSUES = "/repos/{owner}/{repo}/issues";
    public static final String REPO_ISSUE = "/repos/{owner}/{repo}/issues/{number}";
    public static final String ISSUE_COMMENTS = "/repos/{owner}/{repo}/issues/{number}/comments";
    public static final String ISSUE_COMMENT = "/repos/{owner}/{repo}/issues/comments/{id}";
    public static final String CREATE_LABEL = "/repos/{owner}/{repo}/labels";
    public static final String GET_LABEL = "/repos/{owner}/{repo}/labels/{name}";
    public static final String CONTENTS = "/repos/{owner}/{repo}/contents";
    public static final String CONTENT = "/repos/{owner}/{repo}/contents/{path}";
    public static final String LIST_BRANCHES = "/repos/{owner}/{repo}/branches";
    public static final String BRANCH = "/repos/{owner}/{repo}/branches/{branch}";
    public static final String COMMITS = "/repos/{owner}/{repo}/commits";
}
