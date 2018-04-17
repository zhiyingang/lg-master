package com.zyg.guns.core.excel.util;

import com.zyg.guns.core.excel.dto.ExcelHeaderDto;
import com.zyg.guns.core.excel.exception.ExcelExportParamException;
import com.zyg.guns.core.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by liuguofang on 2017/11/23.
 */
public class ExcelExportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);
    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";//默认日期格式
    public static int DEFAULT_COLUMN_WIDTH = 17;

    /**
     * 导出Excel文件(常规)
     *
     * @param sheetName  sheet名称
     * @param titleName  标题名称
     * @param headerList 标题属性对象集合
     * @param dataList   数据集合
     * @return
     */
    public static <T> OutputStream exportBasicExcel(String sheetName, String titleName,
                                           List<ExcelHeaderDto> headerList,
                                           List<T> dataList) throws ExcelExportParamException {

        //先校验
        ExcelExportUtil.checkParams(sheetName, headerList, dataList);
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        cellStyle.setFont(cellFont);

        // 遍历集合数据，产生数据行
        SXSSFSheet sheet = null;
        int rowIndex = 0;
        if(dataList!=null && dataList.size()>0){
            for (Object dataObject : dataList) {
                if (rowIndex % 65535 == 0) {
                    sheet = createSheet(sheetName, titleName, headerList, workbook);
                    rowIndex = sheet.getLastRowNum() + 1;
                }
                if (null == sheet) {
                    rowIndex++;
                    continue;
                }
                SXSSFRow dataRow = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                for (ExcelHeaderDto headerDto : headerList) {
                    String fieldName = headerDto.getFieldName();
                    String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        Method method = dataObject.getClass().getMethod(methodName);
                        Object obj = method.invoke(dataObject);
                        setCellValue(cellStyle, dataRow, cellIndex++, obj);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            sheet = createSheet(sheetName, titleName, headerList, workbook);
        }
        // 自动调整宽度
//        for (int i = 0; i < headerList.size(); i++) {
//            sheet.autoSizeColumn(i);
//        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);

        } catch (IOException e) {
            logger.error("导出Excel出错{}", e);
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                    workbook.dispose();
                }
            } catch (IOException e) {
                logger.error("导出Excel出错{}", e);
            }
        }
        return out;
    }

    /**
     * 创建一个带标题和头部的sheet页
     *
     * @param sheetName  sheet名称
     * @param titleName  标题名称
     * @param headerList 头部信息
     * @param workbook
     */
    private static SXSSFSheet createSheet(String sheetName, String titleName, List<ExcelHeaderDto> headerList, SXSSFWorkbook workbook) {
        // 生成一个(带标题)表格
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBoldweight((short) 700);
        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);
        int[] arrColWidth = new int[headerList.size()];
        // 产生表格标题行,以及设置列宽
        int cellIndex = 0;
        for (ExcelHeaderDto head : headerList) {
            int bytes = head.getHeaderName().getBytes().length;
            Integer columnWidth = head.getColumnWidth() == null ? DEFAULT_COLUMN_WIDTH : head.getColumnWidth();
            arrColWidth[cellIndex] = bytes < columnWidth ? columnWidth : bytes;
            sheet.setColumnWidth(cellIndex, arrColWidth[cellIndex] * 256);
            cellIndex++;
        }
        int rowNum = 0;
        //有标题
        if (!StringUtil.isEmpty(titleName)) {
            SXSSFRow titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue(titleName);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.size() - 1));
        }
        SXSSFRow headerRow = sheet.createRow(rowNum++); //列头 rowIndex =1
        for (int i = 0; i < headerList.size(); i++) {
            headerRow.createCell(i).setCellValue(headerList.get(i).getHeaderName());
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        return sheet;
    }

    /**
     * 校验参数是否合法
     *
     * @param sheetName
     * @param headerList
     * @param dataList
     */
    private static<T> void checkParams(String sheetName, List<ExcelHeaderDto> headerList, List<T> dataList) throws ExcelExportParamException {
        if (null == sheetName) {
            throw new ExcelExportParamException("sheet名称不能为空");
        }
        if (CollectionUtils.isEmpty(headerList)) {
            throw new ExcelExportParamException("标题对象集合不能为空");
        }
//        if (CollectionUtils.isEmpty(dataList)) {
//            throw new ExcelExportParamException("数据集合不能为空");
//        }
        for (ExcelHeaderDto dto : headerList) {
            if (StringUtil.isEmpty(dto.getHeaderName())) {
                throw new ExcelExportParamException("标题名称不能为空");
            }
            if (StringUtil.isEmpty(dto.getFieldName())) {
                throw new ExcelExportParamException("字段属性名不能为空");
            }
        }
    }

    /**
     * 设置单元格数据
     *
     * @param cellStyle
     * @param dataRow
     * @param cellIndex
     * @param obj
     */
    private static void setCellValue(CellStyle cellStyle, SXSSFRow dataRow, int cellIndex, Object obj) {
        SXSSFCell newCell = dataRow.createCell(cellIndex++);
        String cellValue = "";
        if (obj == null) {
            cellValue = "";
        } else if (obj instanceof Date) {
            cellValue = new SimpleDateFormat(DEFAULT_DATE_PATTERN).format(obj);
        } else if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal) {
            cellValue = new BigDecimal(obj.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            cellValue = obj.toString();
        }
        newCell.setCellValue(cellValue);
        newCell.setCellStyle(cellStyle);
    }



    public static<T> void genExcelOutStream(HttpServletResponse response, String fileName, String title, String sheetName,
                                            List<ExcelHeaderDto> headerList, List<T> dataList){
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = (ByteArrayOutputStream) ExcelExportUtil.exportBasicExcel(sheetName, title,headerList,dataList);
            byte[] content = os.toByteArray();
            is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[content.length+1024];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } catch (Exception e) {
            logger.error("导出出错{}", e);
        } finally {
            closeStream(is,outputStream, bis,bos);
        }
    }

    private static void closeStream(InputStream is, ServletOutputStream outputStream,
                                    BufferedInputStream bis, BufferedOutputStream bos) {
        try{
            // 关闭流
            if (is != null) {
                is.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }catch (IOException e){
            logger.debug("close IOException:" + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        List<Object> list = new ArrayList<>();
//        list.add(1);
//        list.add("123");
//        list.add(true);
//        list.add(new Date());
//        for (Object o : list){
//            System.out.println(o.getClass().toString() + "==========="+o.toString());
//        }
//    }
}
