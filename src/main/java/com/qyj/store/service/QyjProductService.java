package com.qyj.store.service;

import java.util.Map;

import com.qyj.common.page.ResultBean;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.vo.QyjProductBean;
import com.qyj.store.vo.SysUserBean;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;

/**
 * 产品表服务层接口
 * @author CTF_stone
 */
public interface QyjProductService {

    /**
     * 获取产品分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    PageBean listProjectPage(PageParam pageParam, Map<String, Object> paramMap);

    /**
     * 根据产品id获取产品信息
     * @param productId
     * @return
     */
    QyjProductBean selectProductInfo(Long productId) throws Exception;

    /**
     * 构造更新数量的产品实体
     * @param productId
     * @param number
     * @param soldNumber
     * @param unpayNumber
     * @return
     */
    QyjProductEntity getUpdateNumberProductEntity(Long productId, int number, int soldNumber, int unpayNumber);

    /**
     * 假删除产品
     * @param productId
     * @param userId
     * @return
     */
    ResultBean deleteProduct(Long productId, Long userId);

    /**
     * 插入产品信息
     * @param record
     * @return
     */
    ResultBean insertProduct(QyjProductEntity record);

    /**
     * 根据产品id更新产品信息
     * @param record
     * @return
     */
    ResultBean updateProduct(QyjProductEntity record);

    /**
     * 获取产品库存数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    ResultBean listStoreDetail(PageParam pageParam, Map<String, Object> paramMap);
}
