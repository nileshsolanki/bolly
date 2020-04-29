
package com.android.bolly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieExternalIds {

    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("facebook_id")
    @Expose
    private String facebookId;
    @SerializedName("instagram_id")
    @Expose
    private String instagramId;
    @SerializedName("twitter_id")
    @Expose
    private String twitterId;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
