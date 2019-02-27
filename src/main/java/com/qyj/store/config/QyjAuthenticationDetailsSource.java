package com.qyj.store.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * 自定义的AuthenticationDetailsSource，填充登录传过来的信息（如验证码）
 * @author shitl
 */
@Component
public class QyjAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new QyjWebAuthenticationDetails(context);
    }
}