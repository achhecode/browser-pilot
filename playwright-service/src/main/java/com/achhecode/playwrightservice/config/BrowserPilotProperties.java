package com.achhecode.playwrightservice.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "playwright")
@Validated
public class BrowserPilotProperties {

    @Valid
    private Browser browser = new Browser();

    @Valid
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

        private String executablePath;

        @Valid
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

        @NotBlank
        private String host = "127.0.0.1";

        @Min(1)
        @Max(65535)
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

        @NotBlank
        private String dir = "./captures";

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }

    public enum Mode {
        LAUNCH, ATTACH
    }
}