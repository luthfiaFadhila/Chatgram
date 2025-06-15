package com.example.whatsapp;

public class ChatItem {
    private String uid;
    private String name;
    private String profileUrl;
    private String lastMessage;
    private String time;
    private int unreadCount;

    public ChatItem(String uid, String name, String profileUrl, String lastMessage, String time, int unreadCount) {
        this.uid = uid;
        this.name = name;
        this.profileUrl = profileUrl;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unreadCount = unreadCount;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getProfileUrl() { return profileUrl; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public int getUnreadCount() { return unreadCount; }

}
