package com.example.myapplication.Community.Like;

public class LikeDictionary {
    private String userName;
    private String userGroup;
    public String getUserGroup() {
        return userGroup;
    }
    public String getUserName() {
        return userName;
    }
    public LikeDictionary(String userName, String userGroup) {
        this.userName = userName;
        this.userGroup = userGroup;
    }


}
