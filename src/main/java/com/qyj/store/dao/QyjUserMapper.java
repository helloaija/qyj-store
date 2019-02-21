package com.qyj.store.dao;

import com.qyj.store.entity.QyjUserEntity;

import java.util.List;
import java.util.Map;

/**
 * 买家用户信息mapper
 * @author shitl
 */
public interface QyjUserMapper {

    /**
     * 统计数据
     * @param queryMap
     * @return
     */
    int countUser(Map<String, Object> queryMap);

    /**
     * 获取分页数据
     * @param queryMap
     * @return
     */
    List<QyjUserEntity> listUser(Map<String, Object> queryMap);

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    QyjUserEntity getUserById(Long userId);

    /**
     * 新增
     * @param record
     * @return
     */
    int insert(QyjUserEntity record);

    /**
     * 更新
     * @param record
     * @return
     */
    int update(QyjUserEntity record);


}