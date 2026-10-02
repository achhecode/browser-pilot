package com.achhecode.browser_pilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "browserpilot")
public class BrowserPilotProperties {

    private Browser browser = new Browser();

    private Capture capture = new Capture();

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public Capture getCapture() {
        return capture;
    }

    public void setCapture(Capture capture) {
        this.capture = capture;
    }

    public static class Browser {

        private Mode mode = Mode.LAUNCH;

        private boolean headless = false;

        /**
         * Optional path to Chromium/Chrome executable.
         *
         * If empty, Playwright's bundled browser is used.
         */
        private String executablePath;

        private Attach attach = new Attach();

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public boolean isHeadless() {
            return headless;
        }

        public void setHeadless(boolean headless) {
            this.headless = headless;
        }

        public String getExecutablePath() {
            return executablePath;
        }

        public void setExecutablePath(String executablePath) {
            this.executablePath = executablePath;
        }

        public Attach getAttach() {
            return attach;
        }

        public void setAttach(Attach attach) {
            this.attach = attach;
        }
    }

    public static class Attach {

        private String host = "127.0.0.1";

        private int port = 9222;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class Capture {

        /**
         * Directory where captured pages are stored.
         */
        private String dir = "./captures";

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }

    public enum Mode {
        LAUNCH,
        ATTACH
    }
}