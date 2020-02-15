package com.llt.im.utils;

import com.alibaba.fastjson.JSON;
import com.llt.im.enums.GameStatus;
import com.llt.im.game.model.KeyWord;
import com.llt.im.game.model.Message;
import com.llt.im.model.vo.GroupVO;

import java.util.List;

/**
 * @author llt11
 */
public class MessageUtills {
    public static String error(String msg){
        return "err::"+msg;
    }

    public static String img(String msg){
        return "img::"+msg;
    }

    public static String status(String msg){
        return "status::"+msg;
    }

    public static String show(String msg){
        return "show::"+msg;
    }

    public static String select(List<KeyWord> keyWordList){
        return "select::"+ JSON.toJSONString(keyWordList);
    }

    public static String status(GameStatus gameStatus){
        return "status::"+gameStatus.getCode();
    }

    public static String changeGroup(GroupVO groupVO){
        return "group::"+JSON.toJSONString(groupVO);
    }

    public static String groupListChange(){
        return "groupListChange::";
    }


    public static String clear(){
        return "clear::";
    }

    public static String message(Message message){
        return "msg::"+ JSON.toJSONString(message);
    }

    public static String getType(String msg){
        if (msg == null) {
            return null;
        }
        String[] split = msg.split("::");
        if (split.length < 1) {
            return null;
        }
        return split[0];
    }
}
