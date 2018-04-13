package com.zyg.guns.core.config;

import com.zyg.guns.core.http.HttpClientTemplate;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    @Bean
    public OkHttpClient okHttpClient (){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        return builder.build();
    }

    @Bean
    public HttpClientTemplate httpClientTemplate(OkHttpClient okHttpClient){
        return new HttpClientTemplate(okHttpClient);
    }
}
