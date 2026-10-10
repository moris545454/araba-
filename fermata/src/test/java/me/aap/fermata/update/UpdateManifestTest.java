package me.aap.fermata.update;

import static org.junit.Assert.*;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UpdateManifestTest {
    private JSONObject release() throws Exception {
        JSONObject release = new JSONObject().put("tag_name", "v2.0.3");
        org.json.JSONArray assets = new org.json.JSONArray();
        for (String name : new String[]{UpdateManifest.APK_ASSET, UpdateManifest.MANIFEST_ASSET})
            assets.put(new JSONObject().put("name", name).put("state", "uploaded").put("browser_download_url",
                    "https://github.com/" + UpdateManifest.REPOSITORY + "/releases/download/v2.0.3/" + name));
        return release.put("assets", assets);
    }
    private JSONObject manifest() throws Exception {
        return new JSONObject().put("versionCode", 310).put("versionName", "2.0.3")
                .put("packageId", "me.app.fermataX.auto.debug").put("sha256", "a".repeat(64))
                .put("size", 12345).put("minSdkVersion", 28).put("apkAsset", UpdateManifest.APK_ASSET);
    }
    private void rejected(JSONObject release, JSONObject manifest) {
        assertThrows(org.json.JSONException.class, () -> UpdateManifest.parse(release, manifest,
                "me.app.fermataX.auto.debug", 36));
    }
    @Test public void acceptsCompleteCompatibleRelease() throws Exception {
        UpdateManifest update = UpdateManifest.parse(release(), manifest(), "me.app.fermataX.auto.debug", 36);
        assertEquals(310, update.versionCode());
        assertTrue(update.apkUrl().endsWith("/v2.0.3/" + UpdateManifest.APK_ASSET));
    }
    @Test public void rejectsOtherPackageAndMalformedChecksum() throws Exception {
        rejected(release(), manifest().put("packageId", "me.app.fermataX.auto"));
        rejected(release(), manifest().put("sha256", "fake"));
    }
    @Test public void rejectsIncompleteAndForeignAssets() throws Exception {
        JSONObject release = release();
        release.getJSONArray("assets").getJSONObject(0).put("state", "new");
        rejected(release, manifest());
        release = release();
        release.getJSONArray("assets").getJSONObject(0).put("browser_download_url", "https://evil.example/update.apk");
        rejected(release, manifest());
    }
    @Test public void rejectsDraftAndPrerelease() throws Exception {
        rejected(release().put("draft", true), manifest());
        rejected(release().put("prerelease", true), manifest());
    }
    @Test public void rejectsTooLargeApkAndNewerAndroidRequirement() throws Exception {
        rejected(release(), manifest().put("size", 1024L * 1024 * 1024));
        rejected(release(), manifest().put("minSdkVersion", 37));
    }
    @Test public void rejectsZeroVersionAndMissingMetadata() throws Exception {
        rejected(release(), manifest().put("versionCode", 0));
        JSONObject manifest = manifest(); manifest.remove("sha256"); rejected(release(), manifest);
    }
    @Test public void rejectsUnsafeReleaseTagAndWrongAssetName() throws Exception {
        rejected(release().put("tag_name", "../other"), manifest());
        rejected(release(), manifest().put("apkAsset", "mobile.apk"));
    }
    @Test public void acceptsVersionBoundRepositoryLfsAndRejectsUnknownChannels() throws Exception {
        JSONObject release = release().put("assets", new org.json.JSONArray());
        UpdateManifest update = UpdateManifest.parse(release, manifest().put("distribution", "repository-lfs"),
                "me.app.fermataX.auto.debug", 36);
        assertEquals("https://media.githubusercontent.com/media/" + UpdateManifest.REPOSITORY +
                "/v2.0.3/distribution/" + UpdateManifest.APK_ASSET, update.apkUrl());
        assertEquals("https://raw.githubusercontent.com/" + UpdateManifest.REPOSITORY +
                "/v2.0.3/distribution/" + UpdateManifest.MANIFEST_ASSET,
                UpdateManifest.repositoryAssetUrl(release, UpdateManifest.MANIFEST_ASSET));
        rejected(release, manifest().put("distribution", "unknown"));
        rejected(release.put("draft", true), manifest().put("distribution", "repository-lfs"));
    }

}
