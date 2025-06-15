package com.example.whatsapp;
public class ChatMessage {
    public String senderId;
    public String receiverId;
    public String message;
    public long timestamp;
    public boolean isRead;

    public ChatMessage() {}

    public ChatMessage(String senderId, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    // getter setter
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) { isRead = read; }

    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

}
