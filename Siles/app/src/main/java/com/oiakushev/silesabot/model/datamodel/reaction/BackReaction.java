package com.oiakushev.silesabot.model.datamodel.reaction;

public class BackReaction {
    private String msg;

    public BackReaction(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String generateQuestion() {
        return "Как я должна отвечать на '" + msg + "'?";
    }
}
