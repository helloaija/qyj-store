package com.qyj.store.service;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.entity.QyjStockOrderEntity;
import com.qyj.store.vo.QyjOrderBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务层接口-进货单
 * @author CTF_stone
 */
public interface QyjStockOrderService {
	
	/**
	 * 获取订单分页数据
	 * @param pageParam 分页信息
	 * @param paramMap 查询参数
	 * @return
	 * @throws Exception
	 */
	ResultBean listStockOrderAndProductPage(PageParam pageParam, Map<String, Object> paramMap);

	/**
	 * 获取订单分页数据 app调用
	 * @param pageParam 分页信息
	 * @param paramMap  查询参数
	 * @return
	 * @throws Exception
	 */
	ResultBean listStockOrder(PageParam pageParam, Map<String, Object> paramMap);

	/**
	 * 添加进货订单
	 * @param stockOrder
	 * @return
	 * @throws Exception
	 */
	ResultBean addStockOrder(QyjStockOrderEntity stockOrder) throws Exception;

	/**
	 * 添加进货订单-app接口
	 * @param stockOrder
	 * @return
	 * @throws Exception
	 */
	public ResultBean addStockOrderAndReturn(QyjStockOrderEntity stockOrder) throws Exception;

	/**
	 * 获取进货单数据
	 * @param stockId
	 * @return
	 */
	ResultBean loadStockOrderAndProductByStockId(Long stockId);

	/**
	 * 更新进货订单
	 * @param stockOrder
	 * @return
	 * @throws Exception
	 */
	ResultBean editStockOrder(QyjStockOrderEntity stockOrder) throws Exception;

	/**
	 * 更新进货订单-app
	 * @param stockOrder
	 * @return
	 * @throws Exception
	 */
	ResultBean editStockOrderAndReturn(QyjStockOrderEntity stockOrder);

	/**
	 * 根据stockId删除进货单
	 * @param stockId
	 * @return
	 * @throws Exception
	 */
	ResultBean deleteStockOrder(Long stockId) throws Exception;

	/**
	 * 加载产品分页信息，用于选择展示产品信息
	 * @param pageParam
	 * @param paramMap
	 * @return
	 */
	ResultBean listProductStockInfo(PageParam pageParam, Map<String, Object> paramMap);
	
}
