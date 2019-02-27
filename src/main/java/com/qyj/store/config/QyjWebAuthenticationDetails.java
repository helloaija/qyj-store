package com.qyj.store.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;


/**
 * 自定义的WebAuthenticationDetails，获取登录传过来的参数（验证码）
 * @author shitl
 */
public class QyjWebAuthenticationDetails extends WebAuthenticationDetails {
    private String validCode;

    public QyjWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.validCode = request.getParameter("validCode");
    }

    public String getValidCode() {
        return validCode;
    }
}
