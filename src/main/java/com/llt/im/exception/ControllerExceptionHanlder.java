package com.llt.im.exception;

import com.llt.im.model.resp.BaseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author llt11
 */
@ControllerAdvice
public class ControllerExceptionHanlder {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseResult errorHandler(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return BaseResult.error(ex.getMessage());
    }
}
