```bash
chmod +x start-browser.sh


# Start Chrome detached
./start-browser.sh start

```


  start       Start Chrome detached
  stop        Stop Chrome
  restart     Restart Chrome
  status      Show status
  logs [N]    Show last N log lines (default 100)
  log-tail    Follow logs
  shell       Open an interactive zsh shell in the project dir
  reset       Delete the persistent browser profile
 
Env overrides: PORT, PROFILE_NAME, CHROME_PATH, START_TIMEOUT