package com.qyj.store.dao;

import com.qyj.store.entity.QyjOrderGoodsEntity;
import com.qyj.store.entity.QyjStockProductEntity;
import com.qyj.store.model.QyjProductMonthCountModel;

import java.util.List;
import java.util.Map;

/**
 * 进货单商品
 * @author CTF_stone
 *
 */
public interface QyjStockProductMapper {

	/**
	 * 根据订单id查询订单商品
	 * @param stockId
	 * @return
	 */
	List<QyjStockProductEntity> listStockProductByStockId(Long stockId);

	/**
	 * 根据订单id查询订单商品
	 * @param stockId
	 * @return
	 */
	List<QyjStockProductEntity> listStockProductJoinBySellId(Long stockId);

	/**
	 * 批量插入订单商品
	 * @param stockProductList
	 * @return
	 */
	int insertStockProductList(List<QyjStockProductEntity> stockProductList);

	/**
	 * 批量更新进货单产品
	 * @param stockProductList
	 * @return
	 */
	int updateStockProductList(List<QyjStockProductEntity> stockProductList);

	/**
	 * 批量删除进货单产品
	 * @param productIdList
	 * @return
	 */
	int deleteStockProductList(List<Long> productIdList);

	/**
	 * 根据stockId删除进货单产品
	 * @param stockId
	 * @return
	 */
	int deleteStockProductByStockId(Long stockId);

	/**
	 * 获取进货产品按月统计数量
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> countStockProductMonth(Map<String, Object> paramMap);

	/**
	 * 获取进货产品按月统计列表
	 * @param paramMap
	 * @return
	 */
	List<QyjProductMonthCountModel> listStockProductMonth(Map<String, Object> paramMap);
}