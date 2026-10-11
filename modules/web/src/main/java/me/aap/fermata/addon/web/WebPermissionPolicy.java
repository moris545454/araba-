package me.aap.fermata.addon.web;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Website consent is separate from Android permission and lasts only for this process. */
final class WebPermissionPolicy {
    static final String LOCATION = "location";
    private final ConcurrentHashMap<String, Set<String>> approved = new ConcurrentHashMap<>();

    static String origin(String address) {
        if (address == null) return null;
        try {
            URI uri = new URI(address);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
                    uri.getRawUserInfo() != null || uri.getPort() < -1 || uri.getPort() > 65535)
                return null;
            int port = uri.getPort();
            return "https://" + uri.getHost().toLowerCase(java.util.Locale.ROOT) +
                    ((port == -1 || port == 443) ? "" : ":" + port);
        } catch (Exception ignored) { return null; }
    }

    static boolean sameOrigin(String request, String page) {
        String source = origin(request);
        return source != null && source.equals(origin(page));
    }

    boolean allows(String source, String page, String resource) {
        if (!sameOrigin(source, page)) return false;
        Set<String> resources = approved.get(origin(source));
        return resources != null && resources.contains(resource);
    }

    void approve(String source, String page, String resource) {
        if (sameOrigin(source, page))
            approved.computeIfAbsent(origin(source), key -> ConcurrentHashMap.newKeySet()).add(resource);
    }
}
