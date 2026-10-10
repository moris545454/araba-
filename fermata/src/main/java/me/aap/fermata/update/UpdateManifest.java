package me.aap.fermata.update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** A release is installable only when its manifest matches a public GitHub APK asset. */
public record UpdateManifest(int versionCode, String versionName, String packageId,
                             String sha256, String apkUrl, long size) {
    public static final String REPOSITORY = "moris545454/araba-";
    public static final String MANIFEST_ASSET = "update-auto-debug.json";
    public static final String APK_ASSET = "FermataX-yeni-arayuz-debug.apk";

    public static String assetUrl(JSONObject release, String name) throws JSONException {
        if (release.optBoolean("draft") || release.optBoolean("prerelease"))
            throw new JSONException("Unpublished release");
        String tag = release.getString("tag_name");
        if (!tag.matches("[A-Za-z0-9._-]+")) throw new JSONException("Invalid release tag");
        String expected = "https://github.com/" + REPOSITORY + "/releases/download/" + tag + "/" + name;
        JSONArray assets = release.getJSONArray("assets");
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            if (name.equals(asset.getString("name")) && "uploaded".equals(asset.getString("state")) &&
                    expected.equals(asset.getString("browser_download_url"))) return expected;
        }
        throw new JSONException("Missing release asset: " + name);
    }

    public static String repositoryAssetUrl(JSONObject release, String name) throws JSONException {
        if (release.optBoolean("draft") || release.optBoolean("prerelease"))
            throw new JSONException("Unpublished release");
        String tag = release.getString("tag_name");
        if (!tag.matches("[A-Za-z0-9._-]+")) throw new JSONException("Invalid release tag");
        if (MANIFEST_ASSET.equals(name))
            return "https://raw.githubusercontent.com/" + REPOSITORY + "/" + tag + "/distribution/" + name;
        if (APK_ASSET.equals(name))
            return "https://media.githubusercontent.com/media/" + REPOSITORY + "/" + tag + "/distribution/" + name;
        throw new JSONException("Unknown repository asset");
    }

    public static UpdateManifest parse(JSONObject release, JSONObject manifest, String installedId,
                                       int sdk) throws JSONException {
        int version = manifest.getInt("versionCode");
        String name = manifest.getString("versionName");
        String id = manifest.getString("packageId");
        String hash = manifest.getString("sha256");
        long size = manifest.getLong("size");
        if (version <= 0 || name.isBlank() || !id.equals(installedId) ||
                !hash.matches("[a-fA-F0-9]{64}") || size <= 0 || size > 512L * 1024 * 1024 ||
                manifest.getInt("minSdkVersion") > sdk ||
                !APK_ASSET.equals(manifest.getString("apkAsset")))
            throw new JSONException("Incompatible update manifest");
        String channel = manifest.optString("distribution", "release");
        String address;
        if ("repository-lfs".equals(channel)) address = repositoryAssetUrl(release, APK_ASSET);
        else if ("release".equals(channel)) address = assetUrl(release, APK_ASSET);
        else throw new JSONException("Unknown update distribution");
        return new UpdateManifest(version, name, id, hash, address, size);
    }
}
