# MyKakebo: project rules

## About this file

- This file is ALWAYS written in English, even when the conversation is in Spanish.
- One topic per section. Add new rules to the section they belong to, or create a new section; never mix topics.

## Tech stack

- Language: Java 21 (`java.version` is 21 in `backend/pom.xml`).
- Framework: Spring Boot 4.1.1 (Spring Web, Spring Data JPA / Hibernate).
- Build tool: Maven, through the wrapper (`./mvnw`) in `backend/`. NEVER use Gradle.
- Database: PostgreSQL 16 (`postgres:16-alpine` in `docker-compose.yml`), database name `kakebo`.
- Migrations: Flyway (`backend/src/main/resources/db/migration`).
- CI: GitHub Actions on `ubuntu-24.04` (`.github/workflows/ci.yml`), plus CodeQL and Dependabot.
- Deployment: Docker (multi-stage `backend/Dockerfile`) and Kubernetes manifests in `k8s/`.
- Base package: `com.aliciagar2.mykakebo`.
- `frontend/` is empty (React + TypeScript planned, not started).

## Commands

Run everything from the repository root unless a `cd` is shown.

| Goal | Command |
|---|---|
| Create local env file (first time only) | `cp .env.example .env`, then set `DB_PASSWORD` |
| Start local Postgres | `docker compose up -d postgres` |
| Stop local Postgres (keeps data) | `docker compose down` |
| Install all modules to the local repo (first time, or after changing a module another one depends on) | `cd backend && ./mvnw install -DskipTests` |
| Run the API | `cd backend/mykakebo-bootstrap && ../mvnw spring-boot:run` |
| Run all tests | `cd backend && ./mvnw test` |
| Run one test class | `cd backend && ./mvnw test -pl mykakebo-adapter-web -am -Dtest=ExpenseControllerTest -Dsurefire.failIfNoSpecifiedTests=false` |
| Run one test method | `cd backend && ./mvnw test -pl mykakebo-adapter-web -am -Dtest=ExpenseControllerTest#listReturnsExpensesInMonth -Dsurefire.failIfNoSpecifiedTests=false` |
| Full build, as CI does | `cd backend && ./mvnw verify` |

- `backend/` is a 5-module Maven reactor (`mykakebo-domain`, `mykakebo-adapter-persistence-jpa`, `mykakebo-adapter-persistence-memory`, `mykakebo-adapter-web`, `mykakebo-bootstrap`). Some goals are not normal lifecycle phases, such as `spring-boot:run`. Running one of these with `-pl <module> -am` from the reactor root runs it against the root aggregator instead. This is a genuine Maven quirk, not a typo. To run it correctly, `cd` into the module directory and invoke `../mvnw` directly, as in "Run the API" above.
- For "Run one test class"/"Run one test method", replace `mykakebo-adapter-web` with the module that actually contains the class. Find it with `grep -rl <ClassName> backend/*/src`.
- `-Dsurefire.failIfNoSpecifiedTests=false` is required with `-am`. It also runs Surefire on every upstream module `-am` builds, and those do not contain the class you named.

- NEVER run `docker compose down -v`: it deletes the Postgres data volume.
- The app reads `DB_URL` (default `jdbc:postgresql://localhost:5432/kakebo`), `DB_USERNAME` and `DB_PASSWORD`.

## Database and Flyway migrations

- Schema changes happen ONLY through Flyway migrations. Hibernate runs with `ddl-auto: validate` and MUST NOT create or alter tables.
- NEVER edit, rename, reorder or delete a migration that already exists (`V1`, `V2`, `V3`, ...). To change the schema, add a NEW migration with the next version number.
- File name format: `V<number>__<snake_case_description>.sql` (double underscore), for example `V2__rename_reserved_columns.sql`.
- Any change to a `@Entity` that affects a column or table MUST come with its migration in the same change.
- Every migration MUST apply cleanly on top of the previous ones, in order, on an empty database.

## Do not touch without asking

Ask the user first, and wait for an explicit yes, before you:

- read, print, edit or delete `.env` (see "Secrets and credentials");
- change dependency or plugin versions in `backend/pom.xml`, or add or remove a dependency;
- edit `.github/` (workflows, `CODEOWNERS`, `dependabot.yml`), `LICENSE`, `.pre-commit-config.yaml` or `.gitignore`;
- edit `.idea/` or `.vscode/` (IDE settings);
- delete or rename files, or move classes between packages;
- do a refactor that touches more than the files the task is about;
- change a test so that it passes (a failing test is information: explain it first);
- run destructive commands: `docker compose down -v`, `rm -rf`, dropping or truncating a database.

## Secrets and credentials

- NEVER commit passwords, tokens, API keys, private keys or connection strings with credentials.
- `.env` is git-ignored and holds the real values. Only `.env.example` is committed, and it contains placeholders only.
- NEVER print, log, quote or copy the value of a secret in a reply, a file, a commit message or a command. When listing `.env`, show variable names only.
- Kubernetes `Secret` manifests in `k8s/` MUST NOT contain real values.
- The pre-commit hook `gitleaks` scans staged changes for secrets. NEVER bypass it (no `--no-verify`).
- If a secret has been committed or exposed: stop, tell the user immediately, and tell them to rotate it. Removing it from the latest commit is NOT enough, because it stays in the git history.

## Code formatting

- Imports: sorted alphabetically by fully qualified name, one block per file. A rename or move that changes a class's import line MUST re-sort that line into place, not just swap the name in place.
- Members: test methods, and helper members that exist only to support tests (e.g. endpoints on an inner `@RestController` used by exception-handler tests), are ordered alphabetically by name within their class. A new test goes where its name sorts, not at the end of the file.
- Files end with exactly one trailing newline.

## Git workflow and commits

### Always ask before committing

- NEVER run `git commit` without asking the user first. This rule has no exceptions.
- Before each commit, reformat every file you touched so it matches "Code formatting" above (import order, member order, trailing newline) — do this even if the change being committed is unrelated to formatting.
- Before each commit:
  1. show `git status` and `git diff --stat`;
  2. propose the exact commit message;
  3. wait for an explicit yes.
- An approval covers ONE commit only. Ask again for the next one.
- Also ask before `git push`, `git commit --amend`, `git rebase`, `git reset`, `git stash drop`, deleting a branch, or any force operation.
- NEVER use `--no-verify`. If a pre-commit hook fails (gitleaks, checkstyle, whitespace, large files), fix the cause.

### Commit message format

- Conventional Commits: `<type>(<optional scope>): <summary>`
- Types used in this repository: `feat`, `fix`, `chore`, `test`, `ci`. Also allowed: `docs`, `refactor`, `build`.
- Scope is optional and short, for example `deps` or `domain`.
- Summary: imperative mood, starts in lowercase, no trailing period, 72 characters or fewer.
- Optional body, separated by a blank line, explains WHY, not what.
- One logical change per commit.

Examples (from this repository's history):

- `feat: create ExpenseController with get/post/delete/put`
- `fix(domain): add explicit YearMonth converter to match VARCHAR schema`
- `chore(deps): bump org.apache.maven:apache-maven in /backend`

### Branches

- Work on a short-lived topic branch with a descriptive name. NEVER commit directly to `main`.
- Changes reach `main` through a pull request.

### Pull request descriptions

- End every PR description with `🤖 Description generated with [Claude Code](https://claude.com/claude-code)`, not the generic "Generated with" line.

## Naming: test methods

- camelCase only. No underscores, no `test` prefix, no `@DisplayName`.
- Structure: `<actionOrSubject><Outcome>[When<Condition>]`
  - `listReturnsExpensesInMonth`
  - `updateReturns404WhenExpenseDoesNotExist`
  - `createReturns409WhenBudgetAlreadyExistsForMonth`
- Verbs for the outcome:
  - `Returns<status>` for HTTP responses (`createReturns409...`)
  - `Rejects` for validation failures (`createRejectsNegativeSavingsGoal`)
  - `Persists` for writes (`createPersistsExpenseAndReturns201`)
  - `Accepts` for valid boundary values (`createAcceptsSmallestPositiveAmount`)
- One behavior per test.
- Exception handler tests start from the exception: `expenseNotFoundReturns404WithErrorBody`.

## Naming: classes

| Kind | Rule | Examples |
|---|---|---|
| Controller | `<Resource>Controller`, in `web/` | `ExpenseController` |
| Service | `<Name>Service`, in `service/` | `SummaryService` |
| Repository | `<Entity without "Entity">Repository`, in `repository/` | `ExpenseRepository` |
| JPA entity | `<Name>Entity`, annotated `@Entity` | `ExpenseEntity` |
| Domain record | plain noun, no suffix | `Expense`, `MonthlyBudget` |
| Request DTO | `<Resource>Request`, record, in `dto/` | `ExpenseRequest` |
| Response DTO | `<Resource>Response`, record, in `dto/` | `ExpenseResponse` |
| Custom exception | `<Subject><Reason>Exception`, in `domain/exceptions/` | `ExpenseNotFoundException`, `MonthlyBudgetAlreadyExistsException` |
| Exception handler | `GlobalExceptionHandler`, in `web/` | |
| Error body | `ErrorResponse`, `FieldViolation` (records, in `dto/`) | |
| JPA converter | `<Type>Converter` | `YearMonthConverter` |

## Naming: test classes

- `<ClassUnderTest>Test`, same package as the class under test, under `src/test/java`.
- Exception: `MykakeboApplicationTests` (Spring Initializr default).
