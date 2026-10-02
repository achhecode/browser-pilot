mkdir -p runtime/{browsers,profiles,downloads,screenshots,videos,traces}


echo $PLAYWRIGHT_BROWSERS_PATH
output > /Users/ardanish/Documents/Projects/browser-pilot/runtime/browsers


mvn exec:java \
-Dexec.mainClass=com.microsoft.playwright.CLI \
-Dexec.args="install chromium"


---



Start Chromium separately


```sh
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome \
  --remote-debugging-port=9222 \
  --user-data-dir="$PWD/runtime/profiles/debug"
```

If  using Brave:

```sh
/Applications/Brave\ Browser.app/Contents/MacOS/Brave\ Browser \
  --remote-debugging-port=9222 \
  --user-data-dir="$PWD/runtime/profiles/debug"
```

---


```sh

java -jar browser-pilot.jar

# with a custom browser:

java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"



java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/usr/bin/google-chrome"
```



---


Making chrome for testing executable:

If
```bash
"/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" --version

# shows error: “Google Chrome for Testing” is damaged and can’t be opened. You should move it to the Trash.
```

Because Apple notes that macOS Gatekeeper checks downloaded applications and can block software it considers untrusted, modified, or otherwise unable to verify.


```bash
xattr -l "/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app"

com.apple.quarantine: 0281;6abf8d12;Brave;4B6F408B-FB83-4137-9CB6-8C10FCA99404


# Remove the quarantine attribute

xattr -dr com.apple.quarantine "/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app"

# now

"/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" --version

Google Chrome for Testing 154.0.8037.92

```