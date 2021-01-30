package com.example.matafonov.models;

public class GifRecord {
    private String description;
    private String url;

    public GifRecord(String description, String url){
        this.description = description;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
