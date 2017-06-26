package net.shopxx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;

/**
 * 导出工具类
 * 
 * @author xiaoyin_lu
 *
 */
public final class ExportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);

    public static List<Map<String,Object>> changeOrderToMap(List<Order> orders){
        List<Map<String,Object>> list = Lists.newArrayList();
        for(Order order : orders){
            for(OrderItem item:order.getOrderItems()){
                Map<String,Object> map = Maps.newHashMap();
                map.put("sn", order.getSn());//订单编号
                switch(order.getType().toString()){
                case "general":
                    map.put("type", "普通订单");
                    break;
                case "exchange":
                    map.put("type", "兑换订单");
                    break;
                case "listedtrade":
                    map.put("type", "挂牌交易订单");
                    break;
                default:
                    map.put("type", "其他订单");
                }
                map.put("itemName",item.getName());//商品名称
                map.put("itemNo",item.getSn());//商品名称
                map.put("member",order.getMember().getUsername());//会员
                map.put("price",item.getPrice());//商品价格
                map.put("quantity",item.getQuantity());//商品数量
                map.put("created_date",order.getCreatedDate());//创建时间
                map.put("orderPrice",order.getPrice());//总价
                map.put("store",order.getStore().getName());//店铺
                map.put("consignee",order.getConsignee());//收货人
                map.put("payment_method_name",order.getPaymentMethodName());//支付方式
                String shippingMethodName = order.getShippingMethodName();
                if(StringUtils.isEmpty(shippingMethodName) && order.getIsListedtrade()){
                    shippingMethodName = "挂牌交易";
                }
                map.put("shipping_method_name", order.getShippingMethodName());//配送方式
                
                switch(order.getStatus().toString()){
                case "pendingPayment" : 
                    map.put("status","等待付款");
                    break;
                case "pendingReview" : 
                    map.put("status","等待审核");
                    break;
                case "pendingShipment" : 
                    map.put("status","等待发货");
                    break;
                case "shipped" : 
                    map.put("status","已发货");
                    break;
                case "received" : 
                    map.put("status","已收货");
                    break;
                case "completed" : 
                    map.put("status","已完成");
                    break;
                case "failed" : 
                    map.put("status","已失败");
                    break;
                case "canceled" : 
                    map.put("status","已取消");
                    break;
                case "denied" : 
                    map.put("status","已拒绝");
                    break;
                case "pendingListedTrade" : 
                    map.put("status","等待挂牌交易");
                    break;
                case "listed" : 
                    map.put("status","已挂牌");
                    break;
                default:
                    map.put("status","其他");
                }
                list.add(map);
            }
        }
        return list;
    }
    
    public static void downloadToExcel(HttpServletResponse response, List<Map<String, Object>> list, String[] keys,
            String[] columnNames, String fileName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            createWorkBook(null, list, keys, columnNames).write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/xlsx;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename="
                            + new String((fileName + dateformat.format(System.currentTimeMillis()) + ".xlsx").getBytes(),
                                    "iso-8859-1"));
            ServletOutputStream out = response.getOutputStream();

            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            logger.error("fail to download :{}", e);
        } finally {
            if (bis != null)
                try {
                    bis.close();
                    if (bos != null)
                        bos.close();
                } catch (IOException e) {
                    logger.error("fail to close :{}", e);
                }

        }
    }
    
    
    
    /**
     * 创建excel文档，
     * 
     * @param list
     *            数据
     * @param keys
     *            list中map的key数组集合
     * @param columnNames
     *            excel的列名
     */
    public static Workbook createWorkBook(String sheetName, List<Map<String, Object>> list, String[] keys,
            String columnNames[]) {
        // 创建excel工作簿
        Workbook wb = new SXSSFWorkbook(1000);
        // Workbook wb = new HSSFWorkbook();
        if (StringUtils.isBlank(sheetName))
            sheetName = "sheet1";
        // 创建第一个sheet（页），并命名
        Sheet sheet = wb.createSheet(sheetName);
        // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
        for (int i = 0; i < keys.length; i++) {
            sheet.setColumnWidth((short) i, (short) (35.7 * 150));
        }

        // 创建第一行
        Row row = sheet.createRow((short) 0);

        // 创建两种单元格格式
        CellStyle cs = wb.createCellStyle();
        CellStyle cs2 = wb.createCellStyle();

        // 创建两种字体
        Font f = wb.createFont();
        Font f2 = wb.createFont();

        // 创建第一种字体样式（用于列名）
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // 创建第二种字体样式（用于值）
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());

        // 设置第一种单元格的样式（用于列名）
        cs.setFont(f);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setAlignment(CellStyle.ALIGN_CENTER);

        // 设置第二种单元格的样式（用于值）
        cs2.setFont(f2);
        cs2.setBorderLeft(CellStyle.BORDER_THIN);
        cs2.setBorderRight(CellStyle.BORDER_THIN);
        cs2.setBorderTop(CellStyle.BORDER_THIN);
        cs2.setBorderBottom(CellStyle.BORDER_THIN);
        cs2.setAlignment(CellStyle.ALIGN_CENTER);

        // 设置列名
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(cs);
        }

        // 设置每行每列的值
        if(list!=null&&!list.isEmpty())
            for (int i = 0; i < list.size(); i++) {
                // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
                // 创建一行，在页sheet上
                Row row1 = sheet.createRow(i + 1);
                // 在row行上创建一个方格
                for (short j = 0; j < keys.length; j++) {
                    Cell cell = row1.createCell(j);
                    cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
                    cell.setCellStyle(cs2);
                }
            }

        return wb;
    }
}
