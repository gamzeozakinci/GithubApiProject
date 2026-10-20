# GitHub REST API Tests

API test automation suite for the [GitHub REST API](https://docs.github.com/en/rest), built with REST Assured and TestNG.

> **Status:** work in progress. 34 test cases are implemented (authentication, rate limit, users and repositories). The remaining areas are planned, see [Test coverage](#test-coverage).

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
├── base/          Common setup and teardown (BaseTest)
├── config/        Configuration and token handling (ConfigManager)
├── constants/     Endpoint paths and TestNG group names
├── specs/         Reusable request and response specifications
├── utils/         Test data factory, schema paths
├── api/           API clients, one per resource (planned)
├── models/
│   ├── request/   Request body models (planned)
│   └── response/  Response models (planned)
└── tests/         Test classes, one per feature area

src/test/resources/
├── testng.xml     Suite definition
├── config.properties
└── schemas/       JSON schemas for response validation
```

The tests currently build their requests directly with the shared `RequestSpecs` and `ResponseSpecs` and the paths in `Endpoints`. The `api`, `models`, `config` and `base` packages are in place as placeholders for the next step, which moves repeated requests (for example creating and deleting a repository) into API client classes.

Every test method carries its test case ID in the name and in its `description`, for example `R009_getRepoByOwnerName`. Tests are tagged with the TestNG groups `smoke`, `regression`, `negative` and `contract` (see `TestGroups`).

## Test coverage

| Area | Test class | Implemented |
|------|-----------|-------------|
| Authentication | `AuthenticationTests` | 5 of 5 |
| Rate limit | `RateLimitTests` | 3 of 3 |
| Users | `UserTests` | 7 of 7 |
| Repositories | `RepositoryTests` | 19 of 19 |
| Issues | `IssueTests` | planned |
| Issue comments | `CommentTests` | planned |
| Labels | `LabelTests` | planned |
| Repository contents | `ContentTests` | planned |
| Branches and commits | `BranchTests` | planned |
| Pull requests | `PullRequestTests` | planned |
| Search | `SearchTests` | planned |
| Gists | `GistTests` | planned |
| Cross-cutting contract checks | `ContractTests` | planned |

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

Non-secret settings (base URI, API version) live in `src/test/resources/config.properties`.

## Running the tests

```bash
mvn clean test
```

Test reports are written to `target/surefire-reports`. Open `emailable-report.html` in a browser for a one-page summary.

A single test can also be started from the IDE with the run icon next to the test method.

## Test data

Most repository tests create a repository in the authenticated account. The names start with `test-repo` or `test_repo`. Only the delete tests (`R018`, `R019`) remove the repository they create, so the other tests leave their repositories behind and they have to be deleted manually until the clean-up is automated. Some tests use fixed repository names, so running them a second time fails with `422` until the repository from the previous run is deleted.

Several tests also read data of the GitHub account `gamzeozakinci` (for example the public repositories used by the pagination tests), so the user tests expect that account to have at least two public repositories.

## Observed API behavior

Things found while writing the tests, confirmed against the live API:

- **Repository names are sanitized.** A name with characters outside letters, digits, `-`, `_` and `.` is accepted with `201`, and a run of invalid characters is replaced by a single hyphen. `test_repo2***` is created as `test_repo2-`, and `test_repo7**** 12 212...... 12` is created as `test_repo7-12-212-12` (`R007`).
- **Validation errors have two messages.** On `422` the response has a top-level `message` and an `errors` array. The specific reason is in `errors[0].message`, for example `name already exists on this account` (`R005`).
- **Repository operations are not instant.** Renaming a repository a few seconds after creating it is rejected, sometimes with `422` ("A conflicting repository operation is still in progress") and sometimes with an empty `500`. `R015` therefore retries the rename up to six times, two seconds apart, and still fails if it never returns `200`.
- **Deprecation headers.** Responses for API version `2022-11-28` carry `Deprecation` and `Sunset` headers (sunset date 10 March 2028).

## Rate limits

The GitHub REST API limits how many requests a client can send. The suite stays well below these limits, but they matter when you run it repeatedly. Source: [Rate limits for the REST API](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api).

### Primary limits (per hour)

| Request type | Limit |
|--------------|-------|
| Unauthenticated | 60 requests per hour, per IP address |
| Authenticated with a personal access token | 5,000 requests per hour |

Tests that use `RequestSpecs.publicSpec()` send unauthenticated requests and count against the 60 requests per hour of your IP address. Tests that use `RequestSpecs.authSpec()` use the 5,000 requests of the token.

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
