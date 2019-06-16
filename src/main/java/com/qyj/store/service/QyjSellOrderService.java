package com.qyj.store.service;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.entity.QyjSellOrderEntity;

import java.util.Map;

/**
 * 服务层接口-销售单
 * @author CTF_stone
 */
public interface QyjSellOrderService {
	
	/**
	 * 获取订单分页数据
	 * @param pageParam 分页信息
	 * @param paramMap 查询参数
	 * @return
	 * @throws Exception
	 */
	ResultBean listSellOrderAndProductPage(PageParam pageParam, Map<String, Object> paramMap);

	/**
	 * 获取订单分页数据 app调用
	 * @param pageParam 分页信息
	 * @param paramMap  查询参数
	 * @return
	 * @throws Exception
	 */
	ResultBean listSellOrder(PageParam pageParam, Map<String, Object> paramMap);

	/**
	 * 添加销售订单
	 * @param sellOrder
	 * @return
	 * @throws Exception
	 */
	ResultBean addSellOrder(QyjSellOrderEntity sellOrder) throws Exception;

	/**
	 * 获取销售单数据
	 * @param sellId
	 * @return
	 */
	ResultBean loadSellOrderAndProductBySellId(Long sellId);

	/**
	 * 更新销售订单
	 * @param sellOrder
	 * @return
	 * @throws Exception
	 */
	ResultBean editSellOrder(QyjSellOrderEntity sellOrder) throws Exception;

	/**
	 * 根据sellId删除销售单
	 * @param sellId
	 * @return
	 * @throws Exception
	 */
	ResultBean deleteSellOrder(Long sellId) throws Exception;

	/**
	 * 获取用户订单统计
	 * @param pageParam 分页信息
	 * @param paramMap 查询参数
	 * @return
	 * @throws Exception
	 */
	ResultBean listUserOrderSumPage(PageParam pageParam, Map<String, Object> paramMap);

	/**
	 * 获取销售单及其产品
	 * @param sellId
	 * @return
	 */
	QyjSellOrderEntity getSellOrderAndProductBySellId(Long sellId);
}
