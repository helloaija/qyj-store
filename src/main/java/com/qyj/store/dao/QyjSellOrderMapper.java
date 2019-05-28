package com.qyj.store.dao;

import com.qyj.store.entity.QyjSellOrderEntity;
import com.qyj.store.model.QyjUserOrderSumModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 销售订单
 * @author CTF_stone
 *
 */
@Repository
public interface QyjSellOrderMapper {

    /**
	 * 根据查询条件获取关联商品的订单列表
	 * @param paramMap
	 * @return
	 */
    List<QyjSellOrderEntity> listSellOrderAndProduct(Map<String, Object> paramMap);

    /**
     * 插入销售单
	 * @param sellOrder
     * @return
     */
	int insertSellOrder(QyjSellOrderEntity sellOrder);

    /**
	 * 根据条件统计订单信息（数量，订单金额总和，已支付金额总和）
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> countSellOrder(Map<String, Object> paramMap);

	/**
     * 获取sellId获取关联产品的销售单
	 * @param sellId
     * @return
     */
	QyjSellOrderEntity getSellOrderAndProductBySellId(Long sellId);

	/**
     * 更新销售单
	 * @param sellOrder
     * @return
     */
	int updateSellOrder(QyjSellOrderEntity sellOrder);

	/**
     * 删除销售单
	 * @param sellId
     * @return
     */
	int deleteSellOrder(Long sellId);

	/**
	 * 统计用户订单金额信息数量
	 * @param queryMap
	 * @return
	 */
    int countUserOrderSum(Map<String, Object> queryMap);

	/**
	 * 获取用户订单金额信息列表
	 * @param queryMap
	 * @return
	 */
    List<QyjUserOrderSumModel> listUserOrderSum(Map<String, Object> queryMap);
}