package com.zyg.guns.core.excel.util;


import com.zyg.guns.core.excel.annotation.ExcelFiledAnnotation;
import com.zyg.guns.core.excel.dto.ExcelExportSelectDto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelSelectParamUtil {
    public static  Map<String,List<ExcelExportSelectDto>> getSelectParam(Object object){
        Map<String,List<ExcelExportSelectDto>> resultMap = new HashMap<>();
        Class<?> clazz = object.getClass();
        Field[]fields  = clazz.getDeclaredFields();
        for (Field field:fields){
            ExcelFiledAnnotation annotation = field.getAnnotation(ExcelFiledAnnotation.class);
            if(annotation!=null && annotation.status()){
                ExcelExportSelectDto excelExportSelectDto = new ExcelExportSelectDto();
                excelExportSelectDto.setKey(field.getName());
                excelExportSelectDto.setName(annotation.filedName());
                if(resultMap.get(annotation.groupKey()) == null){
                    List<ExcelExportSelectDto> orderParamList = new ArrayList<>();
                    orderParamList.add(excelExportSelectDto);
                    ExcelExportSelectDto excelExportSelectDtoTmp = new ExcelExportSelectDto();
                    orderParamList.add(excelExportSelectDtoTmp);
                    resultMap.put(annotation.groupKey(),orderParamList);
                }else{
                    resultMap.get(annotation.groupKey()).add(excelExportSelectDto);
                }
            }
        }
        return resultMap;
    }
}
