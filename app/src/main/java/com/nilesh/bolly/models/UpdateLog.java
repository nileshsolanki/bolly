
package com.nilesh.bolly.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateLog {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("releaseNotes")
    @Expose
    private List<String> releaseNotes = null;
    @SerializedName("latestVersion")
    @Expose
    private String latestVersion;
    @SerializedName("latestVersionCode")
    @Expose
    private Integer latestVersionCode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(List<String> releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Integer getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(Integer latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

}
