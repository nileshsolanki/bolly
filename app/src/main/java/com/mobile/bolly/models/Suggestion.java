package com.mobile.bolly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("year")
    @Expose
    private String year;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}