# GitHub REST API Tests

API test automation suite for the [GitHub REST API](https://docs.github.com/en/rest), built with REST Assured and TestNG.

> **Status:** work in progress.

## Tech stack

| Tool | Purpose |
|------|---------|
| Java 17 | Language |
| Maven | Build and dependency management |
| REST Assured 5.5.6 | HTTP client and response assertions |
| TestNG 7.11.0 | Test framework |
| Jackson | Request and response (de)serialization |
| Allure | Test reporting |

## Project structure

```
src/test/java/com/apitests/github/
├── base/          Common setup and teardown (BaseTest)
├── config/        Configuration and token handling (ConfigManager)
├── constants/     Endpoint paths and TestNG group names
├── specs/         Reusable request and response specifications
├── utils/         Test data factory, schema paths
├── api/           API clients, one per resource (users, repos, issues, ...)
├── models/
│   ├── request/   Request body models
│   └── response/  Response models
└── tests/         Test classes, one per feature area

src/test/resources/
├── testng.xml     Suite definition
├── config.properties
├── allure.properties
└── schemas/       JSON schemas for response validation
```

Tests call the `api` layer, which builds requests from `specs`, `constants` and `models`. Test classes contain no raw URLs or JSON strings.

## Test coverage

| Area | Test class |
|------|-----------|
| Authentication | `AuthenticationTests` |
| Rate limit | `RateLimitTests` |
| Users | `UserTests` |
| Repositories | `RepositoryTests` |
| Issues | `IssueTests` |
| Issue comments | `CommentTests` |
| Labels | `LabelTests` |
| Repository contents | `ContentTests` |
| Branches and commits | `BranchTests` |
| Pull requests | `PullRequestTests` |
| Search | `SearchTests` |
| Gists | `GistTests` |
| Cross-cutting contract checks | `ContractTests` |

The full list of user stories and test cases is in the [test plan](docs/GitHub-API-Test-Plan.pdf).

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- A GitHub personal access token with the `repo`, `gist` and `delete_repo` scopes

## Configuration

The token is read from the `GITHUB_TOKEN` environment variable. It is never stored in the repository.

```bash
# Linux / macOS
export GITHUB_TOKEN=<your-token>

# Windows (PowerShell)
$env:GITHUB_TOKEN = "<your-token>"
```

Non-secret settings (base URI, API version) live in `src/test/resources/config.properties`.

## Running the tests

```bash
mvn clean test
```

Test results are written to `target/allure-results`.

## Notes

- Each test run creates its own repository with a unique name and removes it afterwards, so tests do not depend on existing account data.
- GitHub rate limits apply: 5000 requests per hour when authenticated, and 30 requests per minute for search.
