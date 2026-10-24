Current architecture


start-browser.sh
       │
       ▼
Chrome for Testing
       │
       ├── runtime/profiles/debug
       │
       └── CDP :9222
               ▲
               │
       Spring Boot
       │
       └── Playwright.connectOverCDP()


spring boot connect to browser