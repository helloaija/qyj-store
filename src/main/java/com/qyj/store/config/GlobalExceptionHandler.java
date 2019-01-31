package com.qyj.store.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 * @author shitl
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常捕捉处理
     * @param e
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public void errorHandler(Exception e) throws Exception {
        log.error("global error handler :", e);
        throw new Exception("系统异常");
    }
}
