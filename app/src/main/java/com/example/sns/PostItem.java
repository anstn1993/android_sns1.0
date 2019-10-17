package com.example.sns;

public class PostItem {
    public String postPictureUri, profilePicutreUri;
    public int likeID, commentID,commentCount,moreID, index;
    public String nickname, article;
    public Boolean likeState, focusState;
    float latitude;
    float longitude;
    String address;
    String date;
    int textLine;

    public PostItem(String profilePicutreUri, String postPictureUri, int likeID, int commentID,int commentCount, int moreID, String nickname, String article, Boolean likeState, int index, Boolean focusState, float latitude, float longitude, String address, String date, int textLine) {
        this.profilePicutreUri = profilePicutreUri;
        this.postPictureUri = postPictureUri;
        this.likeID=likeID;
        this.commentID=commentID;
        this.commentCount=commentCount;
        this.moreID=moreID;
        this.nickname = nickname;
        this.article = article;
        this.likeState=likeState;
        this.index=index;
        this.focusState=focusState;
        this.longitude=longitude;
        this.latitude=latitude;
        this.address=address;
        this.date=date;
        this.textLine=textLine;
    }
}
