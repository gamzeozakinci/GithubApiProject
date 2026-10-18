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
| TestNG reports | Test reporting (Surefire HTML and XML reports) |

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

Test reports are written to `target/surefire-reports`.

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
