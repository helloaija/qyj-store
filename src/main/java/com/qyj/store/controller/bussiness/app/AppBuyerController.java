package com.qyj.store.controller.bussiness.app;

import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjUserEntity;
import com.qyj.store.service.QyjUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器-用户管理
 * @author CTF_stone
 */
@Controller
@RequestMapping("/admin/app/buyer")
public class AppBuyerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AppBuyerController.class);

    @Autowired
    private QyjUserService userService;

    /**
     * 获取用户分页数据信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listUserPage", method = RequestMethod.GET)
    public ResultBean listOrderPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 用户姓名
        String userName = request.getParameter("userName");
        // 用户姓名
        String mobilePhone = request.getParameter("mobilePhone");
        // 创建开始时间
        String createTimeBegin = request.getParameter("createTimeBegin");
        // 创建结束时间
        String createTimeEnd = request.getParameter("createTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNameLike", userName);
        paramMap.put("mobilePhoneLike", mobilePhone);

        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        return userService.listUserPage(pageParam, paramMap);
    }

    /**
     * 根据主键查询用户信息
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public ResultBean getUserInfo(Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 新增用户信息
     * @param userEntity
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/insertUserInfo", method = RequestMethod.POST)
    public ResultBean insertUserInfo(QyjUserEntity userEntity) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userEntity == null) {
            return new ResultBean("0001", "买家信息为空！");
        }

        userEntity.setCreateTime(new Date());
        userEntity.setCreateUser(userDetails.getUserId());
        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateUser(userDetails.getUserId());

        return userService.insertUserInfo(userEntity);
    }

    /**
     * 更新买家信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public ResultBean updateUserInfo(QyjUserEntity userEntity) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userEntity == null) {
            return new ResultBean("0001", "买家信息为空！");
        }

        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateUser(userDetails.getUserId());

        return userService.updateUserInfo(userEntity);
    }

}
