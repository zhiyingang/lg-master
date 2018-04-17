package com.zyg.guns;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Guns Web程序启动�?
 *
 * @author fengshuonan
 * @date 2017-05-21 9:43
 */
public class ZgServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ZgApplication.class);
    }
}
