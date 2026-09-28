mkdir -p runtime/{browsers,profiles,downloads,screenshots,videos,traces}


echo $PLAYWRIGHT_BROWSERS_PATH
output > /Users/ardanish/Documents/Projects/browser-pilot/runtime/browsers


mvn exec:java \
-Dexec.mainClass=com.microsoft.playwright.CLI \
-Dexec.args="install chromium"