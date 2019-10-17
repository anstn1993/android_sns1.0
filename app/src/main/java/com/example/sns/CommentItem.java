package com.example.sns;

public class CommentItem {
    String profilePicture;
    String nickname;
    String comment;
    String commentDelete;
    String commentEdit;
    int viewType;
    String date;
    int textLine;
    public CommentItem(String profilePicture, String nickname, String comment,String commentDelete, String commentEdit,int viewType, String date, int textLine) {
        this.profilePicture = profilePicture;
        this.nickname = nickname;
        this.comment = comment;
        this.commentDelete=commentDelete;
        this.commentEdit=commentEdit;
        this.viewType=viewType;
        this.date=date;
        this.textLine=textLine;
    }
}
