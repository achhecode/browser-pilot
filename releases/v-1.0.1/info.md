Mode 1 — default

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false

```

BrowserPilot launches the browser supplied by Playwright.


Mode 2 — custom Chromium

```yaml
browserpilot:
  browser:
    mode: launch
    headless: false
    executable-path: "/path/to/chromium"

    # macOS
    # executable-path: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

    # for MacOS Chrome Testing only
    # ../chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing
    # make sure to remove quarantine attribute



    # Linux
    # executable-path: "/usr/bin/google-chrome"

    # Windows
    # executable-path: "C:/Program Files/Google/Chrome/Application/chrome.exe"

```
And it still supports: `headless: true`

Mode 3 — existing browser

```yaml
browserpilot:
  browser:
    mode: attach

    attach:
      host: 127.0.0.1
      port: 9222
```


# Command line configuration:

```bash
java -jar browser-pilot.jar \
  --browserpilot.browser.headless=true


# Custom executable
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/path/to/chromium"

# For Chrome on macOS
java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

# Attach

java -jar browser-pilot.jar \
  --browserpilot.browser.mode=attach \
  --browserpilot.browser.attach.port=9222 \
  --browserpilot.browser.headless=true

```



