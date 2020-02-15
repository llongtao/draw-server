package com.llt.im.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author llt11
 */

@Getter
@AllArgsConstructor
public enum GameStatus {
    /**
     *
     */
    NO_LOGIN("0"),
    LOGIN("1"),
    CREATED("2"),
    WAIT_SELECT("3"),
    PLAYING("4")
    ;

    private String code;
}
