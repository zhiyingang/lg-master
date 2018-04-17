package com.zyg.guns.core.excel.dto;

import java.io.Serializable;

/**
 * Created by gf.liu on 2017/11/23.
 */
public class ExcelHeaderDto implements Serializable {
    /**
     * 标头名称
     */
    private String headerName;

    /**
     * 此列在实体中的属性名
     */
    private String fieldName;

    /**
     * 列的宽度
     */
    private Integer columnWidth;

    public ExcelHeaderDto() {
    }

    public ExcelHeaderDto(String headerName, String fieldName, Integer columnWidth) {
        this.headerName = headerName;
        this.fieldName = fieldName;
        this.columnWidth = columnWidth;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }
}
