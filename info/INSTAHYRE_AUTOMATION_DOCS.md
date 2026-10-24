# Instahyre Automation API

Browser Pilot provides REST APIs to automate selected Instahyre workflows: logging in, applying to jobs, and completing the candidate questionnaire.

> **Important:** Use this automation responsibly and in accordance with Instahyre's terms, applicable laws, and the website's usage policies. Review applications and submitted answers carefully. Keep credentials and personal files private.

## Prerequisites

- Java 25 and Maven 3.9 required by this project.
- The Browser Pilot application configured and running locally.
- An Instahyre account.
- A resume file available on your machine.
- A questionnaire answers JSON file.
- `curl` or another HTTP client for calling the APIs.

The examples below assume the application is running at `http://localhost:8080`.

## Configure environment variables

The application reads account credentials, the resume location, and the questionnaire configuration path from environment variables.

### macOS (zsh)

In the terminal where you intend to run Browser Pilot, export the variables:

```bash
export INSTAHYRE_USERNAME="your-instahyre-username"
export INSTAHYRE_PASSWORD="your-instahyre-password"
export INSTAHYRE_RESUME_PATH="/absolute/path/to/Resume.pdf"
export QUESTIONNAIRE_ANSWERS_FILE="/absolute/path/to/questionnaire-answers.json"
```

Replace the example values with your own. Use absolute paths for the resume and JSON file to avoid ambiguity about the application's working directory.

Verify that the variables are set without printing your password:

```bash
printf 'Username configured: %s\n' "${INSTAHYRE_USERNAME:+yes}"
printf 'Resume path: %s\n' "$INSTAHYRE_RESUME_PATH"
printf 'Questionnaire file: %s\n' "$QUESTIONNAIRE_ANSWERS_FILE"
```

Run the application from the **same terminal session** so it inherits these variables. If you launch the application from an IDE, configure the same environment variables in the IDE's run configuration.

### Persist variables in zsh

To make the variables available in future terminal sessions, add exports to `~/.zshrc`:

```bash
nano ~/.zshrc
```

Add the following lines, using your real values:

```bash
export INSTAHYRE_USERNAME="your-instahyre-username"
export INSTAHYRE_PASSWORD="your-instahyre-password"
export INSTAHYRE_RESUME_PATH="/absolute/path/to/Resume.pdf"
export QUESTIONNAIRE_ANSWERS_FILE="/absolute/path/to/questionnaire-answers.json"
```

Reload the shell configuration:

```bash
source ~/.zshrc
```

**Security note:** Saving a password in `~/.zshrc` stores it in plaintext. Prefer a secrets manager or another secure local mechanism where possible. Never commit credentials, real resume files, or personal questionnaire answers to Git.

## Configure `application.yml`

The relevant configuration should map the environment variables to the `InstahyreProperties` record:

```yaml
website:
  instahyre:
    username: ${INSTAHYRE_USERNAME}
    password: ${INSTAHYRE_PASSWORD}
    resume-path: ${INSTAHYRE_RESUME_PATH}
    questionnaire:
      answers-file: ${QUESTIONNAIRE_ANSWERS_FILE}
```

The property names correspond to:

- `website.instahyre.username`
- `website.instahyre.password`
- `website.instahyre.resume-path`
- `website.instahyre.questionnaire.answers-file`

Ensure `InstahyreProperties` is registered with Spring Boot using `@ConfigurationPropertiesScan` or `@EnableConfigurationProperties(InstahyreProperties.class)`.

## Questionnaire answers file

Create a JSON file at the path specified by `QUESTIONNAIRE_ANSWERS_FILE`.

Example:

```json
{
  "answers": [
    {
      "rule": "current CTC OR current salary",
      "answer": "12345"
    },
    {
      "rule": "Expected CTC",
      "answer": "123456"
    }
  ]
}
```

Each entry contains:

- `rule`: Text or a supported matching expression used to identify a question.
- `answer`: The answer to enter when the rule matches.

The current answer provider supports matching expressions such as `OR`, `AND`, `ALL`, `ONLY`, and `NOT`. Use rules that distinguish the intended question from similar questions. Keep the JSON valid: use double quotes around property names and string values, and do not add trailing commas.

Do not use the sample compensation values as real answers unless they are correct for your situation. Review all answers before submitting a questionnaire.

## API endpoints

### 1. Log in to Instahyre

**Endpoint**

```http
POST http://localhost:8080/api/instahyre/login
```

**Example**

```bash
curl -i -X POST http://localhost:8080/api/instahyre/login
```

This endpoint triggers the application's Instahyre login workflow. The application uses the configured username and password.

### 2. Apply to jobs

**Endpoint**

```http
POST http://localhost:8080/api/instahyre/apply-job
Content-Type: application/json
```

**Request body**

```json
{
  "noOfJobs": 2,
  "keywords": ["Java"],
  "descriptionSkills": ["Java"]
}
```

**Example**

```bash
curl -i \
  -X POST "http://localhost:8080/api/instahyre/apply-job" \
  -H "Content-Type: application/json" \
  -d '{
    "noOfJobs": 2,
    "keywords": ["Java"],
    "descriptionSkills": ["Java"]
  }'
```

Request fields:

| Field | Meaning |
| --- | --- |
| `noOfJobs` | Maximum number of jobs requested for the workflow to process. |
| `keywords` | Keywords used by the job-search workflow. |
| `descriptionSkills` | Skills used by the workflow when evaluating job descriptions. |

The exact search, filtering, and application behavior depends on the implementation in the running version of Browser Pilot. Check the application logs and response for the actual outcome. Review each job and application details where possible; do not assume that an HTTP success response means every application was submitted successfully.

### 3. Complete the questionnaire

**Endpoint**

```http
POST http://localhost:8080/api/instahyre/questionnaire/complete
```

**Example**

```bash
curl -i \
  -X POST "http://localhost:8080/api/instahyre/questionnaire/complete"
```

This endpoint runs the questionnaire workflow using the configured answers file and resume path. The questionnaire page must be open or otherwise discoverable by the application's page resolver, according to the project's current browser configuration.

The workflow should fill configured answers, upload the resume when the matching question is encountered, and submit only when the required questions have been handled according to the application's validation logic. Confirm the final submission status in the application logs and on the website.

## Suggested workflow

1. Configure the four environment variables.
2. Start Browser Pilot from the same terminal, or provide the variables through your IDE's run configuration.
3. Call the login endpoint.
4. Confirm that login completed and that the browser is in the expected state.
5. Call the apply-job endpoint with a small `noOfJobs` value first.
6. Review the job applications and logs.
7. If a questionnaire is presented, ensure the JSON answers file and resume path are correct, then call the questionnaire completion endpoint.
8. Verify the success message and final state in the browser.

Depending on how the application manages browser tabs and sessions, these operations may depend on a previously opened page or authenticated browser context. If an endpoint reports that a page is missing or closed, check the browser mode, active tabs, and application logs.

## Troubleshooting

### Missing environment variable

Spring may fail to start if a required placeholder cannot be resolved. Check that all variables are exported in the same environment that launches the application:

```bash
printf '%s\n' \
  "USERNAME configured: ${INSTAHYRE_USERNAME:+yes}" \
  "PASSWORD configured: ${INSTAHYRE_PASSWORD:+yes}" \
  "RESUME configured: ${INSTAHYRE_RESUME_PATH:+yes}" \
  "QUESTIONNAIRE configured: ${QUESTIONNAIRE_ANSWERS_FILE:+yes}"
```

This check does not print the password itself.

### Questionnaire JSON file not found

- Confirm that `QUESTIONNAIRE_ANSWERS_FILE` points to the correct file.
- Prefer an absolute path.
- Confirm that the file is readable and contains a top-level `answers` array.
- Validate the JSON syntax.

### Resume file not found

- Confirm that `INSTAHYRE_RESUME_PATH` points to an existing PDF file.
- Confirm that the application process has permission to read it.

### No answer matches a question

- Compare the visible question text with the configured `rule`.
- Use a distinctive phrase instead of a very broad keyword.
- Check for overlapping rules that could match the same question.
- Review logs before allowing the workflow to submit.

### Browser or session problems

- Confirm the configured browser mode and that the browser/context is available.
- Ensure the expected Instahyre tab is open.
- Check for a closed tab, expired login session, or a page that has navigated to a different URL.
- Inspect the application logs for Playwright errors.

## Keep personal data out of Git

Do not commit real credentials, resume files, or personal answer data. Add suitable entries to `.gitignore`, for example:

```gitignore
# Local secrets and personal data
.env
.env.*
config/questionnaire-answers.json
*.pdf
```


## Notes

- The examples assume the API routes and HTTP methods listed above are configured in the application.
- This documentation describes how to call the endpoints; actual behavior and success criteria are determined by the current implementation.
- Treat job applications and questionnaire submissions as consequential actions. Validate the content before submitting and follow the website's terms and applicable policies.
