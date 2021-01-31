package com.example.matafonov.models;

public class GifRecord {
    private final String description;
    private final String url;

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
