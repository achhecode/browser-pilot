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