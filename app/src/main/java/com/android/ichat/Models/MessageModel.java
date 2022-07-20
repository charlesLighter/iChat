package com.android.ichat.Models;

public class MessageModel {
    String user_id, message, messageId;
    Long timestamp;

    public MessageModel() {
        //Empty constructor
    }


    public MessageModel(String user_id, String message, Long timestamp) {
        this.user_id = user_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessageModel(String user_id, String message) {
        this.user_id = user_id;
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
