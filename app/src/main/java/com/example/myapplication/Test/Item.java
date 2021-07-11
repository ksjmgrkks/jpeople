package com.example.myapplication.Test;

public class Item {

    private String content;
    private String name;
    private String time;
    private String profilePath;
    private String imagePath;
    private int viewType;

    public Item(String content, String name , String time, String profilePath, String imagePath, int viewType) {
        this.content = content;
        this.name = name;
        this.time = time;
        this.profilePath = profilePath;
        this.imagePath = imagePath;
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
    public String getProfilePath() {
        return profilePath;
    }
    public String getImagePath() {
        return imagePath;
    }
    public int getViewType() {
        return viewType;
    }
}
