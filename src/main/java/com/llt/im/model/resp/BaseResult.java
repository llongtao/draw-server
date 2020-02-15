package com.llt.im.model.resp;

import com.llt.im.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author llt11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult {

    public static final BaseResult SUCCESS = new BaseResult(true,"200", StringUtils.EMPTY);

    public static final BaseResult ERROR = new BaseResult(false,"500", "服务异常");

    private boolean succeed = true;
    private String code;
    private String msg;

    public static BaseResult error(String code,String msg){
        return new BaseResult(false,code,msg);
    }

    public static BaseResult error(String msg){
        return new BaseResult(false,"500",msg);
    }
}
