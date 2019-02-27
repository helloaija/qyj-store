package com.qyj.store.service;

import java.util.Map;

/**
 * 微信接口业务处理
 * @author shitl
 */
public interface QyjWeChatService {

    /**
     * 获取验证码
     * @param paramMap
     * @return
     */
    String getValidCode(Map<String, String> paramMap) throws Exception;
}
