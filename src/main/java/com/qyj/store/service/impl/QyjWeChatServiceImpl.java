package com.qyj.store.service.impl;

import com.qyj.common.utils.CommonUtils;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.wechat.WechatUtils;
import com.qyj.store.service.QyjWeChatService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

/**
 * 微信接口业务处理
 * @author shitl
 */
@Service
public class QyjWeChatServiceImpl implements QyjWeChatService {
    private static Logger logger = LoggerFactory.getLogger(QyjWeChatServiceImpl.class);

    /**
     * 获取验证码
     * @param paramMap
     * @return
     */
    @Override
    public String getValidCode(Map<String, String> paramMap) throws Exception {
        String openId = paramMap.get("FromUserName");
        // 随机生成验证码，并放到map中
        String code = String.valueOf(CommonUtils.getRandom(6));
        CommonConstant.LOGIN_VALID_CODE_MAP.put(openId, code);
        logger.info("getValidCode openId={}, code={}", openId, code);

        String content = "您本次登录的验证码是：" + code + "，有效期3分钟，切勿将验证码泄露给他人。";

        Document document = DocumentHelper.createDocument();
        // 添加根节点
        Element root = document.addElement("xml");
        Element child = root.addElement("ToUserName");
        child.addText("<![CDATA[" + openId + "]]>");

        child = root.addElement("FromUserName");
        child.addText("<![CDATA[" + paramMap.get("ToUserName") + "]]>");

        child = root.addElement("CreateTime");
        child.addText("<![CDATA[" + System.currentTimeMillis() + "]]>");

        child = root.addElement("MsgType");
        child.addText("<![CDATA[" + WechatUtils.MSG_TYPE_TEXT + "]]>");

        child = root.addElement("Content");
        child.addText("<![CDATA[" + content + "]]>");

        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter xmlWriter = new XMLWriter(sw, format);
        xmlWriter.setEscapeText(false);
        xmlWriter.write(root);
        return sw.toString();
    }
}
