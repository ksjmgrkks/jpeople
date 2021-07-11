package com.example.myapplication.Community;

public class CommunityDictionary {
    private String cwNumber;
    private String cwWriter;
    private String cwCreated;
    private String cwTitle;
    private String cwContents;
    private String cwLike;
    private String cwComment;
    private String cwProfile;
    private String cwLikeIsset;
    private String cwImage;
    private String totalPage;
    public String getTotalPage() {
        return totalPage;
    }
    public String getCwImage() {
        return cwImage;
    }
    public String getUserGroup() {
        return userGroup;
    }

    private String userGroup;
    public String getCwLikeIsset() {
        return cwLikeIsset;
    }
    public String getCwProfile() {
        return cwProfile;
    }
    public String getCwNumber() {
        return cwNumber;
    }

    public String getCwWriter() {
        return cwWriter;
    }
    public String getCwCreated() {
        return cwCreated;
    }

    public String getCwTitle() {
        return cwTitle;
    }

    public String getCwContents() {
        return cwContents;
    }

    public String getCwLike() {
        return cwLike;
    }

    public String getCwComment() {
        return cwComment;
    }
    public CommunityDictionary(String cwNumber,
                               String cwWriter,
                               String cwCreated,
                               String cwTitle,
                               String cwContents,
                               String cwLike,
                               String cwComment,
                               String cwProfile,
                               String cwLikeIsset,
                               String userGroup,
                               String cwImage,
                               String totalPage) {
        this.cwNumber = cwNumber;
        this.cwWriter = cwWriter;
        this.cwCreated = cwCreated;
        this.cwTitle = cwTitle;
        this.cwContents = cwContents;
        this.cwLike = cwLike;
        this.cwComment = cwComment;
        this.cwProfile = cwProfile;
        this.cwLikeIsset = cwLikeIsset;
        this.userGroup = userGroup;
        this.cwImage = cwImage;
        this.totalPage = totalPage;

    }


}
