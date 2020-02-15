package com.llt.im.model.resp;

import lombok.Data;

@Data
public class WeChatAuth {
    private String appId;
    private String timestamp;
    private String nonceStr;
    private String signature;
    private long time;

    public WeChatAuth() {
        time = System.currentTimeMillis();
    }

    public boolean isEffective(){
        return (System.currentTimeMillis()-time)>3600000L;
    }
}
