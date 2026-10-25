# GitHub REST API Tests

API test automation suite for the [GitHub REST API](https://docs.github.com/en/rest), built with REST Assured and TestNG.

101 automated test cases for authentication, rate limit, users, repositories, issues, issue comments, labels, repository contents, branches, pull requests, search, gists and cross-cutting contract checks, see [Test coverage](#test-coverage).

## Tech stack

| Tool | Purpose |
|------|---------|
| Java 17 | Language |
| Maven | Build and dependency management |
| REST Assured 5.5.6 | HTTP client and response assertions |
| TestNG 7.11.0 | Test framework |
| JSON Schema Validator 5.5.6 | Response contract checks |
| Jackson | Request and response (de)serialization |
| TestNG reports | Test reporting (Surefire HTML and XML reports) |

## Project structure

```
src/test/java/com/apitests/github/
├── base/          Base class of all test classes (BaseTest)
├── constants/     Endpoint paths and TestNG group names
├── specs/         Reusable request and response specifications
├── utils/         Paths of the JSON schema files
├── api/           RepositoryApi: create, read and delete a test repository
└── tests/         Test classes, one per feature area

src/test/resources/
├── testng.xml     Suite definition
└── schemas/       JSON schemas for response validation
```

The tests build their requests with the shared `RequestSpecs` and `ResponseSpecs` and the paths in `Endpoints`. Creating, reading and deleting a repository goes through `RepositoryApi`, which the issue and comment tests use for their test repository.

Every test method carries its test case ID in the name and in its `description`, for example `R009_getRepoByOwnerName`. Tests are tagged with the TestNG groups `smoke`, `regression`, `negative` and `contract` (see `TestGroups`).

## Test coverage

| Area | Test class | Test cases |
|------|-----------|------------|
| Authentication | `AuthenticationTests` | 5 |
| Rate limit | `RateLimitTests` | 3 |
| Users | `UserTests` | 7 |
| Repositories | `RepositoryTests` | 19 |
| Issues | `IssueTests` | 15 |
| Issue comments | `CommentTests` | 7 |
| Labels | `LabelTests` | 5 |
| Repository contents | `ContentTests` | 8 |
| Branches and commits | `BranchTests` | 7 |
| Pull requests | `PullRequestTests` | 6 |
| Search | `SearchTests` | 7 |
| Gists | `GistTests` | 6 |
| Cross-cutting contract checks | `ContractTests` | 6 |
| **Total** | | **101** |

The full list of user stories and test cases is in the [test plan](docs/GitHub-API-Test-Plan.pdf).

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- A GitHub personal access token (classic) with the `repo`, `gist` and `delete_repo` scopes

## Configuration

The token is read from the `GITHUB_TOKEN` environment variable. It is never stored in the repository.

```bash
# Linux / macOS
export GITHUB_TOKEN=<your-token>

# Windows (PowerShell)
$env:GITHUB_TOKEN = "<your-token>"
```

## Running the tests

```bash
mvn clean test
```

Test reports are written to `target/surefire-reports`. Open `emailable-report.html` in a browser for a one-page summary.

A single test can also be started from the IDE with the run icon next to the test method.

## Test data

Most test classes create repositories in the authenticated account. The names start with `test-repo` or `test_repo`.

- **Issue, comment, label, content, branch and pull request tests** create one repository for the whole class (`@BeforeClass`) and delete it afterwards (`@AfterClass`). The two issue tests that need an empty repository (`I001`, `I012`) create their own and delete it in a `finally` block. The names contain a timestamp, so the tests can be repeated.
- **Gist tests** create their own gists and delete them again.
- **Repository tests** do not clean up after themselves. Only the delete tests (`R018`, `R019`) remove the repository they work on, so the other tests leave their repositories behind and they have to be deleted manually. Some of them use fixed repository names, so running them a second time fails with `422` until the repository from the previous run is deleted.

Several tests also read data of the GitHub account `gamzeozakinci` (for example the public repositories used by the pagination tests), so the user tests expect that account to have at least two public repositories.

## Observed API behavior

Things found while writing the tests, confirmed against the live API:

- **Repository names are sanitized.** A name with characters outside letters, digits, `-`, `_` and `.` is accepted with `201`, and a run of invalid characters is replaced by a single hyphen. `test_repo2***` is created as `test_repo2-`, and `test_repo7**** 12 212...... 12` is created as `test_repo7-12-212-12` (`R007`).
- **Validation errors have two messages.** On `422` the response has a top-level `message` and an `errors` array. The specific reason is in `errors[0].message`, for example `name already exists on this account` (`R005`).
- **Repository operations are not instant.** Renaming a repository a few seconds after creating it is rejected, sometimes with `422` ("A conflicting repository operation is still in progress") and sometimes with an empty `500`. `R015` therefore retries the rename up to six times, two seconds apart, and still fails if it never returns `200`.
- **Issue lists can lag behind.** Right after creating or closing an issue, the list endpoint can still return an empty array (`state=open` or `state=closed`) although reading the single issue already shows the new state. `I009` to `I012` poll the list up to six times, two seconds apart.
- **Creating many repositories gets the client blocked.** About ten repositories in a row (one per test, each with a delete) led to `403` with the message "You have exceeded a secondary rate limit and have been temporarily blocked from content creation", while the primary limit was far from used up and no `Retry-After` header was sent. The issue tests therefore share one repository per class.
- **Search by language is not exact.** A search with `language:java` can return repositories whose own `language` field is `null`. `S002` therefore accepts `Java` or `null` for every result and requires at least one `Java` result.
- **Search without `q` is rejected.** `GET /search/repositories` without a `q` parameter returns `422` with `errors[0].field = q` and `errors[0].code = missing` (`S004`). A search without matches is not an error, it returns `200` with `total_count: 0` and an empty `items` array (`S005`).
- **Error responses have one shape.** Unknown resources (`404`), missing authentication (`401`) and invalid requests (`422`) all return JSON with a `message` and a `documentation_url`, also for unknown routes (`X003`, `X006`). The `Content-Type` is `application/json; charset=utf-8` for successful and failed responses (`X001`).
- **Unsupported API versions are rejected.** A request with `X-GitHub-Api-Version: 1999-01-01` returns `400 Bad Request`. The body lists the supported versions, which were `2026-03-10` and `2022-11-28` when the test was written (`X005`).
- **Deprecation headers.** Responses for API version `2022-11-28` carry `Deprecation` and `Sunset` headers (sunset date 10 March 2028).

## Rate limits

The GitHub REST API limits how many requests a client can send. The suite stays far below the primary limits, but the secondary limit for content creation was reached once when every test created its own repository (see [Observed API behavior](#observed-api-behavior)), so the limits matter when you run the suite repeatedly. Source: [Rate limits for the REST API](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api).

### Primary limits (per hour)

| Request type | Limit |
|--------------|-------|
| Unauthenticated | 60 requests per hour, per IP address |
| Authenticated with a personal access token | 5,000 requests per hour |

Tests that use `RequestSpecs.publicSpec()` send unauthenticated requests and count against the 60 requests per hour of your IP address. Tests that use `RequestSpecs.authSpec()` use the 5,000 requests of the token. Running the whole suite several times within an hour can use up the 60 unauthenticated requests. The tests that use `publicSpec()` then fail with `403` ("rate limit exceeded") until the hour is over, `x-ratelimit-reset` tells when that is.

### Response headers

Every response reports the current state of the limit:

| Header | Meaning |
|--------|---------|
| `x-ratelimit-limit` | Maximum number of requests allowed per hour |
| `x-ratelimit-remaining` | Requests left in the current window |
| `x-ratelimit-used` | Requests made in the current window |
| `x-ratelimit-reset` | When the window resets, in UTC epoch seconds |
| `x-ratelimit-resource` | The rate limit resource the request counted against |

`RateLimitTests` verifies that these headers are returned. GitHub notes that the values can differ slightly between responses, so tests compare them in a relative way (for example "used increases between two calls") instead of checking an exact number.

`GET /rate_limit` returns the current status of all limits. It does not count against the primary limit.

### Secondary limits

GitHub also applies secondary limits to protect the service. Documented examples:

- No more than 100 concurrent requests.
- No more than 900 points per minute for a REST endpoint (most `GET` requests cost 1 point, most `POST`, `PATCH`, `PUT` and `DELETE` requests cost 5 points).
- No more than 80 content-creating requests per minute and 500 per hour.

Creating and deleting repositories, issues and comments are write requests, so the suite runs its tests sequentially and does not use parallel execution.

### Search limits

According to the [Search API documentation](https://docs.github.com/en/rest/search/search), most search endpoints allow 30 requests per minute for authenticated requests. Unauthenticated search requests are limited to 10 per minute.

### When a limit is exceeded

GitHub responds with `403` or `429`:

- If `x-ratelimit-remaining` is `0`, wait until the time in `x-ratelimit-reset`.
- If a `retry-after` header is present, wait that many seconds.
- Do not keep sending requests while rate limited, GitHub may block the client.
