package com.llt.im.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author llt11
 */
@Data
@NoArgsConstructor
public class KeyWord {
    private String name;

    private int len;

    private String desc;

    public KeyWord(String name, String desc) {
        this.name = name;
        this.len = name.trim().length();
        this.desc = desc;
    }
}
