package com.example.sns;

public class NotificationItem {
    public String profilePicture;
    public String notificationText;
    public String notificationNickname;
    public String notificationComment;
    public int notificationPostIndex;
    public String date;

    public NotificationItem(String profilePicture, String notificationText, String notificationNickname, String notificationComment, int notificationPostIndex, String date) {
        this.profilePicture = profilePicture;
        this.notificationText = notificationText;
        this.notificationNickname=notificationNickname;
        this.notificationComment=notificationComment;
        this.notificationPostIndex=notificationPostIndex;
        this.date=date;
    }
}
