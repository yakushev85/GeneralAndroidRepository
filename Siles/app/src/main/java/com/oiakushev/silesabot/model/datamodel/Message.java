package com.oiakushev.silesabot.model.datamodel;

public class Message {
    public static int USER = 0;
    public static int SILES = 1;

    private final int author;
    private final String message;

    public Message(int author, String message) {
        this.author = author;
        this.message = message;
    }

    public int getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
