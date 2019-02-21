package com.qyj.store.service;

import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.entity.QyjUserEntity;

import java.util.Map;

/**
 * 买家用户服务层接口
 * @author CTF_stone
 */
public interface QyjUserService {

    /**
     * 获取用户分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    ResultBean listUserPage(PageParam pageParam, Map<String, Object> paramMap);

    /**
     * 根据产品id获取用户信息
     * @param userId
     * @return
     */
    ResultBean getUserInfo(Long userId);

    /**
     * 插入用户信息
     * @param record
     * @return
     */
    ResultBean insertUserInfo(QyjUserEntity record);

    /**
     * 根据产品id更新产品信息
     * @param record
     * @return
     */
    ResultBean updateUserInfo(QyjUserEntity record);
}
