package com.zyg.guns;
import com.alipay.demo.trade.config.Configs;
import com.zyg.guns.modular.pay.unionpay.util.SDKConfig;
import com.zyg.guns.modular.pay.weixinpay.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot方式启动类
 *
 * @author stylefeng
 * @Date 2017/5/21 12:06
 */
@SpringBootApplication
public class ZgApplication {

    private final static Logger logger = LoggerFactory.getLogger(ZgApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZgApplication.class, args);
        Configs.init("pay/zfbinfo.properties");//支付宝
        ConfigUtil.init("pay/wxinfo.properties");//微信
        SDKConfig.getConfig().loadPropertiesFromSrc();//银联
        logger.info("GunsApplication is success!");
    }

}
