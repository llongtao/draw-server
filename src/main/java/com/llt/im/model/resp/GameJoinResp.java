package com.llt.im.model.resp;

import com.llt.im.game.model.Group;
import lombok.Data;

@Data
public class GameJoinResp extends BaseResult {
    private Group group;
}
