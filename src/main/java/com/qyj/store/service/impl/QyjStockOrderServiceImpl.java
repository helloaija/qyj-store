package com.qyj.store.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qyj.common.exception.ValidException;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.enums.CommonEnums;
import com.qyj.store.common.util.Utils;
import com.qyj.store.dao.QyjProductMapper;
import com.qyj.store.dao.QyjSellProductMapper;
import com.qyj.store.dao.QyjStockOrderMapper;
import com.qyj.store.dao.QyjStockProductMapper;
import com.qyj.store.entity.*;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.service.QyjStockOrderService;
import com.qyj.store.vo.app.SellOrderBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务层接口实现-我的订单
 * @author CTF_stone
 */
@Service
public class QyjStockOrderServiceImpl implements QyjStockOrderService {

    private static Logger logger = LoggerFactory.getLogger(QyjStockOrderServiceImpl.class);

    private QyjStockOrderMapper stockOrderMapper;
    private QyjStockProductMapper stockProductMapper;
    private QyjProductMapper productMapper;
    private QyjProductService productService;
    private QyjSellProductMapper sellProductMapper;

    /**
     * 获取订单分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public ResultBean listStockOrderAndProductPage(PageParam pageParam, Map<String, Object> paramMap) {
        ResultBean resultBean = new ResultBean();

        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        // 统计订单信息
        Map<String, Object> countMap = stockOrderMapper.countStockOrder(paramMap);
        logger.info("listOrderPage paramMap:{}, countMap:{}", paramMap, countMap);

        int totalCount = Integer.parseInt(String.valueOf(countMap.get("totalCount")));
        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        if (totalCount <= 0) {
            countMap.put("pageBean", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, new ArrayList<>()));
            return resultBean.init("0000", "请求成功", countMap);
        }

        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        paramMap.put("pageParam", pageParam);

        // 获取分页数据列表
        List<QyjStockOrderEntity> projectList = stockOrderMapper.listStockOrderAndProduct(paramMap);

        countMap.put("pageBean", new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, projectList));
        return resultBean.init("0000", "请求成功", countMap);
    }

    /**
     * 获取订单分页数据 app调用
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public ResultBean listStockOrder(PageParam pageParam, Map<String, Object> paramMap) {
        ResultBean resultBean = new ResultBean();

        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }

        paramMap.put("pageParam", pageParam);
        // 订单统计信息
        Map<String, Object> countMap = stockOrderMapper.countStockOrder(paramMap);
        logger.info("listStockOrder paramMap:{}, countMap:{}", paramMap, countMap);

        int totalCount = Integer.parseInt(String.valueOf(countMap.get("totalCount")));
        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        if (totalCount <= 0) {
            return resultBean.init("0000", "请求成功",
                    new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, new ArrayList<>()));
        }

        // 获取分页数据列表
        List<QyjStockOrderEntity> entityList = stockOrderMapper.listStockOrderAndProduct(paramMap);

        return resultBean.init("0000", "请求成功",
                new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, entityList));
    }

    /**
     * 添加进货订单
     * @param stockOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean addStockOrder(QyjStockOrderEntity stockOrder) throws Exception {
        ResultBean resultBean = new ResultBean();

        // 校验
        if (!this.viladStockOrderAndSetValue(stockOrder, resultBean)) {
            return resultBean;
        }

        stockOrder.setOrderNumber(Utils.getCurrentUid(5));
        stockOrder.setCreateTime(new Date());
        stockOrder.setUpdateTime(new Date());

        // 插入订单
        int insertNum = stockOrderMapper.insertStockOrder(stockOrder);
        if (insertNum != 1) {
            return resultBean.init("0002", "添加进货单失败");
        }

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        List<QyjStockProductEntity> stockProductEntityList = stockOrder.getStockProductList();
        for (QyjStockProductEntity stockProductEntity : stockProductEntityList) {
            // 设置产品关联订单
            stockProductEntity.setStockId(stockOrder.getId());
            stockProductEntity.setCreateTime(new Date());

            QyjProductEntity productEntity = new QyjProductEntity();
            productEntity.setId(stockProductEntity.getProductId());
            productEntity.setNumber(stockProductEntity.getNumber());
            productEntity.setSoldNumber(0);
            productEntity.setUnpayNumber(0);
            productEntityList.add(productEntity);
        }

        // 插入订单产品
        insertNum = stockProductMapper.insertStockProductList(stockOrder.getStockProductList());
        if (insertNum < 1) {
            throw new ValidException("添加进货单产品失败");
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        return resultBean.init("0000", "添加进货单成功");
    }

    /**
     * 添加进货订单-app接口
     * @param stockOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean addStockOrderAndReturn(QyjStockOrderEntity stockOrder) throws Exception {
        ResultBean resultBean = new ResultBean();

        // 校验
        if (!this.viladStockOrderAndSetValue(stockOrder, resultBean)) {
            return resultBean;
        }

        stockOrder.setOrderNumber(Utils.getCurrentUid(5));
        stockOrder.setCreateTime(new Date());
        stockOrder.setUpdateTime(new Date());

        // 插入订单
        int insertNum = stockOrderMapper.insertStockOrder(stockOrder);
        if (insertNum != 1) {
            return resultBean.init("0002", "添加进货单失败");
        }

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        List<QyjStockProductEntity> stockProductEntityList = stockOrder.getStockProductList();
        for (QyjStockProductEntity stockProductEntity : stockProductEntityList) {
            // 设置产品关联订单
            stockProductEntity.setStockId(stockOrder.getId());
            stockProductEntity.setCreateTime(new Date());

            QyjProductEntity productEntity = new QyjProductEntity();
            productEntity.setId(stockProductEntity.getProductId());
            productEntity.setNumber(stockProductEntity.getNumber());
            productEntity.setSoldNumber(0);
            productEntity.setUnpayNumber(0);
            productEntityList.add(productEntity);
        }

        // 插入订单产品
        insertNum = stockProductMapper.insertStockProductList(stockOrder.getStockProductList());
        if (insertNum < 1) {
            throw new ValidException("添加进货单产品失败");
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        QyjStockOrderEntity stockOrderEntity = stockOrderMapper.getStockOrderAndProductByStockId(stockOrder.getId());

        return resultBean.init("0000", "添加进货单成功", stockOrderEntity);
    }

    /**
     * 获取进货单数据
     * @param stockId
     * @return
     */
    @Override
    public ResultBean loadStockOrderAndProductByStockId(Long stockId) {
        if (stockId == null) {
            return new ResultBean("0002", "订单id为空");
        }
        QyjStockOrderEntity stockOrderEntity = stockOrderMapper.getStockOrderAndProductByStockId(stockId);
        if (stockOrderEntity == null) {
            return new ResultBean("0002", "获取不到订单");
        }
        return new ResultBean("0000", "获取数据成功", stockOrderEntity);
    }

    /**
     * 添加进货订单
     * @param stockOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean editStockOrder(QyjStockOrderEntity stockOrder) throws Exception {
        ResultBean resultBean = new ResultBean();
        // 校验
        if (!this.viladStockOrderAndSetValue(stockOrder, resultBean)) {
            return resultBean;
        }

        stockOrder.setUpdateTime(new Date());

        // 更新订单
        stockOrderMapper.updateStockOrder(stockOrder);

        // 获取之前的进货单产品
        List<QyjStockProductEntity> oldStockProductList = stockProductMapper.listStockProductByStockId(stockOrder.getId());

        List<QyjStockProductEntity> addList = new ArrayList<>();
        List<QyjStockProductEntity> updateList = new ArrayList<>();
        List<Long> deleteList = new ArrayList<>();

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        Set<Long> stockProductIdSet = new HashSet<>();
        List<QyjStockProductEntity> stockProductEntityList = stockOrder.getStockProductList();
        // 对页面上编辑的产品分类，没有id的就是新增，有id就是编辑
        for (QyjStockProductEntity stockProductEntity : stockProductEntityList) {
            stockProductIdSet.add(stockProductEntity.getId());
            if (stockProductEntity.getId() == null) {
                stockProductEntity.setStockId(stockOrder.getId());
                stockProductEntity.setCreateTime(new Date());
                addList.add(stockProductEntity);
            } else {
                updateList.add(stockProductEntity);
            }

            // 新增的和更新的库存都加，旧的都减
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(stockProductEntity.getProductId(),
                    stockProductEntity.getNumber(), 0, 0);
            productEntityList.add(productEntity);
        }

        for (QyjStockProductEntity stockProductEntity : oldStockProductList) {
            if (!stockProductIdSet.contains(stockProductEntity.getId())) {
                deleteList.add(stockProductEntity.getId());
            }

            // 旧的不管是更新的还是删除的都库存减，因为上面的新增和更新都库存加
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(
                    stockProductEntity.getProductId(), -1 * stockProductEntity.getNumber(), 0, 0);
            productEntityList.add(productEntity);
        }

        if (!addList.isEmpty()) {
            stockProductMapper.insertStockProductList(addList);
        }
        if (!updateList.isEmpty()) {
            stockProductMapper.updateStockProductList(updateList);
        }
        if (!deleteList.isEmpty()) {
            stockProductMapper.deleteStockProductList(deleteList);
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        return resultBean.init("0000", "更新进货单成功");
    }

    /**
     * 添加进货订单-app接口
     * @param stockOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean editStockOrderAndReturn(QyjStockOrderEntity stockOrder) {
        ResultBean resultBean = new ResultBean();
        // 校验
        if (!this.viladStockOrderAndSetValue(stockOrder, resultBean)) {
            return resultBean;
        }

        stockOrder.setUpdateTime(new Date());

        // 更新订单
        stockOrderMapper.updateStockOrder(stockOrder);

        // 获取之前的进货单产品
        List<QyjStockProductEntity> oldStockProductList = stockProductMapper.listStockProductByStockId(stockOrder.getId());

        List<QyjStockProductEntity> addList = new ArrayList<>();
        List<QyjStockProductEntity> updateList = new ArrayList<>();
        List<Long> deleteList = new ArrayList<>();

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        Set<Long> stockProductIdSet = new HashSet<>();
        List<QyjStockProductEntity> stockProductEntityList = stockOrder.getStockProductList();
        // 对页面上编辑的产品分类，没有id的就是新增，有id就是编辑
        for (QyjStockProductEntity stockProductEntity : stockProductEntityList) {
            stockProductIdSet.add(stockProductEntity.getId());
            if (stockProductEntity.getId() == null) {
                stockProductEntity.setStockId(stockOrder.getId());
                stockProductEntity.setCreateTime(new Date());
                addList.add(stockProductEntity);
            } else {
                updateList.add(stockProductEntity);
            }

            // 新增的和更新的库存都加，旧的都减
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(stockProductEntity.getProductId(),
                    stockProductEntity.getNumber(), 0, 0);
            productEntityList.add(productEntity);
        }

        for (QyjStockProductEntity stockProductEntity : oldStockProductList) {
            if (!stockProductIdSet.contains(stockProductEntity.getId())) {
                deleteList.add(stockProductEntity.getId());
            }

            // 旧的不管是更新的还是删除的都库存减，因为上面的新增和更新都库存加
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(
                    stockProductEntity.getProductId(), -1 * stockProductEntity.getNumber(), 0, 0);
            productEntityList.add(productEntity);
        }

        if (!addList.isEmpty()) {
            stockProductMapper.insertStockProductList(addList);
        }
        if (!updateList.isEmpty()) {
            stockProductMapper.updateStockProductList(updateList);
        }
        if (!deleteList.isEmpty()) {
            stockProductMapper.deleteStockProductList(deleteList);
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        QyjStockOrderEntity stockOrderEntity = stockOrderMapper.getStockOrderAndProductByStockId(stockOrder.getId());

        return resultBean.init("0000", "更新进货单成功", stockOrderEntity);
    }

    /**
     * 根据stockId删除进货单
     * @param stockId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean deleteStockOrder(Long stockId) throws Exception {
        ResultBean resultBean = new ResultBean();
        if (stockId == null) {
            return resultBean.init("0002", "进货单ID为空");
        }

        int optNum = stockOrderMapper.deleteStockOrder(stockId);
        if (optNum == 0) {
            return resultBean.init("0002", "删除进货单失败，ID=" + stockId);
        }

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        // 查询进货单下的产品
        List<QyjStockProductEntity> stockProductList = stockProductMapper.listStockProductByStockId(stockId);
        for (QyjStockProductEntity stockProductEntity : stockProductList) {
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(stockProductEntity.getProductId(),
                    -1 * stockProductEntity.getNumber(), 0, 0);
            productEntityList.add(productEntity);
        }

       optNum = stockProductMapper.deleteStockProductByStockId(stockId);
        if (optNum == 0) {
            throw new ValidException("删除进货单产品失败");
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        return resultBean.init("0000", "进货单删除成功");
    }

    /**
     * 校验进货单信息
     * @param stockOrder
     * @param resultBean
     * @return
     */
    private boolean viladStockOrderAndSetValue(QyjStockOrderEntity stockOrder, ResultBean resultBean) {
        if (stockOrder == null) {
            resultBean.init("0002", "进货单为空");
            return false;
        }
        if (stockOrder.getOrderAmount() == null) {
            resultBean.init("0002", "订单金额不能为空");
            return false;
        }
        if (stockOrder.getOrderTime() == null) {
            resultBean.init("0002", "进货时间不能为空");
            return false;
        }
        if (stockOrder.getHasPayAmount() == null) {
            resultBean.init("0002", "已支付金额不能为空");
            return false;
        }

        List<QyjStockProductEntity> stockProductEntityList = stockOrder.getStockProductList();

        // 订单总金额 = 各个产品售价 × 数量 之和
        BigDecimal orderAmount = BigDecimal.ZERO;
        Set<Long> idSet = new HashSet<Long>();

        Iterator<QyjStockProductEntity> it = stockProductEntityList.iterator();
        while(it.hasNext()) {
            QyjStockProductEntity stockProductEntity = it.next();
            if (stockProductEntity.getProductId() == null) {
                it.remove();
                continue;
            }
            orderAmount = orderAmount.add(stockProductEntity.getPrice().multiply(BigDecimal.valueOf(stockProductEntity.getNumber())));
            if (stockProductEntity.getProductId() == null || StringUtils.isEmpty(stockProductEntity.getProductTitle())) {
                resultBean.init("0002", "产品信息不能为空");
                return false;
            }
            if (stockProductEntity.getNumber() == null || stockProductEntity.getNumber().intValue() <= 0) {
                resultBean.init("0002", "产品数量不正确");
                return false;
            }
            if (stockProductEntity.getPrice() == null || stockProductEntity.getPrice().doubleValue() < 0) {
                resultBean.init("0002", "产品金额不正确");
                return false;
            }
            if (idSet.contains(stockProductEntity.getProductId())) {
                resultBean.init("0002", "产品信息存在重复[" + stockProductEntity.getProductTitle() + "]");
                return false;
            }
            idSet.add(stockProductEntity.getProductId());
        }

        if (stockProductEntityList == null || stockProductEntityList.isEmpty()) {
            resultBean.init("0002", "进货单产品不能为空");
            return false;
        }

        stockOrder.setOrderAmount(orderAmount);
        int comVal = stockOrder.getHasPayAmount().compareTo(orderAmount);
        if (comVal != -1) {
            // 支付完
            stockOrder.setOrderStatus(CommonEnums.OrderStatusEnum.HASPAYALL.toString());
        } else {
            if (stockOrder.getHasPayAmount().doubleValue() == 0) {
                // 一分钱都没支付
                stockOrder.setOrderStatus(CommonEnums.OrderStatusEnum.UNPAY.toString());
            } else {
                // 未支付完
                stockOrder.setOrderStatus(CommonEnums.OrderStatusEnum.UNPAYALL.toString());
            }
        }

        if (stockOrder.getPayTime() == null &&
                !CommonEnums.OrderStatusEnum.UNPAY.toString().equals(stockOrder.getOrderStatus())) {
            resultBean.init("0002", "支付时间不能为空");
            return false;
        }

        return true;
    }

    /**
     * 加载产品分页信息，用于选择展示产品信息，加入进货价
     * @param pageParam
     * @param paramMap
     * @return
     */
    @Override
    public ResultBean listProductStockInfo(PageParam pageParam, Map<String, Object> paramMap) {
        // 获取产品分页数据
        PageBean pageBean = this.productService.listProjectPage(pageParam, paramMap);
        List<QyjProductEntity> recordList = (List<QyjProductEntity>) pageBean.getRecordList();

        if (recordList != null && !recordList.isEmpty()) {
            StringBuilder productIdSb = new StringBuilder();
            for (QyjProductEntity entity: recordList) {
                if (productIdSb.length() > 0) {
                    productIdSb.append(",");
                }
                productIdSb.append(entity.getId());
            }

            // 获取产品进货价， 并把进货价加入到产品信息中
            Map<Long, String> stockPricesMap = new HashMap<>();
            List<Map<String, Object>> pricesMapList = stockProductMapper.listProductStockPrice(productIdSb.toString());
            if (pricesMapList != null && !pricesMapList.isEmpty()) {
                for (Map<String, Object> map : pricesMapList) {
                    stockPricesMap.put(Long.parseLong(String.valueOf(map.get("productId"))), String.valueOf(map.get("prices")));
                }
            }

            Map<Long, BigDecimal> sellPricesMap = new HashMap<>();
            if (paramMap.get("userId") != null) {
                Long userId = (Long) paramMap.get("userId");
                Map<String, Object> sellParamMap = new HashMap<>();
                sellParamMap.put("userId", userId);
                sellParamMap.put("productIds", productIdSb.toString());
                // 获取用户对应产品最后购买价格
                List<QyjSellProductEntity> sellProductList = sellProductMapper.listLastSellPrice(sellParamMap);
                for (QyjSellProductEntity entity : sellProductList) {
                    sellPricesMap.put(entity.getProductId(), entity.getPrice());
                }
            }

            List<JSONObject> recordJsonList = new ArrayList<>();
            for (QyjProductEntity entity: recordList) {
                if (sellPricesMap.containsKey(entity.getId())) {
                    entity.setPrice(sellPricesMap.get(entity.getId()));
                }
                JSONObject json = (JSONObject) JSON.toJSON(entity);
                String prices = stockPricesMap.get(entity.getId());
                if (StringUtils.isEmpty(prices)) {
                    json.put("stockPrices", new String[]{});
                } else {
                    json.put("stockPrices", prices.split(";"));
                }
                recordJsonList.add(json);
            }

            pageBean.setRecordList(recordJsonList);


        }
        return new ResultBean("0000", "请求成功", pageBean);
    }

    @Autowired
    public void setStockOrderMapper(QyjStockOrderMapper stockOrderMapper) {
        this.stockOrderMapper = stockOrderMapper;
    }

    @Autowired
    public void setStockProductMapper(QyjStockProductMapper stockProductMapper) {
        this.stockProductMapper = stockProductMapper;
    }

    @Autowired
    public void setProductMapper(QyjProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Autowired
    public void setProductService(QyjProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setSellProductMapper(QyjSellProductMapper sellProductMapper) {
        this.sellProductMapper = sellProductMapper;
    }
}
