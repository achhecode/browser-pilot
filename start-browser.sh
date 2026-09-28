#!/bin/bash

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

export PLAYWRIGHT_BROWSERS_PATH="$PROJECT_DIR/runtime/browsers"

PROFILE_DIR="$PROJECT_DIR/runtime/profiles/debug"

PORT=9222

mkdir -p "$PROFILE_DIR"

echo "Starting BrowserPilot Chromium..."
echo "Browser path: $PLAYWRIGHT_BROWSERS_PATH"
echo "Profile: $PROFILE_DIR"
echo "CDP port: $PORT"



# CHROMIUM_PATH=$(find "$PLAYWRIGHT_BROWSERS_PATH" \
#     -path "*/chrome" \
#     -type f \
#     -perm -111 \
#     | head -n 1)

# if [ -z "$CHROMIUM_PATH" ]; then

#     echo "ERROR: Chromium executable not found."

#     echo "Install Chromium with:"
#     echo ""
#     echo 'PLAYWRIGHT_BROWSERS_PATH="$PWD/runtime/browsers" mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"'

#     exit 1
# fi

CHROMIUM_PATH=$(find "$PLAYWRIGHT_BROWSERS_PATH" \
    -type f \
    -path "*/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" \
    | head -n 1)

if [ -z "$CHROMIUM_PATH" ]; then
    echo "ERROR: Chromium executable not found."
    exit 1
fi

echo "Using Chromium:"
echo "$CHROMIUM_PATH"

"$CHROMIUM_PATH" \
    --remote-debugging-port="$PORT" \
    --user-data-dir="$PROFILE_DIR" \
    --no-first-run \
    --no-default-browser-check
    # optional
    --disable-features=WelcomePage,SignInPromo,PromoBrowser



# chmod +x start-browser.sh
# ./start-browser.sh