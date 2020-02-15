package com.llt.im.model.req;

import lombok.Data;

/**
 * @author llt11
 */
@Data
public class GameSendReq {
    private String userId;
    private String message;
    private String groupId;
}
