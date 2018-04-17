package com.zyg.guns.core.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelFiledAnnotation {
    /**
     * 字段中文名称
     *
     * @return
     */
    String filedName() default "";

    /** 是否需要导出
     * @return
     */
    boolean status() default true;

    /**
     * 分组名称
     * @return
     */
    String groupName() default "";

    /**
     * 分组key
     * @return
     */
    String groupKey() default "";
}
