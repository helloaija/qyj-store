package com.qyj.store.controller.bussiness;

import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.util.wechat.WechatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Controller
@RequestMapping("/wechat")
public class WeChatController {
    private static Logger logger = LoggerFactory.getLogger(WeChatController.class);

    /**
     * 验证微信服务器消息
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public void validProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (WechatUtils.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
        out.close();
    }

    /**
     * 微信业务处理接口
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String respText = "";

        Map<String, String> map = WechatUtils.parseXml(request);
        logger.info("doProcess info = {}", map);
        String msgType = map.get("MsgType");
        if (WechatUtils.MSG_TYPE_TEXT.equalsIgnoreCase(msgType)) {
            String content = map.get("Content");
            if ("yzm".equalsIgnoreCase(content)) {
                Document document = DocumentHelper.createDocument();
                // 添加根节点
                Element root = document.addElement("xml");
                Element child = root.addElement("ToUserName");
                child.addText("<![CDATA[" + map.get("FromUserName") + "]]>");

                child = root.addElement("FromUserName");
                child.addText("<![CDATA[" + map.get("ToUserName") + "]]>");

                child = root.addElement("CreateTime");
                child.addText("<![CDATA[" + System.currentTimeMillis() + "]]>");

                child = root.addElement("MsgType");
                child.addText("<![CDATA[" + WechatUtils.MSG_TYPE_TEXT + "]]>");

                child = root.addElement("Content");
                child.addText("<![CDATA[" + "哈哈" + "]]>");

                respText = root.asXML();

                StringWriter sw = new StringWriter();
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("utf-8");
                XMLWriter xmlWriter = new XMLWriter(sw, format);
                xmlWriter.setEscapeText(false);
                xmlWriter.write(root);
                respText = sw.toString();
            }
        } else {

        }

        if (!StringUtils.isEmpty(respText)) {
            // 响应消息
            PrintWriter out = response.getWriter();
            out.print(respText);
            out.close();
        }
    }
}
