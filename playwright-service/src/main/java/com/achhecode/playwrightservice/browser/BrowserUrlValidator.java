package com.achhecode.playwrightservice.browser;

import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;

@Component
public class BrowserUrlValidator {

    public URI validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank.");
        }

        final URI uri;

        try {
            uri = URI.create(value.trim()).normalize();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid URL.", exception);
        }

        String scheme = uri.getScheme();

        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("Only HTTP and HTTPS URLs are supported.");
        }

        if (uri.getHost() == null || uri.getRawUserInfo() != null) {
            throw new IllegalArgumentException("URL must contain a valid hostname and must not contain credentials.");
        }

        if (uri.getPort() < -1 || uri.getPort() > 65535) {
            throw new IllegalArgumentException("Invalid URL port.");
        }

        return uri;
    }

    public boolean matchesExactly(String existing, URI target) {
        try {
            URI candidate = validate(existing);

            return candidate.getScheme().equalsIgnoreCase(target.getScheme())
                    && candidate.getHost().equalsIgnoreCase(target.getHost())
                    && effectivePort(candidate) == effectivePort(target)
                    && normalizedPath(candidate).equals(normalizedPath(target))
                    && equalNullable(candidate.getRawQuery(), target.getRawQuery());
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private int effectivePort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return uri.getScheme().equalsIgnoreCase("https") ? 443 : 80;
    }

    private String normalizedPath(URI uri) {
        String path = uri.getRawPath();

        return path == null || path.isEmpty() ? "/" : path;
    }

    private boolean equalNullable(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }

    /**
     * Supplemental protection against navigation to local addresses. Network-level
     * restrictions are still required for robust SSRF protection.
     */
    public void rejectNonPublicAddress(URI uri) {
        String host = uri.getHost().toLowerCase(Locale.ROOT);

        if (host.equals("localhost") || host.endsWith(".localhost") || host.endsWith(".local")) {
            throw new IllegalArgumentException("Local hostnames are not allowed.");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);

            if (addresses.length == 0) {
                throw new IllegalArgumentException("Hostname did not resolve.");
            }

            // Reject the hostname if ANY resolved address is unsafe.
            for (InetAddress address : addresses) {
                if (!isAllowedAddress(address)) {
                    throw new IllegalArgumentException(
                            "Navigation to a private or special-use IP address is not allowed.");
                }
            }
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("Could not resolve hostname: " + host, exception);
        }
    }

    private boolean isAllowedAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return false;
        }

        byte[] bytes = address.getAddress();

        if (address instanceof Inet4Address) {
            return isAllowedIpv4(bytes);
        }

        if (address instanceof Inet6Address) {
            return isAllowedIpv6(bytes);
        }

        return false;
    }

    private boolean isAllowedIpv4(byte[] bytes) {
        int first = bytes[0] & 0xff;
        int second = bytes[1] & 0xff;
        int third = bytes[2] & 0xff;

        if (first == 0 || first == 10 || first == 127 || first >= 224) {
            return false;
        }

        if (first == 100 && second >= 64 && second <= 127) {
            return false;
        }

        if (first == 169 && second == 254) {
            return false;
        }

        if (first == 172 && second >= 16 && second <= 31) {
            return false;
        }

        if (first == 192 && second == 168) {
            return false;
        }

        if (first == 192 && second == 0 && third == 0) {
            return false;
        }

        if (first == 192 && second == 0 && third == 2) {
            return false;
        }

        if (first == 198 && (second == 18 || second == 19)) {
            return false;
        }

        if (first == 198 && second == 51 && third == 100) {
            return false;
        }

        if (first == 203 && second == 0 && third == 113) {
            return false;
        }

        return true;
    }

    private boolean isAllowedIpv6(byte[] bytes) {
        int first = bytes[0] & 0xff;
        int second = bytes[1] & 0xff;

        // Unique-local and link-local IPv6.
        if ((first & 0xfe) == 0xfc || (first == 0xfe && (second & 0xc0) == 0x80)) {
            return false;
        }

        // IPv4-mapped IPv6 addresses.
        boolean mapped = true;

        for (int i = 0; i < 10; i++) {
            if (bytes[i] != 0) {
                mapped = false;
                break;
            }
        }

        mapped = mapped && bytes[10] == (byte) 0xff && bytes[11] == (byte) 0xff;

        if (mapped) {
            try {
                byte[] ipv4 = { bytes[12], bytes[13], bytes[14], bytes[15] };

                return isAllowedIpv4(ipv4);
            } catch (RuntimeException exception) {
                return false;
            }
        }

        // Conservatively allow only global-unicast IPv6 space.
        return (first & 0xe0) == 0x20;
    }
}