# Browser Pilot
 
> A platform of Spring Boot services that automate browser-based games through simple REST APIs. **LinkedIn Games is the first supported service**; more services will follow the same layout.
 
![Java](https://img.shields.io/badge/Java-25-orange)
![Maven](https://img.shields.io/badge/Apache%20Maven-3.9.16-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)
![Platform](https://img.shields.io/badge/platform-macOS-lightgrey)
 
Release **2.0.1** splits the work into three services. **Clients should talk to the Game Orchestrator only.** One call to the orchestrator computes the solution, caches it for the day, and applies it to the game in your browser.
 
```bash
curl -X POST http://localhost:8080/api/games/QUEENS/solve
```
 
The Pinpoint and LinkedIn Games services are documented below for troubleshooting, testing and advanced use, but you normally never call them yourself.
 
---
 
## Table of Contents
 
- [Services](#services)
- [Supported Games](#supported-games)
- [Quick Start](#quick-start)
- [Orchestrator API (use this)](#orchestrator-api-use-this)
- [Pinpoint Service API](#pinpoint-service-api)
- [LinkedIn Games Service API](#linkedin-games-service-api)
- [Browser Management API](#browser-management-api)
- [Game Data Shapes](#game-data-shapes)
- [Architecture](#architecture)
- [Configuration](#configuration)
- [Logging](#logging)
- [Development](#development)
- [Changes in 2.0.1](#changes-in-201)
- [Troubleshooting](#troubleshooting)
- [Security](#security)
---
 
## Services
 
Each service is a separate Spring Boot JAR with its own YAML config and port. They communicate over HTTP.
 
| Service | JAR | Port | Who calls it | Role |
|---------|-----|------|--------------|------|
| **Game Orchestrator** | `game-orchestrator-2.0.1.jar` | `8080` | **Clients** | Single entry point. Checks the daily cache, gets the solution from Pinpoint when needed, sends it to LinkedIn Games. |
| **Pinpoint Service** | `pinpoint-service-2.0.1.jar` | `8081` | Orchestrator | Calculates the solution for a game. |
| **LinkedIn Games Service** | `linkedin-service-2.0.1.jar` | `8082` | Orchestrator | Applies a solution to the game in an attached browser. |
| Future services | n/a | n/a | n/a | Will use the same layout: own JAR, config, port and attached browser. |
 
```
Client ─► Game Orchestrator :8080 ──► Pinpoint Service :8081       (compute solution)
              │      ▲                        │
              │      └── daily cache ◄────────┘
              │          data/pinpoint-solutions
              └────────► LinkedIn Games Service :8082 ──► Chrome (CDP :9222)
```
 
## Supported Games
 
The orchestrator accepts these `{game}` values. Downstream paths use kebab-case.
 
| `{game}` (orchestrator) | Pinpoint path | LinkedIn Games path |
|-------------------------|---------------|---------------------|
| `MINI_SUDOKU` | `/api/pinpointanswer/mini-sudoku/solve` | `/api/linkedin-games/mini-sudoku/solve` |
| `PATCHES` | `/api/pinpointanswer/patches/solve` | `/api/linkedin-games/patches/solve` |
| `QUEENS` | `/api/pinpointanswer/queens/solve` | `/api/linkedin-games/queens/solve` |
| `TANGO` | `/api/pinpointanswer/tango/solve` | `/api/linkedin-games/tango/solve` |
| `ZIP` | `/api/pinpointanswer/zip/solve` | `/api/linkedin-games/zip/solve` |
| `WEND` | `/api/pinpointanswer/wend/solve` | `/api/linkedin-games/wend/solve` |
| `PINPOINT` | `/api/pinpointanswer/pinpoint/solve` | `/api/linkedin-games/pinpoint/solve` |
| `CROSSCLIMB` | not in the 2.0.1 OpenAPI | not in the 2.0.1 OpenAPI |
 
> `CROSSCLIMB` is accepted by the orchestrator, but the published Pinpoint and LinkedIn Games OpenAPI documents contain no matching endpoint. Treat it as not yet usable until the service Swagger UIs list it.
 
---
 
## Quick Start
 
### Prerequisites
 
- **JDK 25 or newer**
- macOS (keyboard automation uses CoreGraphics through JNA)
- A Chromium-based browser with remote debugging enabled, signed in to LinkedIn
- Free ports `8080`, `8081`, `8082` and `9222`, or configured alternatives
> macOS typically requires **Accessibility** permission for the terminal or IDE that launches the services before synthetic key presses are accepted.
 
### 1. Start the browser
 
The services attach to an already-running browser; they do not launch one in `ATTACHED` mode. Use a dedicated profile:
 
```bash
open -na "Google Chrome" --args \
  --remote-debugging-port=9222 \
  --user-data-dir="$HOME/chrome-cdp-profile"
```
 
1. Sign in to LinkedIn in that profile.
2. Open the game you want to solve.
3. Keep Chrome running while using the services.
If Chrome is already running with the same profile, it may ignore the debugging flags. Quit that profile's Chrome fully and relaunch with the command above.
 
### 2. Start the services
 
Release layout:
 
```text
release-v2.0.1/
├── pinpoint-service-2.0.1.jar
├── linkedin-service-2.0.1.jar
├── game-orchestrator-2.0.1.jar
├── config/
│   ├── pinpoint-service.yaml
│   ├── linkedin-service.yaml
│   ├── game-orchestrator.yaml
│   └── services.env              (optional overrides)
└── scripts/
    ├── services.sh               (lifecycle manager)
    ├── start.sh
    └── stop.sh
```
 
```bash
chmod +x scripts/*.sh
./scripts/services.sh start        # Pinpoint → LinkedIn Games → Orchestrator
./scripts/services.sh status
./scripts/services.sh logs orchestrator
./scripts/services.sh stop         # reverse order
```
 
`start` checks Java, JARs, configs and ports, waits for each service to become ready, and rolls back anything it started if a service fails. You can target specific services, e.g. `./scripts/services.sh restart linkedin`.
 
| Resource | URL |
|----------|-----|
| Orchestrator Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Pinpoint Swagger UI | http://localhost:8081/swagger-ui/index.html |
| LinkedIn Games Swagger UI | http://localhost:8082/swagger-ui/index.html |
| Orchestrator health | http://localhost:8080/actuator/health |
 
### 3. Solve a game
 
With the game open in Chrome:
 
```bash
curl -X POST http://localhost:8080/api/games/MINI_SUDOKU/solve
```
 
---
 
## Orchestrator API (use this)
 
Base URL: `http://localhost:8080`
 
### `POST /api/games/{game}/solve`
 
Solves the named game end to end.
 
| Part | Details |
|------|---------|
| Path parameter | `game`, one of `CROSSCLIMB`, `PATCHES`, `ZIP`, `MINI_SUDOKU`, `WEND`, `TANGO`, `QUEENS`, `PINPOINT` |
| Request body | None |
| Success response | `200 OK`, `application/json` |
 
**What it does**
 
1. Checks the cache for the current cache day.
2. Cache hit: loads the stored solution and skips Pinpoint.
3. Cache miss: calls the matching Pinpoint endpoint and stores the result.
4. Sends the solution to the matching LinkedIn Games endpoint.
5. Returns the result to you.
You do not need to convert between the Pinpoint response and the LinkedIn Games request; the orchestrator does that.
 
The OpenAPI document models the response as a generic `JsonNode` (its many boolean properties are a documentation artifact of that type). The actual JSON content depends on the game and the orchestrator's result, so do not rely on a fixed schema. Check a live response for the fields you need.
 
Examples:
 
```bash
curl -X POST http://localhost:8080/api/games/ZIP/solve
curl -X POST http://localhost:8080/api/games/TANGO/solve
curl -X POST http://localhost:8080/api/games/PATCHES/solve
```
 
### Health
 
`GET /actuator/health` (and `/actuator/info`) are exposed by default.
 
---
 
## Pinpoint Service API
 
Base URL: `http://localhost:8081`. Used by the orchestrator; call it directly to inspect or debug solutions.
 
Every solve endpoint is a `POST` with **no request body**. The `200` response is a JSON object with a single `response` property.
 
| Endpoint | `response` type |
|----------|-----------------|
| `POST /api/pinpointanswer/mini-sudoku/solve` | array of [Cell](#game-data-shapes) |
| `POST /api/pinpointanswer/patches/solve` | array of [PatchResult](#game-data-shapes) |
| `POST /api/pinpointanswer/queens/solve` | array of [Coordinates](#game-data-shapes) |
| `POST /api/pinpointanswer/tango/solve` | array of [TangoResult](#game-data-shapes) |
| `POST /api/pinpointanswer/zip/solve` | array of strings |
| `POST /api/pinpointanswer/wend/solve` | array of [WendResult](#game-data-shapes) |
| `POST /api/pinpointanswer/pinpoint/solve` | string |
 
Pinpoint also exposes [browser management](#browser-management-api) endpoints and `POST /api/page/capture`.
 
Example:
 
```bash
curl -X POST http://localhost:8081/api/pinpointanswer/queens/solve
```
 
```json
{
  "response": [
    { "row": 0, "column": 5 },
    { "row": 1, "column": 0 }
  ]
}
```
 
*(Truncated for readability; a real response lists one entry per queen.)*
 
---
 
## LinkedIn Games Service API
 
Base URL: `http://localhost:8082`. Used by the orchestrator to execute a solution in the browser.
 
Every solve endpoint is a `POST` with a JSON body containing a single **`request`** property. It returns `200 OK` with no body.
 
| Endpoint | `request` type |
|----------|----------------|
| `POST /api/linkedin-games/mini-sudoku/solve` | array of [Cell](#game-data-shapes) |
| `POST /api/linkedin-games/patches/solve` | array of [PatchResult](#game-data-shapes) |
| `POST /api/linkedin-games/queens/solve` | array of [Coordinates](#game-data-shapes) |
| `POST /api/linkedin-games/tango/solve` | array of [TangoResult](#game-data-shapes) |
| `POST /api/linkedin-games/zip/solve` | array of strings |
| `POST /api/linkedin-games/wend/solve` | array of [WendResult](#game-data-shapes) |
| `POST /api/linkedin-games/pinpoint/solve` | string |
 
**The shapes match Pinpoint exactly.** A Pinpoint `response` is a valid LinkedIn Games `request`; only the property name changes.
 
Example (Queens, game open in the browser):
 
```bash
curl -X POST http://localhost:8082/api/linkedin-games/queens/solve \
  -H "Content-Type: application/json" \
  -d '{
        "request": [
          { "row": 0, "column": 5 },
          { "row": 1, "column": 0 },
          { "row": 2, "column": 7 },
          { "row": 3, "column": 1 },
          { "row": 4, "column": 4 },
          { "row": 5, "column": 2 },
          { "row": 6, "column": 6 },
          { "row": 7, "column": 3 }
        ]
      }'
```
 
### Doing the orchestrator's job by hand
 
Useful for debugging a single stage (requires `jq`):
 
```bash
curl -s -X POST http://localhost:8081/api/pinpointanswer/queens/solve \
  | jq '{request: .response}' \
  | curl -X POST http://localhost:8082/api/linkedin-games/queens/solve \
      -H "Content-Type: application/json" -d @-
```
 
This bypasses the cache. Use `POST /api/games/QUEENS/solve` for normal operation.
 
---
 
## Browser Management API
 
The Pinpoint (`8081`) and LinkedIn Games (`8082`) services each expose the same Playwright management endpoints for the browser they are attached to. These are operational tools; clients of the orchestrator do not need them.
 
| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/playwright/status` | Is the Playwright connection to the browser established? |
| `POST` | `/api/playwright/reconnect` | Try to restore the Playwright connection |
| `POST` | `/api/playwright/restart` | Hard restart: close the browser, launch a fresh one, reattach, return the tab list |
| `GET` | `/api/playwright/tabs` | List managed tabs |
| `POST` | `/api/playwright/tabs` | Create a tab (`201`) |
| `POST` | `/api/playwright/tabs/reset` | Soft reset: close managed tabs, keep the browser, leave one blank tab |
| `POST` | `/api/playwright/tabs/{tabId}/activate` | Make a tab the active tab |
| `POST` | `/api/playwright/tabs/{tabId}/reload` | Reload a tab |
| `DELETE` | `/api/playwright/tabs/{tabId}` | Close a tab (`204`) |
| `POST` | `/api/playwright/navigate?url=...&tabId=...` | Navigate a tab; `tabId` defaults to the active tab |
| `GET` | `/api/playwright/url` | URL of the active tab |
| `GET` | `/api/playwright/title` | Title of the active tab |
| `POST` | `/api/page/capture` | Capture the current page; returns a string result |
 
Tabs are described by `TabInfo`:
 
```json
{ "id": "string", "url": "string", "title": "string", "closed": false, "active": true }
```
 
Common errors on the LinkedIn Games service: `404` for an unknown tab, `400` for an invalid navigation URL, `500` when the browser operation fails.
 
Example:
 
```bash
curl http://localhost:8082/api/playwright/status
curl "http://localhost:8082/api/playwright/tabs"
curl -X POST "http://localhost:8082/api/playwright/navigate?url=https://www.linkedin.com/games/"
```
 
---
 
## Game Data Shapes
 
All coordinates are objects with a zero-based `row` and `column`.
 
### Coordinates
 
```json
{ "row": 0, "column": 5 }
```
 
### Cell (Mini Sudoku)
 
```json
{ "value": 4, "coordinates": { "row": 2, "column": 3 } }
```
 
One entry per cell to fill, with the digit to type.
 
### TangoResult (Tango)
 
```json
{ "value": "S", "coordinates": { "row": 0, "column": 1 } }
```
 
`value` is a string identifying Sun or Moon for that cell. Check a live Pinpoint response for the exact values your build produces.
 
### PatchResult (Patches)
 
```json
{
  "patchSize": 3,
  "startCoordinates": { "row": 0, "column": 0 },
  "coordinates": [
    { "row": 0, "column": 0 },
    { "row": 0, "column": 1 },
    { "row": 1, "column": 1 }
  ],
  "directions": [["RIGHT", "DOWN"]]
}
```
 
| Field | Meaning |
|-------|---------|
| `patchSize` | Number of cells in the patch |
| `startCoordinates` | Where the patch starts |
| `coordinates` | Cells belonging to the patch |
| `directions` | Lists of moves, each `UP`, `DOWN`, `LEFT` or `RIGHT` |
 
### WendResult (Wend)
 
```json
{
  "word": "CAT",
  "startCoordinates": { "row": 1, "column": 0 },
  "coordinates": [
    { "row": 1, "column": 0 },
    { "row": 1, "column": 1 },
    { "row": 2, "column": 1 }
  ],
  "directions": ["RIGHT", "DOWN"]
}
```
 
### Zip and Pinpoint
 
- **Zip**: an array of strings, passed through from Pinpoint to LinkedIn Games unchanged.
- **Pinpoint**: a single string.
> The JSON above shows the structure from the OpenAPI documents with illustrative values. Real values come from the Pinpoint service; do not hand-craft them unless you are testing.
 
---
 
## Architecture
 
Three services, one entry point:
 
1. **Client** calls the orchestrator.
2. **Orchestrator** looks up the current cache day. On a miss it calls **Pinpoint**, which calculates the solution, and stores it. On a hit it reuses the stored one.
3. **Orchestrator** posts the solution to **LinkedIn Games**, which drives the attached browser (Playwright over CDP, plus keyboard automation on macOS) to enter it.
Design rules:
 
- Services communicate over HTTP only.
- Each service owns its config file, port, health endpoint and OpenAPI document.
- Pinpoint and LinkedIn Games share data shapes, so the orchestrator forwards solutions without transforming them.
- OS-specific keyboard control sits behind an abstraction, so supporting another OS means a new implementation, not new game logic.
### Cache
 
The orchestrator stores solutions in `data/pinpoint-solutions/`. A cache day starts at `GAME_CACHE_DAY_START` (default `00:35`) in `GAME_CACHE_ZONE` (default `Asia/Kolkata`). A request at 00:20 belongs to the day that began at 00:35 on the previous date; a request at 00:40 belongs to the day that began at 00:35 today.
 
Keep the directory across restarts to preserve entries. Do not assume cache files from earlier releases are compatible with 2.0.1. A cached solution may not apply if the game state changed, so if a game was reset or re-dealt, clear the relevant cache file.
 
---
 
## Configuration
 
Each service reads an external YAML file passed with `--spring.config.additional-location`; the scripts do this for you.
 
### Pinpoint: `config/pinpoint-service.yaml`
 
```yaml
spring:
  application:
    name: pinpoint-service
 
server:
  port: 8081
 
logging:
  file:
    name: ./logs/pinpoint-service.log
  level:
    root: INFO
    com.achhecode: INFO
  logback:
    rollingpolicy:
      file-name-pattern: ./logs/pinpoint-service.%d{yyyy-MM-dd}.%i.log.gz
      max-file-size: 50MB
      total-size-cap: 1GB
      max-history: 30
      clean-history-on-start: true
 
playwright:
  browser:
    mode: ATTACHED
    host: 127.0.0.1
    port: 9222
  capture:
    dir: ./captures
```
 
### LinkedIn Games: `config/linkedin-service.yaml`
 
Same structure as Pinpoint with these differences:
 
```yaml
spring:
  application:
    name: linkedin-service
server:
  port: 8082
logging:
  file:
    name: ./logs/linkedin-service.log
  logback:
    rollingpolicy:
      file-name-pattern: ./logs/linkedin-service.%d{yyyy-MM-dd}.%i.log.gz
```
 
### Orchestrator: `config/game-orchestrator.yaml`
 
```yaml
spring:
  application:
    name: game-orchestrator
 
server:
  port: 8080
 
logging:
  file:
    name: ./logs/game-orchestrator.log
  level:
    root: INFO
    com.achhecode: INFO
  logback:
    rollingpolicy:
      file-name-pattern: ./logs/game-orchestrator.%d{yyyy-MM-dd}.%i.log.gz
      max-file-size: 50MB
      total-size-cap: 1GB
      max-history: 30
      clean-history-on-start: true
 
services:
  pinpoint:
    base-url: ${PINPOINT_URL:http://localhost:8081}
  linkedin:
    base-url: ${LINKEDIN_URL:http://localhost:8082}
 
management:
  endpoints:
    web:
      exposure:
        include: health,info
 
game-cache:
  directory: ${GAME_CACHE_DIRECTORY:./data/pinpoint-solutions}
  day-start: ${GAME_CACHE_DAY_START:00:35}
  zone: ${GAME_CACHE_ZONE:Asia/Kolkata}
```
 
### Reference
 
| Setting | Default | Purpose |
|---------|---------|---------|
| `server.port` | `8080` / `8081` / `8082` | Orchestrator / Pinpoint / LinkedIn Games |
| `playwright.browser.mode` | `ATTACHED` | Attach to a running browser. Must be a mode your build supports. |
| `playwright.browser.host` / `port` | `127.0.0.1` / `9222` | Browser CDP endpoint |
| `playwright.capture.dir` | `./captures` | Browser capture directory |
| `PINPOINT_URL` | `http://localhost:8081` | Pinpoint address used by the orchestrator |
| `LINKEDIN_URL` | `http://localhost:8082` | LinkedIn Games address used by the orchestrator |
| `GAME_CACHE_DIRECTORY` | `./data/pinpoint-solutions` | Cache location |
| `GAME_CACHE_DAY_START` | `00:35` | Cache day start |
| `GAME_CACHE_ZONE` | `Asia/Kolkata` | Cache day time zone |
 
> **Env var naming:** Spring Boot turns `_` in an environment variable into `.`. The time zone property is `game-cache.zone`, so the variable must be `GAME_CACHE_ZONE`. The old name `GAME_CACHE_ZONE_ID` is bound as `game-cache.zone.id` and fails startup with `Failed to bind properties under 'game-cache.zone'` and an `InaccessibleObjectException`. If you see it, `unset GAME_CACHE_ZONE_ID` or open a new terminal.
 
### Script settings: `config/services.env` (optional)
 
`services.sh` loads `KEY=value` lines from this file. Keep it out of version control.
 
```bash
JAVA_HOME=/opt/jdk-25
ORCHESTRATOR_JAVA_OPTS="-Xms256m -Xmx1g"
GAME_CACHE_DIRECTORY=/var/lib/linkedin-games/solutions
STARTUP_TIMEOUT=240
```
 
| Variable | Default | Purpose |
|----------|---------|---------|
| `JAVA_HOME`, `JAVA_BIN` | auto-detected | Select a JDK |
| `MIN_JAVA_VERSION` | `25` | Minimum Java major version (inclusive) |
| `JAVA_OPTS` | `-XX:+ExitOnOutOfMemoryError` | JVM flags for all services |
| `PINPOINT_JAVA_OPTS`, `LINKEDIN_JAVA_OPTS`, `ORCHESTRATOR_JAVA_OPTS` | empty | Per-service JVM flags |
| `PINPOINT_PORT`, `LINKEDIN_PORT`, `ORCHESTRATOR_PORT` | `8081`, `8082`, `8080` | Ports (override `server.port`; downstream URLs follow automatically) |
| `PINPOINT_JAR`, `LINKEDIN_JAR`, `ORCHESTRATOR_JAR` | `<name>-<VERSION>.jar` | JAR filenames |
| `PINPOINT_CONFIG`, `LINKEDIN_CONFIG`, `ORCHESTRATOR_CONFIG` | `config/<name>.yaml` | Config paths (use these if your files end in `.yml`) |
| `STARTUP_TIMEOUT` | `180` | Seconds to wait per service |
| `SHUTDOWN_TIMEOUT` | `30` | Seconds before `SIGKILL` on stop |
| `HEALTH_PATH` | `/actuator/health` | Readiness endpoint probed at startup |
 
### Running a single service manually
 
```bash
java -jar linkedin-service-2.0.1.jar \
  --spring.config.additional-location=file:./config/linkedin-service.yaml \
  --server.port=8082
```
 
Do not mix manually started services with script-managed ones; the scripts only track processes they started.
 
---
 
## Logging
 
Each service writes a rolling application log (daily or at 50 MB, compressed, 1 GB cap, 30 days). The scripts also capture console output:
 
```
logs/
├── pinpoint-service.log
├── linkedin-service.log
├── game-orchestrator.log
├── pinpoint-console.log
├── linkedin-console.log
└── orchestrator-console.log
```
 
Follow a service live with `./scripts/services.sh logs <pinpoint|linkedin|orchestrator>`. Console logs rotate at start once they exceed 10 MB.
 
---
 
## Development
 
```bash
./mvnw clean package     # build
./mvnw test              # run tests
./mvnw spring-boot:run   # run one service from source
```
 
Run a single service from source:
 
```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8082 --spring.config.additional-location=file:./config/linkedin-service.yaml"
```
 
### Adding a new service
 
1. Create a Spring Boot module with its own `config/<name>.yaml` and port.
2. Expose Actuator `health` and a SpringDoc OpenAPI document.
3. Register it in `scripts/services.sh` (JAR, config, port, start order).
4. Add its base URL to the orchestrator's `services.*` settings and a route for it.
---
 
## Changes in 2.0.1
 
If you used an earlier version, note these API changes.
 
| Earlier | 2.0.1 |
|---------|-------|
| Call game endpoints directly, e.g. `POST /api/linkedin/games/queens` on port 8080 | Call **`POST /api/games/{game}/solve`** on the orchestrator (8080) |
| You supplied the solution (`positions`, `instructions`, `gridWidth`...) | The orchestrator gets it from Pinpoint and caches it; no request body |
| Port `8080` was the game service | `8080` is the orchestrator; LinkedIn Games moved to `8082`, Pinpoint is `8081` |
| LinkedIn endpoints under `/api/linkedin/games/*` | `/api/linkedin-games/{game}/solve` with body `{ "request": ... }` |
| Positions as `[row, column]` arrays | Objects: `{ "row": 0, "column": 5 }` |
| Request options `onlyKey` and `keySpeed` | Not part of the 2.0.1 request schemas |
| `browserpilot.*` configuration | `playwright.browser.*` and `playwright.capture.dir` |
| Env var `GAME_CACHE_ZONE_ID` | `GAME_CACHE_ZONE` |
 
---
 
## Troubleshooting
 
| Symptom | Likely cause / fix |
|---------|--------------------|
| `Java 25+ required` | Install JDK 25 or newer, or set `JAVA_HOME` / `JAVA_BIN` |
| `port N is already in use` | Another process owns the port, or a manually started copy is running. Stop it or change the port |
| Orchestrator fails with `Failed to bind properties under 'game-cache.zone'` | Rename `GAME_CACHE_ZONE_ID` to `GAME_CACHE_ZONE` and unset the old variable |
| Startup times out | Read `logs/<service>-console.log`; raise `STARTUP_TIMEOUT` |
| `status` shows `STARTING/UNHEALTHY` | The process is alive but `/actuator/health` is not returning 200/401/403/404; check Actuator exposure and dependencies |
| Browser attachment fails | Confirm Chrome was started with `--remote-debugging-port=9222` and the host/port match both service configs. Check `GET /api/playwright/status`, then try `POST /api/playwright/reconnect` |
| Solve returns but nothing happens in the browser | Make sure the game is open and visible in the attached browser; grant macOS Accessibility permission to the launching terminal or IDE |
| Wrong or stale solution applied | The cache may hold a solution for a different puzzle. Check `GAME_CACHE_DAY_START` and `GAME_CACHE_ZONE`, and clear the cache entry |
| Orchestrator cannot reach Pinpoint or LinkedIn Games | Check `PINPOINT_URL` / `LINKEDIN_URL` and ports with `./scripts/services.sh status` |
| `400` on `/api/playwright/navigate` | The `url` must include its scheme, e.g. `https://example.com` |
| `CROSSCLIMB` fails | No matching downstream endpoint is published in 2.0.1 |
| Stale PID files after a crash or reboot | No action needed; the next `start` or `stop` removes them |
 
---
 
## Security
 
- CDP access can control the browser and its signed-in session. Use a dedicated profile, keep port `9222` bound to localhost, and never expose it to untrusted networks.
- The Pinpoint and LinkedIn Games services can control your browser (navigate, restart, capture pages). Do not expose ports `8081` and `8082` beyond localhost; let clients reach only the orchestrator, and protect that with a reverse proxy or firewall if it is network-accessible.
- Restrict access to the release directory, logs, cache, captures, `config/services.env` and the browser profile.
- Run the services as a dedicated, low-privilege OS user.
- The scripts do not restart crashed services or start on boot. Use a process manager such as launchd or systemd for that.
---
 
## Disclaimer
 
This project automates a third-party website for personal and educational use. Check LinkedIn's terms of service before using it.