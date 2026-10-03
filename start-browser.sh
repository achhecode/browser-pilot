#!/usr/bin/env bash
# BrowserPilot Chrome manager
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BROWSER_ROOT="$PROJECT_DIR/testing-browser"

# Overridable via environment, e.g. PORT=9333 PROFILE_NAME=dev ./browser.sh start
PROFILE_NAME="${PROFILE_NAME:-ar}"
PORT="${PORT:-9222}"
START_TIMEOUT="${START_TIMEOUT:-15}"   # seconds to wait for CDP
MAX_LOG_BYTES="${MAX_LOG_BYTES:-5242880}"  # rotate log above 5 MB

CHROME_PATH="${CHROME_PATH:-$BROWSER_ROOT/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing}"
PROFILE_DIR="$BROWSER_ROOT/browser-data/$PROFILE_NAME"
RUNTIME_DIR="$BROWSER_ROOT/runtime"
PID_FILE="$RUNTIME_DIR/chrome.pid"
LOG_FILE="$RUNTIME_DIR/chrome.log"
CDP_URL="http://127.0.0.1:$PORT"

mkdir -p "$RUNTIME_DIR" "$PROFILE_DIR"

usage() {
    cat <<EOF

BrowserPilot Chrome manager

Usage: $0 <command>

  start       Start Chrome detached
  stop        Stop Chrome
  restart     Restart Chrome
  status      Show status
  logs [N]    Show last N log lines (default 100)
  log-tail    Follow logs
  shell       Open an interactive zsh shell in the project dir
  reset       Delete the persistent browser profile

Env overrides: PORT, PROFILE_NAME, CHROME_PATH, START_TIMEOUT

EOF
}

die() { echo "ERROR: $*" >&2; exit 1; }

read_pid() { [ -f "$PID_FILE" ] && tr -d '[:space:]' < "$PID_FILE" || true; }

# Running = PID alive AND the process is actually our Chrome (guards against PID reuse).
is_running() {
    local pid; pid="$(read_pid)"
    [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null || return 1
    ps -p "$pid" -o command= 2>/dev/null | grep -qF -- "--user-data-dir=$PROFILE_DIR"
}

cdp_ready() { curl -fsS --max-time 1 "$CDP_URL/json/version" >/dev/null 2>&1; }

port_in_use() { lsof -nP -iTCP:"$PORT" -sTCP:LISTEN >/dev/null 2>&1; }

rotate_log() {
    [ -f "$LOG_FILE" ] || return 0
    local size; size="$(stat -f%z "$LOG_FILE" 2>/dev/null || echo 0)"
    if [ "$size" -gt "$MAX_LOG_BYTES" ]; then
        mv -f "$LOG_FILE" "$LOG_FILE.1"
    fi
}

start_browser() {
    [ -x "$CHROME_PATH" ] || die "Chrome executable not found or not executable: $CHROME_PATH"

    if is_running; then
        echo "Chrome already running (PID $(read_pid)) — CDP: $CDP_URL"
        return 0
    fi
    rm -f "$PID_FILE"

    port_in_use && die "Port $PORT is already in use by another process."

    # Clear stale profile locks left by a crash (safe: we know it isn't running).
    rm -f "$PROFILE_DIR"/Singleton{Lock,Cookie,Socket}

    rotate_log

    local args=(
        --remote-debugging-port="$PORT"
        --remote-debugging-address=127.0.0.1
        --user-data-dir="$PROFILE_DIR"
        --no-first-run
        --no-default-browser-check
        --disable-features=WelcomePage,SignInPromo,PromoBrowser
        about:blank
    )

    echo "Starting Chrome  profile=$PROFILE_NAME  port=$PORT"
    nohup "$CHROME_PATH" "${args[@]}" >> "$LOG_FILE" 2>&1 < /dev/null &
    local pid=$!
    disown "$pid" 2>/dev/null || true
    echo "$pid" > "$PID_FILE"

    # Poll for readiness instead of a fixed sleep.
    local i
    for ((i = 0; i < START_TIMEOUT * 4; i++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            echo "ERROR: Chrome exited during startup. Last logs:" >&2
            tail -n 30 "$LOG_FILE" >&2 || true
            rm -f "$PID_FILE"
            exit 1
        fi
        if cdp_ready; then
            echo "Chrome ready."
            echo "PID:  $pid"
            echo "CDP:  $CDP_URL"
            echo "Logs: $LOG_FILE"
            return 0
        fi
        sleep 0.25
    done

    die "Chrome is running (PID $pid) but CDP not ready after ${START_TIMEOUT}s. Check: $0 logs"
}

stop_browser() {
    if ! is_running; then
        rm -f "$PID_FILE"
        echo "Chrome is not running."
        return 0
    fi

    local pid; pid="$(read_pid)"
    echo "Stopping Chrome (PID $pid)..."
    kill "$pid" 2>/dev/null || true   # SIGTERM = clean shutdown

    local i
    for ((i = 0; i < 20; i++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            rm -f "$PID_FILE"
            echo "Chrome stopped."
            return 0
        fi
        sleep 0.5
    done

    echo "Graceful stop timed out; sending SIGKILL..."
    kill -9 "$pid" 2>/dev/null || true
    rm -f "$PID_FILE"
    echo "Chrome stopped."
}

status_browser() {
    if is_running; then
        echo "Chrome:  RUNNING"
        echo "PID:     $(read_pid)"
        echo "CDP:     $CDP_URL ($(cdp_ready && echo responding || echo 'not responding'))"
        echo "Profile: $PROFILE_DIR"
        echo "Logs:    $LOG_FILE"
    else
        rm -f "$PID_FILE"
        echo "Chrome:  STOPPED"
        return 3   # conventional "not running" status code, handy in scripts
    fi
}

show_logs() {
    [ -f "$LOG_FILE" ] || { echo "No log file yet: $LOG_FILE"; return 0; }
    tail -n "${1:-100}" "$LOG_FILE"
}

tail_logs() {
    [ -f "$LOG_FILE" ] || { echo "No log file yet: $LOG_FILE"; return 0; }
    exec tail -f "$LOG_FILE"
}

open_shell() {
    cd "$PROJECT_DIR"
    echo "Opening zsh in $PROJECT_DIR"
    exec /bin/zsh
}

reset_profile() {
    is_running && die "Chrome is running. Run '$0 stop' first."

    echo "WARNING: This will delete the persistent browser profile:"
    echo "$PROFILE_DIR"
    read -r -p "Continue? [y/N] " answer
    case "$answer" in
        [yY]|[yY][eE][sS])
            rm -rf "${PROFILE_DIR:?}"
            mkdir -p "$PROFILE_DIR"
            echo "Browser profile reset."
            ;;
        *) echo "Cancelled." ;;
    esac
}

case "${1:-}" in
    start)    start_browser ;;
    stop)     stop_browser ;;
    restart)  stop_browser; start_browser ;;
    status)   status_browser ;;
    logs)     show_logs "${2:-100}" ;;
    log-tail) tail_logs ;;
    shell)    open_shell ;;
    reset)    reset_profile ;;
    *)        usage; exit 1 ;;
esac