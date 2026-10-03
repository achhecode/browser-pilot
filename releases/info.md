# Running BrowserPilot

BrowserPilot can be run as a standalone Spring Boot JAR and supports three browser modes:

1. **Launch Playwright's browser** — default
2. **Launch a custom Chromium/Chrome executable**
3. **Attach to an already-running Chromium browser using CDP**

Browser configuration can be provided through `application.yml` or overridden from the command line.

---

## Prerequisites

* Java 25 or later
* A BrowserPilot release JAR

Verify Java:

```bash
java -version
```

---

## Run the Application

Start BrowserPilot with the default configuration:

```bash
java -jar browser-pilot.jar
```

By default, BrowserPilot launches a Playwright-managed browser in non-headless mode.

---

# Browser Modes

## Mode 1 — Launch Playwright Browser

This is the default mode.

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
```

BrowserPilot launches the browser managed by Playwright.

### Headless

To run without opening a visible browser window:

```yaml
browserpilot:
  browser:
    mode: launch
    headless: true
```

---

## Mode 2 — Launch Custom Chromium/Chrome

You can provide the path to an existing Chromium or Chrome executable.

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/path/to/chromium"
```

### macOS — Google Chrome

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
```

### macOS — Chrome for Testing

The path must point to the executable inside the `.app` bundle:

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/path/to/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing"
```

If Chrome for Testing was downloaded from the internet and macOS reports that it is damaged or cannot be opened, the application may have a quarantine attribute.

If you trust the downloaded application, remove the quarantine attribute:

```bash
xattr -dr com.apple.quarantine "/path/to/Google Chrome for Testing.app"
```

Then verify the executable:

```bash
"/path/to/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" --version
```

The executable should successfully print its version before using the path in BrowserPilot.

### Linux

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/usr/bin/google-chrome"
```

### Windows

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "C:/Program Files/Google/Chrome/Application/chrome.exe"
```

`headless` can also be set to `true` when using a custom executable:

```yaml
browserpilot:
  browser:
    mode: launch
    headless: true
    executable-path: "/path/to/chromium"
```

If `executable-path` is not specified, Playwright's browser is used.

---

# Mode 3 — Attach to an Existing Browser

BrowserPilot can connect to an already-running Chromium browser through Chrome DevTools Protocol (CDP).

```yaml
browserpilot:
  browser:
    mode: attach

    attach:
      host: 127.0.0.1
      port: 9222
```

Start Chromium with remote debugging enabled before starting BrowserPilot.

For example:

```bash
./start-browser.sh
```

Then start BrowserPilot:

```bash
java -jar browser-pilot.jar
```

BrowserPilot will connect to the existing browser instead of launching a new one.

### Important

In `attach` mode, BrowserPilot does not own the Chromium process. Therefore, stopping BrowserPilot does not intentionally close the externally started browser.

`headless` has no effect in `attach` mode because BrowserPilot does not launch the browser. The visibility of the browser is determined when the external Chromium process is started.

---

# Command-Line Configuration

Browser configuration can also be supplied directly when starting the JAR.

Command-line properties override the corresponding values from `application.yml`.

## Headless

Launch the browser in headless mode:

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.headless=true
```

---

## Custom Chromium

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/path/to/chromium"
```

### macOS

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
```

### Linux

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/usr/bin/google-chrome"
```

### Windows

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="C:/Program Files/Google/Chrome/Application/chrome.exe"
```

---

## Attach to Existing Browser

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.mode=attach \
  --browserpilot.browser.attach.port=9222
```

You can also specify the host:

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.mode=attach \
  --browserpilot.browser.attach.host=127.0.0.1 \
  --browserpilot.browser.attach.port=9222
```

---

# Configuration Examples

## Playwright Browser — Visible

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
```

Run:

```bash
java -jar browser-pilot.jar
```

---

## Playwright Browser — Headless

```yaml
browserpilot:
  browser:
    mode: launch
    headless: true
```

---

## Custom Chrome — Visible

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/path/to/chrome"
```

---

## Custom Chrome — Headless

```yaml
browserpilot:
  browser:
    mode: launch
    headless: true
    executable-path: "/path/to/chrome"
```

---

## Existing Chromium Browser

```yaml
browserpilot:
  browser:
    mode: attach

    attach:
      host: 127.0.0.1
      port: 9222
```

---

# Browser Configuration Summary

| Configuration      | `launch`                                | `attach`              |
| ------------------ | --------------------------------------- | --------------------- |
| `headless`         | Supported                               | Not applicable        |
| `executable-path`  | Optional                                | Not applicable        |
| `attach.host`      | Not applicable                          | Supported             |
| `attach.port`      | Not applicable                          | Supported             |
| Playwright browser | Default when no executable is specified | Not used              |
| Existing Chromium  | Not used                                | Connected through CDP |

---

# Using a Custom Configuration File

Instead of modifying the JAR, you can provide Spring Boot configuration externally.

For example, create:

```text
application.yml
```

and start BrowserPilot with the configuration available to Spring Boot.

Alternatively, individual properties can be supplied directly through command-line arguments:

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.mode=launch \
  --browserpilot.browser.headless=false \
  --browserpilot.browser.executable-path="/path/to/chromium"
```

This makes the release usable across different machines without modifying or rebuilding the JAR.
