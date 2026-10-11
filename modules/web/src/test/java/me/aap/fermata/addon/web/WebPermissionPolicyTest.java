package me.aap.fermata.addon.web;

import static org.junit.Assert.*;
import org.junit.Test;

public class WebPermissionPolicyTest {
    @Test public void androidPermissionDoesNotAuthorizeAnUnapprovedWebsite() {
        var policy = new WebPermissionPolicy();
        assertFalse(policy.allows("https://example.com", "https://example.com/watch", "microphone"));
    }
    @Test public void approvalCannotLeakToOtherSitesPortsOrResources() {
        var policy = new WebPermissionPolicy();
        policy.approve("https://example.com", "https://example.com/watch", "microphone");
        assertTrue(policy.allows("https://EXAMPLE.com:443", "https://example.com/new", "microphone"));
        assertFalse(policy.allows("https://example.com", "https://evil.com", "microphone"));
        assertFalse(policy.allows("https://evil.com", "https://evil.com", "microphone"));
        assertFalse(policy.allows("https://example.com:444", "https://example.com:444", "microphone"));
        assertFalse(policy.allows("https://example.com", "https://example.com", "camera"));
        assertFalse(new WebPermissionPolicy().allows("https://example.com", "https://example.com", "microphone"));
    }
    @Test public void untrustedFramesCannotApprovePermissions() {
        var policy = new WebPermissionPolicy();
        policy.approve("https://evil.com", "https://example.com", "camera");
        assertFalse(policy.allows("https://evil.com", "https://evil.com", "camera"));
    }
    @Test public void insecureAndMalformedOriginsAreRejected() {
        for (String value : new String[]{null, "http://example.com", "file:///secret", "content://data",
                "javascript:alert(1)", "https://user@example.com", "https://example.com:99999", "invalid"}) {
            assertNull(WebPermissionPolicy.origin(value));
            assertFalse(WebPermissionPolicy.sameOrigin(value, value));
        }
    }
}
