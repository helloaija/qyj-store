package com.qyj.store.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.qyj.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.qyj.store.common.enums.CommonEnums.ProductDetailStatusEnum;
import com.qyj.store.common.enums.CommonEnums.ProductStatusEnum;
import com.qyj.store.common.util.FileUtils;
import com.qyj.store.common.util.Utils;
import com.qyj.store.dao.QyjFileInfoMapper;
import com.qyj.store.dao.QyjProductDetailMapper;
import com.qyj.store.dao.QyjProductMapper;
import com.qyj.store.entity.QyjFileInfoEntity;
import com.qyj.store.entity.QyjProductDetailEntity;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.vo.QyjFileInfoBean;
import com.qyj.store.vo.QyjProductBean;
import com.qyj.store.vo.QyjProductDetailBean;
import com.qyj.store.vo.SysUserBean;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;

/**
 * 产品表服务层接口
 * @author CTF_stone
 */
@Service
public class QyjProductServiceImpl implements QyjProductService {

	private static final Logger logger = LoggerFactory.getLogger(QyjProductServiceImpl.class);

	@Autowired
	private QyjProductMapper productMapper;
	
	@Autowired
	private QyjFileInfoMapper fileInfoMapper;
	
	@Autowired
	private QyjProductDetailMapper productDetailMapper;
	

	/**
	 * 根据主键删除产品信息
	 * @param id
	 * @return
	 */
	@Override
	public int deleteByPrimaryKey(Long id) throws Exception {
		return productMapper.deleteByPrimaryKey(id);
	}

	/**
	 * 插入产品信息
	 * @param record
	 * @return
	 */
	@Override
	public int insert(QyjProductEntity record) throws Exception {
		return productMapper.insert(record);
	}

	/**
	 * 选择性的插入产品信息
	 * @param record
	 * @return
	 */
	@Override
	public int insertSelective(QyjProductEntity record) throws Exception {
		return productMapper.insertSelective(record);
	}

	/**
	 * 根据产品id获取产品信息
	 * @param id
	 * @return
	 */
	@Override
	public QyjProductEntity selectByPrimaryKey(Long id) throws Exception {
		return productMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据产品id选择性的更新产品信息
	 * @param record
	 * @return
	 */
	@Override
	public int updateByPrimaryKeySelective(QyjProductEntity record) throws Exception {
		return productMapper.updateByPrimaryKeySelective(record);
	}

	/**
	 * 根据产品id更新产品信息
	 * @param record
	 * @return
	 */
	@Override
	public int updateByPrimaryKey(QyjProductEntity record) throws Exception {
		return productMapper.updateByPrimaryKey(record);
	}

	/**
	 * 获取产品分页数据
	 * @param pageParam 分页信息
	 * @param paramMap 查询参数
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageBean listProjectPage(PageParam pageParam, Map<String, Object> paramMap) throws Exception {
		if (pageParam == null) {
			pageParam = new PageParam();
		}
		if (paramMap == null) {
			paramMap = new HashMap<String, Object>();
		}
		// 统计产品数量
		int totalCount = productMapper.countProduct(paramMap);

		if (totalCount <= 0) {
			return new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), 0, null);
		}

		pageParam.setTotalCount(totalCount);
		// 计算分页信息
		pageParam.splitPageInstance();

		paramMap.put("pageParam", pageParam);

		// 获取分页数据列表
		List<QyjProductEntity> projectList = productMapper.listProduct(paramMap);

		return new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, projectList);
	}

	/**
	 * 保存产品信息包括图片信息
	 * @param sysUserBean
	 * @param productBean
	 * @throws Exception
	 */
	@Override
	public void saveAllProductInfo(SysUserBean sysUserBean, QyjProductBean productBean) throws Exception {
		Date nowDate = new Date();
		QyjProductEntity productEntity = new QyjProductEntity();
		BeanUtils.copyProperties(productBean, productEntity);
		
		productEntity.setUpdateUser(sysUserBean.getId());
		productEntity.setUpdateTime(nowDate);
		
		String todayDir = new SimpleDateFormat("yyyyMMdd").format(nowDate);
		// 图片保存文件夹地址
		String fileDirPath = Utils.getUploadFilePath() + File.separator;
		// 相对文件保存地址的路径
		String relativeFileDirPath = "product" + File.separator + todayDir + File.separator;
		FileUtils.mkDirs(fileDirPath + relativeFileDirPath);
		
		QyjFileInfoEntity fileInfo = null;
		// id为空插入数据
		if (productEntity.getId() == null || productEntity.getId() == 0) {
			productEntity.setCreateTime(nowDate);
			productEntity.setCreateUser(sysUserBean.getId());
			productEntity.setSoldNumber(0);
			productEntity.setUnpayNumber(0);
			productEntity.setProductStatus(ProductStatusEnum.PUBLISH.toString());
			
			int insertResult = this.insert(productEntity);
			logger.info("saveAllProductInfo insert productEntity, info={}, insertResult={}", productEntity.toString(), insertResult);
			
			if (insertResult <= 0) {
				throw new Exception("新增产品信息失败！");
			}
		} else {
			// 编辑更新数据
			productEntity.setUpdateTime(nowDate);
			int updateResult = this.updateByPrimaryKey(productEntity);
			logger.info("saveAllProductInfo update productEntity, info={}, result={}", productEntity.toString(),
					updateResult);
		}
		
	}
	
	/**
	 * 根据产品id删除产品信息（包括产品、产品详情、产品图片）
	 * @param productId
	 * @return
	 */
	@Override
	public int deleteProductInfo(Long productId) throws Exception {
		// 查询产品
		QyjProductEntity product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			throw new Exception("id为" + productId + "的产品不存在！");
		}
		
		if (!ProductStatusEnum.PUBLISH.toString().equals(product.getProductStatus())) {
			throw new Exception("产品" + productId + "不是发布状态不能删除！");
		}
				
		// 删除产品详情
		productDetailMapper.delProductDetailByProductId(productId);
		// 查找文件信息
		List<QyjFileInfoEntity> fileInfoList = fileInfoMapper.listFileInfoByItemId(productId);
		if (fileInfoList != null && !fileInfoList.isEmpty()) {
			for (QyjFileInfoEntity fileInfo : fileInfoList) {
				// 删除文件
				File file = new File(Utils.getUploadFilePath() + File.separator + fileInfo.getFilePath());
				if (file.exists()) {
					file.delete();
				}
			}
			// 删除文件信息
			fileInfoMapper.delFileByItemId(productId);
		}
		
		// if (!StringUtils.isEmpty(product.getImgUrl())) {
		// 	// 删除文件产品列表展示图片文件
		// 	File file = new File(Utils.getUploadFilePath() + File.separator + product.getImgUrl());
		// 	if (file.exists()) {
		// 		file.delete();
		// 	}
		// }
		
		// 删除产品
		return productMapper.deleteByPrimaryKey(productId);
	}
	
	/**
	 * 根据产品id获取产品信息
	 * @param productId
	 * @return
	 */
	@Override
	public QyjProductBean selectProductInfo(Long productId) throws Exception {
		// 查询产品信息
		QyjProductEntity productEntity = productMapper.selectByPrimaryKey(productId);
		if (productEntity == null) {
			throw new Exception("不存在产品，产品id：" + productId);
		}
		
		QyjProductBean productBean = new QyjProductBean();
		BeanUtils.copyProperties(productEntity, productBean);

		return productBean;
	}
	
	/**
	 * 更新产品状态
	 * @param userBean
	 * @param productId
	 * @param productStatus
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean updateProductStatus(SysUserBean userBean, Long productId, String productStatus) throws Exception {
		if (productId == null) {
			throw new Exception("产品id不能为空！");
		}
		if (StringUtils.isEmpty(productStatus)) {
			throw new Exception("更新的产品状态不能为空！");
		}
		
		// 查询产品
		QyjProductEntity product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			throw new Exception("不存在产品id为" + productId + "的产品！");
		}
		
		// 上架
		if (ProductStatusEnum.PUTAWAY.toString().equals(productStatus)) {
			if (ProductStatusEnum.PUBLISH.toString().equals(product.getProductStatus())) {
				Date curDate = new Date();
				QyjProductEntity updateProduct = new QyjProductEntity();
				updateProduct.setId(productId);
				updateProduct.setUpdateTime(curDate);
				updateProduct.setUpdateUser(userBean.getId());
				updateProduct.setProductStatus(productStatus);
				updateProduct.setVersion(product.getVersion());
				// 上架时间
				// updateProduct.setPutawayTime(curDate);
				int updateResult = productMapper.updateByPrimaryKeySelective(updateProduct);
				
				if (updateResult == 1) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
			if (ProductStatusEnum.PUTAWAY.toString().equals(product.getProductStatus())) {
				throw new Exception("产品" + productId + "已经上架！");
			}
			if (ProductStatusEnum.SOLDOUT.toString().equals(product.getProductStatus())) {
				throw new Exception("产品" + productId + "已经下架！");
			}
			throw new Exception("产品" + productId + "更新上架状态失败！");
		}
		
		// 下架
		if (ProductStatusEnum.SOLDOUT.toString().equals(productStatus)) {
			if (ProductStatusEnum.PUBLISH.toString().equals(product.getProductStatus())) {
				throw new Exception("产品" + productId + "还没有上架！");
			}
			if (ProductStatusEnum.PUTAWAY.toString().equals(product.getProductStatus())) {
				Date curDate = new Date();
				QyjProductEntity updateProduct = new QyjProductEntity();
				updateProduct.setId(productId);
				updateProduct.setUpdateTime(curDate);
				updateProduct.setUpdateUser(userBean.getId());
				updateProduct.setProductStatus(productStatus);
				updateProduct.setVersion(product.getVersion());
				// 下架时间
				// updateProduct.setSoldoutTime(curDate);
				int updateResult = productMapper.updateByPrimaryKeySelective(updateProduct);
				
				if (updateResult == 1) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
			if (ProductStatusEnum.SOLDOUT.toString().equals(product.getProductStatus())) {
				throw new Exception("产品" + productId + "已经下架！");
			}
			throw new Exception("产品" + productId + "更新下架状态失败！");
		}
		
		throw new Exception("产品" + productId + "更新状态" + productStatus + "失败！");
	}

	/**
	 * 构造更新数量的产品实体
	 * @param productId
	 * @param number
	 * @param soldNumber
	 * @param unpayNumber
	 * @return
	 */
	@Override
	public QyjProductEntity getUpdateNumberProductEntity(Long productId, int number, int soldNumber, int unpayNumber) {
		QyjProductEntity productEntity = new QyjProductEntity();
		productEntity.setId(productId);
		productEntity.setNumber(number);
		productEntity.setSoldNumber(soldNumber);
		productEntity.setUnpayNumber(unpayNumber);
		return productEntity;
	}
}
