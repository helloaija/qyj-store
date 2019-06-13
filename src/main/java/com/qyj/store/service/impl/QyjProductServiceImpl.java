package com.qyj.store.service.impl;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.common.enums.CommonEnums.ProductStatusEnum;
import com.qyj.store.dao.QyjProductMapper;
import com.qyj.store.dao.QyjStockProductMapper;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.entity.QyjStockProductEntity;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.vo.QyjProductBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品表服务层接口
 * @author CTF_stone
 */
@Service
public class QyjProductServiceImpl implements QyjProductService {

    private static final Logger logger = LoggerFactory.getLogger(QyjProductServiceImpl.class);

    private QyjProductMapper productMapper;
    private QyjStockProductMapper stockProductMapper;

    /**
     * 获取产品分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public PageBean listProjectPage(PageParam pageParam, Map<String, Object> paramMap) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
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
     * 根据产品id获取产品信息
     * @param productId
     * @return
     */
    @Override
    public QyjProductBean selectProductInfo(Long productId) throws Exception {
        // 查询产品信息
        QyjProductEntity productEntity = productMapper.selectByPrimaryKey(productId);
        if (productEntity == null) {
            throw new ValidationException("不存在产品，产品id：" + productId);
        }

        QyjProductBean productBean = new QyjProductBean();
        BeanUtils.copyProperties(productEntity, productBean);

        return productBean;
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

    /**
     * 假删除产品
     * @param productId
     * @param userId
     * @return
     */
    @Override
    public ResultBean deleteProduct(Long productId, Long userId) {
        if (productId == null) {
            return new ResultBean("0000", "删除的产品ID为空");
        }
        int result = productMapper.deleteProduct(productId, userId);

        if (result >= 1) {
            return new ResultBean("0000", "删除成功");
        }

        return new ResultBean("0000", "删除失败");
    }

    /**
     * 插入产品信息
     * @param record
     * @return
     */
    @Override
    public ResultBean insertProduct(QyjProductEntity record) {
        record.setNumber(0);
        record.setSoldNumber(0);
        record.setUnpayNumber(0);
        record.setProductStatus(ProductStatusEnum.PUBLISH.toString());

        Map<String, Object> paramMap = new HashMap<>();
        PageParam pageParam = new PageParam();
        pageParam.setQueryCondition(" and title = '" + record.getTitle() + "'");
        paramMap.put("pageParam", pageParam);

        if (productMapper.countProduct(paramMap) >= 1) {
            return new ResultBean("0001", "产品名称已经存在");
        }

        logger.info("saveProductInfo insert productEntity, info={}, result={}", record.toString());

        if (productMapper.insert(record) == 1) {
            return new ResultBean("0000", "新增产品信息成功", record);
        }

        return new ResultBean("0002", "新增产品信息失败");
    }

    /**
     * 根据产品id更新产品信息
     * @param record
     * @return
     */
    @Override
    public ResultBean updateProduct(QyjProductEntity record) {
        Map<String, Object> paramMap = new HashMap<>();
        PageParam pageParam = new PageParam();
        pageParam.setQueryCondition(" and id <> '" + record.getId() + "'and title = '" + record.getTitle() + "'");
        paramMap.put("pageParam", pageParam);

        if (productMapper.countProduct(paramMap) >= 1) {
            return new ResultBean("0001", "产品名称已经存在");
        }

        productMapper.updateProductEdit(record);

        return new ResultBean("0000", "更新产品信息成功", record);
    }

    /**
     * 获取产品库存明细数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public ResultBean listStoreDetail(PageParam pageParam, Map<String, Object> paramMap) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        // 统计产品数量
        int totalCount = productMapper.countProduct(paramMap);

        if (totalCount <= 0) {
            Map<String, Object> countData = new HashMap<>();
            countData.put("pageBean", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), 0, null));
            return new ResultBean("0000", "请求成功", countData);
        }

        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        paramMap.put("pageParam", pageParam);

        // 获取产品分页数据列表
        List<QyjProductEntity> projectList = productMapper.listProduct(paramMap);

        List<Long> productIdList = new ArrayList<>();
        for (QyjProductEntity product : projectList) {
            productIdList.add(product.getId());
        }
        // 获取最新进货数据
        List<QyjStockProductEntity> stockList = stockProductMapper.listProductNewStock(productIdList);
        Map<Long, BigDecimal> stockPriceMap = new HashMap<>();
        for (QyjStockProductEntity stock : stockList) {
            stockPriceMap.put(stock.getProductId(), stock.getPrice());
        }

        List<QyjProductBean> beanList = new ArrayList<>();
        for (QyjProductEntity entity : projectList) {
            QyjProductBean bean = new QyjProductBean();
            BeanUtils.copyProperties(entity, bean);
            bean.setStockPrice(stockPriceMap.get(bean.getId()));
            beanList.add(bean);
        }

        // 获取库存统计数据，storeAmount（库存总额） hasStoreNum（库存不为0数量） noStoreNum（库存为零数量）
        Map<String, Object> countData = productMapper.countStoreData(paramMap);
        countData.put("pageBean", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, beanList));

        return new ResultBean("0000", "请求成功", countData);
    }

    @Autowired
    public void setProductMapper(QyjProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Autowired
    public void setStockProductMapper(QyjStockProductMapper stockProductMapper) {
        this.stockProductMapper = stockProductMapper;
    }
}
