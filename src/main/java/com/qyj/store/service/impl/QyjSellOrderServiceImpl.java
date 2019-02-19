package com.qyj.store.service.impl;

import com.qyj.common.exception.ValidException;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.util.Utils;
import com.qyj.store.dao.QyjProductMapper;
import com.qyj.store.dao.QyjSellOrderMapper;
import com.qyj.store.dao.QyjSellProductMapper;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.entity.QyjSellOrderEntity;
import com.qyj.store.entity.QyjSellProductEntity;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.service.QyjSellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务层接口实现-我的订单
 * @author CTF_stone
 */
@Service
public class QyjSellOrderServiceImpl implements QyjSellOrderService {

    private static Logger logger = LoggerFactory.getLogger(QyjSellOrderServiceImpl.class);

    private QyjSellOrderMapper sellOrderMapper;
    private QyjSellProductMapper sellProductMapper;
    private QyjProductMapper productMapper;
    private QyjProductService productService;

    /**
     * 锁-编辑销售单
     */
    private static final Lock LOCK_SELLORDER = new ReentrantLock();

    /**
     * 获取订单分页数据
     * @param pageParam 分页信息
     * @param paramMap  查询参数
     * @return
     * @throws Exception
     */
    @Override
    public PageBean listSellOrderAndProductPage(PageParam pageParam, Map<String, Object> paramMap) throws Exception {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        if (paramMap == null) {
            paramMap = new HashMap<String, Object>();
        }
        // 订单数量
        int totalCount = sellOrderMapper.countSellOrder(paramMap);
        logger.info("listOrderPage paramMap:{}, totalCount:{}", paramMap, totalCount);

        if (totalCount <= 0) {
            return new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), 0, null);
        }

        pageParam.setTotalCount(totalCount);
        // 计算分页信息
        pageParam.splitPageInstance();

        paramMap.put("pageParam", pageParam);

        // 获取分页数据列表
        List<QyjSellOrderEntity> projectList = sellOrderMapper.listSellOrderAndProduct(paramMap);

        return new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), totalCount, projectList);
    }

    /**
     * 添加销售订单
     * @param sellOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean addSellOrder(QyjSellOrderEntity sellOrder) throws Exception {
        ResultBean resultBean = new ResultBean();

        // 校验
        if (!this.viladSellOrder(sellOrder, resultBean)) {
            return resultBean;
        }

        sellOrder.setOrderNumber(Utils.getCurrentUid(5));
        sellOrder.setCreateTime(new Date());
        sellOrder.setUpdateTime(new Date());

        // 插入订单
        int insertNum = sellOrderMapper.insertSellOrder(sellOrder);
        if (insertNum != 1) {
            return resultBean.init("0002", "添加销售单失败");
        }

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        List<QyjSellProductEntity> sellProductEntityList = sellOrder.getSellProductList();
        for (QyjSellProductEntity SellProductEntity : sellProductEntityList) {
            // 设置产品关联订单
            SellProductEntity.setSellId(sellOrder.getId());
            SellProductEntity.setCreateTime(new Date());

            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(SellProductEntity.getProductId(),
                    -1 * SellProductEntity.getNumber(), SellProductEntity.getNumber(), 0);
            productEntityList.add(productEntity);
        }

        // 插入订单产品
        insertNum = sellProductMapper.insertSellProductList(sellOrder.getSellProductList());
        if (insertNum < 1) {
            throw new ValidException("添加销售单产品失败");
        }

        if (LOCK_SELLORDER.tryLock()) {
            try {
                // 更新库存数量
                productMapper.updateProductNumberBatch(productEntityList);
                // 更新库存数据后校验库存数据，如果存在库存数量为空则回滚
                this.validSellProduct(sellProductEntityList);
            } finally {
                LOCK_SELLORDER.unlock();
            }
        }

        return resultBean.init("0000", "添加销售单成功");
    }

    /**
     * 获取销售单数据
     * @param sellId
     * @return
     */
    @Override
    public ResultBean loadSellOrderAndProductBySellId(Long sellId) {
        if (sellId == null) {
            return new ResultBean("0002", "订单id为空");
        }
        QyjSellOrderEntity SellOrderEntity = sellOrderMapper.getSellOrderAndProductBySellId(sellId);
        if (SellOrderEntity == null) {
            return new ResultBean("0002", "获取不到订单");
        }
        return new ResultBean("0000", "获取数据成功", SellOrderEntity);
    }

    /**
     * 添加销售订单
     * @param sellOrder
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean editSellOrder(QyjSellOrderEntity sellOrder) throws Exception {
        ResultBean resultBean = new ResultBean();
        // 校验
        if (!this.viladSellOrder(sellOrder, resultBean)) {
            return resultBean;
        }

        sellOrder.setUpdateTime(new Date());

        // 更新订单
        sellOrderMapper.updateSellOrder(sellOrder);

        // 获取之前的销售单产品
        List<QyjSellProductEntity> oldSellProductList = sellProductMapper.listSellProductBySellId(sellOrder.getId());

        List<QyjSellProductEntity> addList = new ArrayList<>();
        List<QyjSellProductEntity> updateList = new ArrayList<>();
        List<Long> deleteList = new ArrayList<>();

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        Map<Long, QyjSellProductEntity> updateSellProductMap = new HashMap<>();
        List<QyjSellProductEntity> sellProductEntityList = sellOrder.getSellProductList();

        for (QyjSellProductEntity sellProductEntity : sellProductEntityList) {
            if (sellProductEntity.getId() == null) {
                // 对页面上编辑的产品，没有id的就是新增
                sellProductEntity.setSellId(sellOrder.getId());
                sellProductEntity.setCreateTime(new Date());
                addList.add(sellProductEntity);

                // 新增的，库存减，已售加
                QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(sellProductEntity.getProductId(),
                        -1 * sellProductEntity.getNumber(), sellProductEntity.getNumber(), 0);
                productEntityList.add(productEntity);
            } else {
                // 对页面上编辑的产品，有id的就是更新
                updateSellProductMap.put(sellProductEntity.getId(), sellProductEntity);
            }
        }

        for (QyjSellProductEntity sellProductEntity : oldSellProductList) {
            if (updateSellProductMap.containsKey(sellProductEntity.getId())) {
                // 旧的产品存在于更新的产品，就是需要更新的
                updateList.add(updateSellProductMap.get(sellProductEntity.getId()));
                // 更新的，库存加旧值、减新值，已售减旧值加新值
                int number = sellProductEntity.getNumber() - updateSellProductMap.get(sellProductEntity.getId()).getNumber();
                QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(
                        sellProductEntity.getProductId(), number, -1 * number, 0);
                productEntityList.add(productEntity);
            } else {
                // 旧的产品不存在于更新的产品，就是需要删除的
                deleteList.add(sellProductEntity.getId());
                // 删除的，库存加，已售减
                QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(
                        sellProductEntity.getProductId(), sellProductEntity.getNumber(), -1 * sellProductEntity.getNumber(), 0);
                productEntityList.add(productEntity);
            }
        }

        if (!addList.isEmpty()) {
            sellProductMapper.insertSellProductList(addList);
        }
        if (!updateList.isEmpty()) {
            sellProductMapper.updateSellProductList(updateList);
        }
        if (!deleteList.isEmpty()) {
            sellProductMapper.deleteSellProductList(deleteList);
        }
        if (LOCK_SELLORDER.tryLock()) {
            try {
                // 更新库存数量
                productMapper.updateProductNumberBatch(productEntityList);
                // 更新库存数据后校验库存数据，如果存在库存数量为空则回滚
                this.validSellProduct(sellProductEntityList);
            } finally {
                LOCK_SELLORDER.unlock();
            }
        }

        return resultBean.init("0000", "更新销售单成功");
    }

    /**
     * 根据SellId删除销售单
     * @param sellId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public ResultBean deleteSellOrder(Long sellId) throws Exception {
        ResultBean resultBean = new ResultBean();
        if (sellId == null) {
            return resultBean.init("0002", "销售单ID为空");
        }

        int optNum = sellOrderMapper.deleteSellOrder(sellId);
        if (optNum == 0) {
            return resultBean.init("0002", "删除销售单失败，ID=" + sellId);
        }

        // 获取订单的产品数量，用来更新产品储量
        List<QyjProductEntity> productEntityList = new ArrayList<>();

        // 查询销售单下的产品
        List<QyjSellProductEntity> SellProductList = sellProductMapper.listSellProductBySellId(sellId);
        for (QyjSellProductEntity SellProductEntity : SellProductList) {
            QyjProductEntity productEntity = productService.getUpdateNumberProductEntity(SellProductEntity.getProductId(),
                    1 * SellProductEntity.getNumber(), -1 * SellProductEntity.getNumber(), 0);
            productEntityList.add(productEntity);
        }

        optNum = sellProductMapper.deleteSellProductBySellId(sellId);
        if (optNum == 0) {
            throw new ValidException("删除销售单产品失败");
        }

        // 更新库存数量
        productMapper.updateProductNumberBatch(productEntityList);

        return resultBean.init("0000", "销售单删除成功");
    }

    /**
     * 校验销售单信息
     * @param sellOrder
     * @param resultBean
     * @return
     */
    private boolean viladSellOrder(QyjSellOrderEntity sellOrder, ResultBean resultBean) {
        if (sellOrder == null) {
            resultBean.init("0002", "销售单为空");
            return false;
        }

        if (sellOrder.getOrderAmount() == null) {
            resultBean.init("0002", "订单金额不能为空");
            return false;
        }
        if (sellOrder.getModifyAmount() == null) {
            resultBean.init("0002", "调整金额不能为空");
            return false;
        }
        if (sellOrder.getHasPayAmount() == null) {
            resultBean.init("0002", "已支付金额不能为空");
            return false;
        }
        if (StringUtils.isEmpty(sellOrder.getOrderStatus())) {
            resultBean.init("0002", "订单状态不能为空");
            return false;
        }

        List<QyjSellProductEntity> SellProductEntityList = sellOrder.getSellProductList();
        if (SellProductEntityList == null || SellProductEntityList.isEmpty()) {
            resultBean.init("0002", "销售单产品不能为空");
            return false;
        }

        Set<Long> idSet = new HashSet<Long>();
        for (QyjSellProductEntity SellProductEntity : SellProductEntityList) {
            if (SellProductEntity.getProductId() == null || StringUtils.isEmpty(SellProductEntity.getProductTitle())) {
                resultBean.init("0002", "产品信息不能为空");
                return false;
            }
            if (SellProductEntity.getNumber() == null || SellProductEntity.getNumber().intValue() <= 0) {
                resultBean.init("0002", "产品数量不正确");
                return false;
            }
            if (SellProductEntity.getPrice() == null || SellProductEntity.getPrice().doubleValue() <= 0) {
                resultBean.init("0002", "产品金额不正确");
                return false;
            }
            if (idSet.contains(SellProductEntity.getProductId())) {
                resultBean.init("0002", "产品信息存在重复[" + SellProductEntity.getProductTitle() + "]");
                return false;
            }
            idSet.add(SellProductEntity.getProductId());
        }

        return true;
    }

    /**
     * 校验销售产品数量，如果产品数量为负数就是数量不够
     * @param SellProductEntityList
     * @throws Exception
     */
    private void validSellProduct(List<QyjSellProductEntity> SellProductEntityList) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (QyjSellProductEntity SellProductEntity : SellProductEntityList) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(SellProductEntity.getProductId());
        }
        sb.insert(0, " and id in (");
        sb.append(") ");
        Map<String, Object> queryParams = new HashMap<>();
        PageParam pageParam = new PageParam();
        pageParam.setQueryCondition(sb.toString());
        pageParam.setPaging(false);
        queryParams.put("pageParam", pageParam);
        List<QyjProductEntity> productList = productMapper.listProduct(queryParams);

        sb = new StringBuilder();
        for (QyjProductEntity entity : productList) {
            if (entity.getNumber() < 0) {
                sb.append("[");
                sb.append(entity.getTitle());
                sb.append("]库存数量不足；<br/>");
            }
        }
        if (sb.length() > 0) {
            throw new ValidException(sb.toString());
        }
    }

    @Autowired
    public void setSellOrderMapper(QyjSellOrderMapper sellOrderMapper) {
        this.sellOrderMapper = sellOrderMapper;
    }

    @Autowired
    public void setSellProductMapper(QyjSellProductMapper sellProductMapper) {
        this.sellProductMapper = sellProductMapper;
    }

    @Autowired
    public void setProductMapper(QyjProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Autowired
    public void setProductService(QyjProductService productService) {
        this.productService = productService;
    }
}
