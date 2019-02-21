package com.qyj.store.service.impl;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.enums.CommonEnums.ProductStatusEnum;
import com.qyj.store.dao.QyjUserMapper;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.entity.QyjUserEntity;
import com.qyj.store.service.QyjUserService;
import com.qyj.store.vo.QyjProductBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家用户服务层接口
 * @author CTF_stone
 */
@Service
public class QyjUserServiceImpl implements QyjUserService {

    private static final Logger logger = LoggerFactory.getLogger(QyjUserServiceImpl.class);

    @Autowired
    private QyjUserMapper qyjUserMapper;

    /**
     * 获取用户分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public ResultBean listUserPage(PageParam pageParam, Map<String, Object> paramMap) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        paramMap.put("pageParam", pageParam);

        // 统计数量
        int totalCount = qyjUserMapper.countUser(paramMap);

        if (totalCount <= 0) {
            return new ResultBean("0000", "获取数据成功", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), 0, null));
        }

        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        // 获取分页数据列表
        List<QyjUserEntity> userList = qyjUserMapper.listUser(paramMap);
        return new ResultBean("0000", "获取数据成功", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, userList));
    }

    /**
     * 根据产品id获取用户信息
     * @param userId
     * @return
     */
    @Override
    public ResultBean getUserInfo(Long userId) {
        if (userId == null || userId.longValue() == 0) {
            return new ResultBean("0001", "用户ID为空！");
        }
        // 查询用户信息
        QyjUserEntity userEntity = qyjUserMapper.getUserById(userId);
        if (userEntity == null) {
            return new ResultBean("0001", "不存在产品，产品id：" + userId);
        }

        return new ResultBean("0000", "success", userEntity);
    }

    /**
     * 插入用户信息
     * @param record
     * @return
     */
    @Override
    public ResultBean insertUserInfo(QyjUserEntity record) {
        ResultBean resultBean = new ResultBean();
        if (!this.validUserInfo(record, resultBean)) {
            return resultBean;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", record.getUserName());

        if (qyjUserMapper.countUser(paramMap) >= 1) {
            return resultBean.init("0001", "买家名称已经存在");
        }

        logger.info("insertUserInfo userEntity, {}", record);

        return resultBean.init("0000", "新增买家成功", qyjUserMapper.insert(record));
    }

    /**
     * 根据产品id更新产品信息
     * @param record
     * @return
     */
    @Override
    public ResultBean updateUserInfo(QyjUserEntity record) {
        ResultBean resultBean = new ResultBean();
        if (!this.validUserInfo(record, resultBean)) {
            return resultBean;
        }
        PageParam pageParam = new PageParam();
        pageParam.setQueryCondition(" and id <> '" + record.getId() + "'");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", record.getUserName());
        paramMap.put("pageParam", pageParam);

        if (qyjUserMapper.countUser(paramMap) >= 1) {
            return resultBean.init("0001", "买家名称已经存在");
        }

        qyjUserMapper.update(record);

        return resultBean.init("0000", "更新买家信息成功");
    }

    /**
     * 校验买家信息
     * @param record
     * @param resultBean
     * @return
     */
    private boolean validUserInfo(QyjUserEntity record, ResultBean resultBean) {
        if (StringUtils.isEmpty(record.getUserName())) {
            resultBean.init("0001", "买家名称不能为空！");
            return false;
        }
        return true;
    }
}
