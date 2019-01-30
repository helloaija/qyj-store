package com.qyj.store.dao;

import java.util.List;
import java.util.Map;

import com.qyj.store.entity.QyjProductEntity;

/**
 * 产品信息mapper
 * @author CTF_stone
 *
 */
public interface QyjProductMapper {
	
	/**
	 * 根据主键删除产品信息
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * 插入产品信息
	 * @param record
	 * @return
	 */
	int insert(QyjProductEntity record);

	/**
	 * 选择性的插入产品信息
	 * @param record
	 * @return
	 */
	int insertSelective(QyjProductEntity record);

	/**
	 * 根据产品id获取产品信息
	 * @param id
	 * @return
	 */
	QyjProductEntity selectByPrimaryKey(Long id);

	/**
	 * 根据产品id选择性的更新产品信息
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(QyjProductEntity record);

	/**
	 * 根据产品id更新产品信息
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(QyjProductEntity record);
	
	/**
	 * 根据条件统计产品数量
	 * @param paramMap
	 * @return
	 */
	Integer countProduct(Map<String, Object> paramMap);
	
	/**
	 * 根据条件获取产品列表数据
	 * @param paramMap
	 * @return
	 */
	List<QyjProductEntity> listProduct(Map<String, Object> paramMap);

	/**
	 * 更新产品数量
	 * @param list
	 * @return
	 */
	int updateProductNumberBatch(List<QyjProductEntity> list);
}