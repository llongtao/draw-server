package com.llt.im.game.model;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author llt11
 */
@Data
public class Message {
    private String name;
    private Date date;
    private String content;

    public Message(String name, String content) {
        this.name = name;
        this.content = content;
        this.date = new Date();
    }
}
