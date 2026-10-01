# Browser Pilot

> Spring Boot service that solves LinkedIn's daily games by driving the keyboard (and optionally the browser) through a simple REST API.

You send a solution to an endpoint. Browser Pilot moves a logical cursor around the grid and types the answer into the game for you, as fast as you tell it to.

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen)
![Platform](https://img.shields.io/badge/platform-macOS-lightgrey)

---

## Table of Contents

- [Features](#features)
- [Supported Games](#supported-games)
- [Quick Start](#quick-start)
- [Common Request Options](#common-request-options)
- [Game APIs](#game-apis)
  - [Mini Sudoku](#mini-sudoku)
  - [Patches](#patches)
  - [Queens](#queens)
  - [Tango](#tango)
- [Execution Modes](#execution-modes)
- [Coordinate System & Traversal](#coordinate-system--traversal)
- [Architecture](#architecture)
- [Configuration](#configuration)
- [Logging](#logging)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

---

## Features

- Keyboard-driven solving: fast and reliable, no fragile DOM clicking
- Two modes: keyboard-only (game already open) or full browser navigation via Playwright
- Configurable key delay (`keySpeed`) from 10 ms to 1000 ms
- Input validation before any key is pressed
- Internal cursor tracking so navigation is exact
- Swagger UI for interactive testing
- Per-game rolling log files

## Supported Games

| Game | Endpoint | Status |
|------|----------|--------|
| Mini Sudoku | `POST /api/linkedin/games/mini-sudoku` | Documented |
| Patches | `POST /api/linkedin/games/patches` | Documented |
| Queens | `POST /api/linkedin/games/queens` | Documented |
| Tango | `POST /api/linkedin/games/tango` | Documented |
| Zip | `POST /api/linkedin/games/zip` | In progress |
| Crossclimb | `POST /api/linkedin/games/crossclimb` | In progress |
| Wend | `POST /api/linkedin/games/wend` | In progress |

## Tech Stack

Java 25 · Spring Boot · Spring MVC · Spring Validation · SpringDoc OpenAPI · Microsoft Playwright · JNA · Lombok · macOS CoreGraphics

---

## Quick Start

### Prerequisites

- JDK 25
- macOS (keyboard automation uses CoreGraphics through JNA)
- A browser with LinkedIn logged in

> macOS typically requires you to grant **Accessibility** permission to the terminal or IDE that launches the app before synthetic key presses are accepted.

### Run

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI spec | http://localhost:8080/v3/api-docs |

### Your first solve

1. Open a LinkedIn game (e.g. Queens) in your browser.
2. Send the solution:

```bash
curl -X POST http://localhost:8080/api/linkedin/games/queens \
  -H "Content-Type: application/json" \
  -d '{
        "positions": [5, 0, 7, 1, 4, 2, 6, 3],
        "onlyKey": true,
        "keySpeed": 20
      }'
```

Browser Pilot switches to the browser tab and enters the solution.

---

## Common Request Options

Every game endpoint accepts these two fields.

| Field | Type | Range | Description |
|-------|------|-------|-------------|
| `onlyKey` | boolean | `true` / `false` | `true`: skip navigation, switch to the browser tab, send keys only (game must already be open). `false`: Browser Pilot navigates to the game first. |
| `keySpeed` | integer (ms) | 10 – 1000 | Delay between key presses. |

### Choosing `keySpeed`

| Value | Behaviour |
|-------|-----------|
| 10 | Very fast, may be less stable |
| **20** | **Recommended for normal use** |
| 50 | Slower, easy to watch |
| 100 | Slow |
| 1000 | Debugging |

---

## Game APIs

### Mini Sudoku

`POST /api/linkedin/games/mini-sudoku`

```json
{
  "instructions": "256431413256524613631524342165165342",
  "onlyKey": true,
  "keySpeed": 20
}
```

**`instructions`** is the full solved grid as one string, read row by row, left to right, starting at the top-left. Its length must equal `gridWidth × gridHeight` (36 for a 6×6 board).

```
2 5 6 4 3 1       row 0 → 256431
4 1 3 2 5 6       row 1 → 413256
5 2 4 6 1 3       row 2 → 524613
6 3 1 5 2 4       row 3 → 631524
3 4 2 1 6 5       row 4 → 342165
1 6 5 3 4 2       row 5 → 165342
```

Concatenated: `256431413256524613631524342165165342`

---

### Patches

`POST /api/linkedin/games/patches`

```json
{
  "gridWidth": 7,
  "gridHeight": 7,
  "patches": [
    { "position": [0, 0], "move": ["RIGHT", "DOWN"] },
    { "position": [0, 2], "move": ["DOWN", "DOWN"] },
    { "position": [2, 5], "move": ["RIGHT", "DOWN", "DOWN"] }
  ],
  "onlyKey": true,
  "keySpeed": 20
}
```

*(Shortened for readability. A full 7×7 solution lists every patch.)*

| Field | Description |
|-------|-------------|
| `gridWidth` | Number of columns |
| `gridHeight` | Number of rows |
| `patches[].position` | `[row, column]` where the patch starts |
| `patches[].move` | Ordered list of `UP`, `DOWN`, `LEFT`, `RIGHT` |

For `{ "position": [0, 5], "move": ["RIGHT", "DOWN"] }` Browser Pilot will:

1. Navigate the cursor to `[0, 5]`
2. Start the patch
3. Move right, then down
4. Finish the patch

The cursor position is tracked across patches, so only the necessary movement is sent.

<details>
<summary>Full 7×7 example</summary>

```json
{
  "gridWidth": 7,
  "gridHeight": 7,
  "patches": [
    { "position": [0, 0], "move": ["RIGHT", "DOWN"] },
    { "position": [0, 2], "move": ["DOWN", "DOWN"] },
    { "position": [0, 3], "move": ["DOWN"] },
    { "position": [0, 4], "move": ["DOWN", "DOWN"] },
    { "position": [0, 5], "move": ["RIGHT", "DOWN"] },
    { "position": [2, 0], "move": ["RIGHT", "DOWN"] },
    { "position": [2, 3], "move": ["DOWN"] },
    { "position": [2, 5], "move": ["RIGHT", "DOWN", "DOWN"] },
    { "position": [3, 2], "move": ["DOWN", "DOWN", "DOWN"] },
    { "position": [3, 4], "move": ["DOWN"] },
    { "position": [4, 0], "move": ["RIGHT", "DOWN", "DOWN"] },
    { "position": [4, 3], "move": ["DOWN", "DOWN"] },
    { "position": [5, 4], "move": ["RIGHT", "RIGHT", "DOWN"] }
  ],
  "onlyKey": true,
  "keySpeed": 20
}
```

</details>

---

### Queens

`POST /api/linkedin/games/queens`

```json
{
  "positions": [5, 0, 7, 1, 4, 2, 6, 3],
  "onlyKey": true,
  "keySpeed": 10
}
```

**`positions`**: the array index is the **row**, the value is the **column** holding that row's queen. Board size is `N × N` where `N = positions.length`.

| Row | Column | Coordinate |
|-----|--------|------------|
| 0 | 5 | `[0,5]` |
| 1 | 0 | `[1,0]` |
| 2 | 7 | `[2,7]` |
| 3 | 1 | `[3,1]` |
| 4 | 4 | `[4,4]` |
| 5 | 2 | `[5,2]` |
| 6 | 6 | `[6,6]` |
| 7 | 3 | `[7,3]` |

The board is traversed in [snake order](#grid-traversal), placing a queen at each required coordinate.

---

### Tango

`POST /api/linkedin/games/tango`

```json
{
  "instructions": "MSSMMSMMSSMSSMMSSMMSSMMSSSMMSMSMMSSM",
  "onlyKey": true,
  "keySpeed": 20
}
```

**`instructions`**: one character per cell, row by row, left to right from the top-left.

| Char | Meaning |
|------|---------|
| `S` | Sun |
| `M` | Moon |

Example 3×3 board:

```
S M S
M M S      →  SMSMMSSSM
S S M
```

Browser Pilot maps each character to a `[row, column]` coordinate, splits the result into **Sun positions** and **Moon positions**, then traverses the board in snake order.

```
M S S M      [0,0]→M  [0,1]→S  [0,2]→S  [0,3]→M
M S M M      [1,0]→M  [1,1]→S  [1,2]→M  [1,3]→M
```

---

## Execution Modes

| | Keyboard-only | Browser automation |
|--|---------------|--------------------|
| Flag | `"onlyKey": true` | `"onlyKey": false` |
| Navigation | None, game must already be open | Browser Pilot opens the game via Playwright |
| Behaviour | Switches to the browser tab, sends keys | Navigates, then sends keys |
| Best for | Fast, repeat runs | Hands-off, end-to-end runs |

---

## Coordinate System & Traversal

### Coordinates

Always `[row, column]`, zero-based.

```
[0,0] [0,1] [0,2] [0,3]
[1,0] [1,1] [1,2] [1,3]
[2,0] [2,1] [2,2] [2,3]
[3,0] [3,1] [3,2] [3,3]
```

### Grid Traversal

Games that visit every cell use a **snake** traversal, which keeps the logical cursor exact and minimises key presses.

```
→ → → →
        ↓
← ← ← ←
↓
→ → → →
        ↓
← ← ← ←
```

Visit order for a 4×4 grid:

```
[0,0] [0,1] [0,2] [0,3]
[1,3] [1,2] [1,1] [1,0]
[2,0] [2,1] [2,2] [2,3]
[3,3] [3,2] [3,1] [3,0]
```

---

## Architecture

Website navigation, game logic, grid traversal and low-level keyboard control are kept separate.

```
com.achhecode.browser_pilot
├── keyboard
│   ├── KeyboardService          # abstraction
│   └── JnaKeyboardService       # macOS CoreGraphics implementation
├── grid
│   ├── GridPosition
│   ├── GridTraversal
│   ├── SnakeGridTraversal
│   └── GridPrinter
└── website
    └── linkedin
        ├── controller
        ├── service
        └── page
            └── games
                ├── minisudoku
                ├── patches
                ├── queen
                ├── tango
                └── zip
```

### Request flow

```
HTTP ─► Controller ─► Game Service ─► Mapper ─► Traversal ─► Executor ─► KeyboardService
```

| Layer | Responsibility |
|-------|----------------|
| **Controller** | Handles HTTP, validates input |
| **Service** | Coordinates the game workflow |
| **Mapper** | Converts request data to internal form (e.g. Tango string → Sun/Moon coordinates via `TangoPositionMapper`) |
| **Traversal** | Decides how the cursor moves between `GridPosition`s |
| **Executor** | Turns movement/commands into keyboard actions |
| **KeyboardService** | Low-level API: `pressLeft()`, `pressRight()`, `pressUp()`, `pressDown()`, `pressSpace()`, `typeLetter('5')` |

Because games only talk to `KeyboardService`, supporting another OS means adding a new implementation, not touching game logic.

---

## Configuration

`application.yml`:

```yaml
spring:
  application:
    name: browser-pilot

browserpilot:
  runtime-dir: ./runtime
  headless: false
  browser-count: 1

  debug:
    persistent: true   # keep one browser alive with a remote debugging port
    port: 9222

  capture:
    dir: ./page-captures

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

### Browser debugging

With `browserpilot.debug.persistent: true`, Browser Pilot runs a persistent browser exposing remote debugging on `browserpilot.debug.port`. This lets the app reattach to the same session during development.

---

## Logging

Logs are written to `logs/` with rolling policies. Archives are compressed and retained according to the Logback configuration.

```
logs/
├── browser-pilot.log
├── browser-pilot-error.log
└── automation/
    ├── queens.log
    ├── tango.log
    ├── patches.log
    ├── zip.log
    └── mini-sudoku.log
```

---

## Development

```bash
./mvnw clean package     # build
./mvnw test              # run tests
./mvnw spring-boot:run   # run
```

### Conventions

- Coordinates are zero-based and always `[row, column]`
- `keySpeed` is in milliseconds and validated to 10–1000
- All input is validated before any key is sent
- Game logic never depends on the OS keyboard implementation

---

## Troubleshooting

| Symptom | Likely cause / fix |
|---------|--------------------|
| Nothing happens, no keys typed | Grant Accessibility permission to your terminal/IDE in macOS System Settings |
| Keys land in the wrong place | Make sure the game is open and focused; with `onlyKey: true` it is not opened for you |
| Dropped or misordered inputs | Increase `keySpeed` (try 20 → 50) |
| Validation error on request | Check string length matches the grid size and `keySpeed` is within 10–1000 |
| Can't reach Swagger | Confirm the app is running on port 8080 |

---

## Disclaimer

This project automates a third-party website for personal and educational use. Check LinkedIn's terms of service before using it.