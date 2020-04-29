
package com.android.bolly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelectedTorrent {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("magnet")
    @Expose
    private String magnet;
    @SerializedName("files")
    @Expose
    private List<File> files = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

}
