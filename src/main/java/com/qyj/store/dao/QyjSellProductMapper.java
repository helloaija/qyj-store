package com.qyj.store.dao;

import com.qyj.store.entity.QyjSellProductEntity;
import com.qyj.store.model.QyjProductMonthCountModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 销售单商品
 * @author CTF_stone
 *
 */
@Repository
public interface QyjSellProductMapper {

	/**
	 * 根据订单id查询订单商品
	 * @param sellId
	 * @return
	 */
	List<QyjSellProductEntity> listSellProductBySellId(Long sellId);

	/**
	 * 根据订单id查询订单商品关联产品
	 * @param sellId
	 * @return
	 */
	List<QyjSellProductEntity> listSellProductJoinBySellId(Long sellId);

	/**
	 * 批量插入订单商品
	 * @param sellProductList
	 * @return
	 */
	int insertSellProductList(List<QyjSellProductEntity> sellProductList);

	/**
     * 批量更新销售单产品
	 * @param sellProductList
     * @return
     */
	int updateSellProductList(List<QyjSellProductEntity> sellProductList);

	/**
	 * 批量删除销售单产品
	 * @param productIdList
	 * @return
	 */
	int deleteSellProductList(List<Long> productIdList);

	/**
     * 根据sellId删除销售单产品
	 * @param sellId
     * @return
     */
	int deleteSellProductBySellId(Long sellId);

	/**
	 * 获取销售产品按月统计数量
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> countSellProductMonth(Map<String, Object> paramMap);

	/**
	 * 获取销售产品按月统计列表
	 * @param paramMap
	 * @return
	 */
	List<QyjProductMonthCountModel> listSellProductMonth(Map<String, Object> paramMap);

	/**
	 * 获取每个月销售额，按产品类型统计
	 * @param year
	 * @return
	 */
	List<Map<String, Object>> getMonthSellData(int year);
}