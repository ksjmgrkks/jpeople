package com.example.myapplication.Home;

public class HomeSliderDictionary {
    String imageUri;
    String slidePage;
    public String getImageUri() {
        return imageUri;
    }
    public String getSlidePage() {
        return slidePage;
    }
    public HomeSliderDictionary(String imageUri, String slidePage) {
        this.imageUri = imageUri;
        this.slidePage = slidePage;
    }
}
