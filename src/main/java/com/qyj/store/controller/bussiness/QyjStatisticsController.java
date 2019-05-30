package com.qyj.store.controller.bussiness;

import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjSellProductEntity;
import com.qyj.store.service.QyjStatisticsService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计controller
 * @author shitl
 */
@Controller
@RequestMapping("admin/statistics")
public class QyjStatisticsController extends BaseController {

    private QyjStatisticsService qyjStatisticsService;

    /**
     * 获取产品销售按月统计数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listSellProductMonthPage", method = RequestMethod.GET)
    public ResultBean listSellProductMonthPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 年份
        String year = request.getParameter("year");
        // 月份
        String month = request.getParameter("month");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("year", year);
        paramMap.put("month", month);
        paramMap.put("pageParam", pageParam);

        return qyjStatisticsService.listSellProductMonthPage(paramMap);
    }

    /**
     * 获取产品进货按月统计数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listStockProductMonthPage", method = RequestMethod.GET)
    public ResultBean listStockProductMonthPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 年份
        String year = request.getParameter("year");
        // 月份
        String month = request.getParameter("month");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("year", year);
        paramMap.put("month", month);
        paramMap.put("pageParam", pageParam);

        return qyjStatisticsService.listStockProductMonthPage(paramMap);
    }

    /**
     * 获取按月图标数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProductMonthData", method = RequestMethod.GET)
    public ResultBean getProductMonthData(HttpServletRequest request) {
        // 年份
        int year = Integer.parseInt(request.getParameter("year"));
        return qyjStatisticsService.getProductMonthData(year);
    }

    /**
     * 获取销售产品分页数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSellProductPage", method = RequestMethod.GET)
    public ResultBean getSellProductPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 用户姓名
        String userName = request.getParameter("userName");
        // 产品名称
        String productTitie = request.getParameter("productTitie");
        // 销售时间区间
        String orderTimeBegin = request.getParameter("orderTimeBegin");
        String orderTimeEnd = request.getParameter("orderTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageParam", pageParam);

        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userName.trim())) {
            paramMap.put("userName", userName);
        }
        if (!StringUtils.isEmpty(productTitie) && !StringUtils.isEmpty(productTitie.trim())) {
            paramMap.put("productTitie", productTitie);
        }
        if (!StringUtils.isEmpty(orderTimeBegin) && !StringUtils.isEmpty(orderTimeBegin.trim())) {
            paramMap.put("orderTimeBegin", orderTimeBegin);
        }
        if (!StringUtils.isEmpty(orderTimeEnd) && !StringUtils.isEmpty(orderTimeEnd.trim())) {
            paramMap.put("orderTimeEnd", orderTimeEnd);
        }

        return qyjStatisticsService.listSellProductPage(paramMap);
    }

    /**
     * 导出销售产品明细列表
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportSellProductDetail", method = RequestMethod.POST)
    public void exportSellProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 用户姓名
        String userName = request.getParameter("userName");
        // 产品名称
        String productTitie = request.getParameter("productTitie");
        // 销售时间区间
        String orderTimeBegin = request.getParameter("orderTimeBegin");
        String orderTimeEnd = request.getParameter("orderTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();

        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userName.trim())) {
            paramMap.put("userName", userName);
        }
        if (!StringUtils.isEmpty(productTitie) && !StringUtils.isEmpty(productTitie.trim())) {
            paramMap.put("productTitie", productTitie);
        }
        if (!StringUtils.isEmpty(orderTimeBegin) && !StringUtils.isEmpty(orderTimeBegin.trim())) {
            paramMap.put("orderTimeBegin", orderTimeBegin);
        }
        if (!StringUtils.isEmpty(orderTimeEnd) && !StringUtils.isEmpty(orderTimeEnd.trim())) {
            paramMap.put("orderTimeEnd", orderTimeEnd);
        }

        PageParam pageParam = new PageParam();
        paramMap.put("pageParam", pageParam);
        pageParam.setPaging(false);
        pageParam.setOrderByCondition("so.order_time desc");

        response.setHeader("Content-Disposition", "attachment;filename=销售产品明细表.xlsx");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try (OutputStream os = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os)) {

            String[] headers = {"买家名称", "产品名称", "类型", "销售价格", "销售数量", "单位", "进货价格", "利润", "销售时间"};

            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("销售列表");
            // 表头
            XSSFRow row = sheet.createRow(0);
            XSSFCellStyle cellStyle = wb.createCellStyle();
            // 单元格字体
            XSSFFont font = wb.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(headers[i]);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Object> countMap = qyjStatisticsService.countSellProductAllInfo(paramMap);
            if (countMap != null && countMap.get("totalCount") != null &&
                    Integer.parseInt(countMap.get("totalCount").toString()) != 0) {
                List<QyjSellProductEntity> list = qyjStatisticsService.listSellProductAllInfo(paramMap);
                for (int i = 0; i < list.size(); i++) {
                    QyjSellProductEntity entity = list.get(i);
                    XSSFRow contentRow = sheet.createRow(i + 1);
                    XSSFCell cell = contentRow.createCell(0);
                    cell.setCellValue(StringUtils.isEmpty(entity.getUserName()) ? "其他" : entity.getUserName());
                    cell = contentRow.createCell(1);
                    cell.setCellValue(entity.getProductTitle());
                    cell = contentRow.createCell(2);
                    cell.setCellValue("MANURE".equals(entity.getProductType()) ? "化肥" : "PESTICIDE".equals(entity.getProductType()) ? "农药" : "");
                    cell = contentRow.createCell(3);
                    cell.setCellValue(entity.getPrice().setScale(2).doubleValue());
                    cell = contentRow.createCell(4);
                    cell.setCellValue(entity.getNumber());
                    cell = contentRow.createCell(5);
                    cell.setCellValue(entity.getProductUnit());
                    cell = contentRow.createCell(6);
                    cell.setCellValue(entity.getStockPrice().setScale(2).doubleValue());
                    cell = contentRow.createCell(7);
                    BigDecimal profit = new BigDecimal(entity.getNumber()).multiply(entity.getPrice().subtract(entity.getStockPrice())).setScale(2);
                    cell.setCellValue(profit.doubleValue() < 0 ? 0.00 : profit.doubleValue());
                    cell = contentRow.createCell(8);
                    cell.setCellValue(sdf.format(entity.getOrderTime()));
                }
            }

            wb.write(bos);
            bos.flush();
        }
    }

    @Autowired
    public void setQyjStatisticsService(QyjStatisticsService qyjStatisticsService) {
        this.qyjStatisticsService = qyjStatisticsService;
    }
}
