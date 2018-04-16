///*
// *    Copyright 2010-2015 the original author or authors.
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//import com.google.common.collect.Maps;
//import lombok.extern.slf4j.Slf4j;
//import net.jcmob.joyplus.manage.config.RedisConfig;
//import net.jcmob.joyplus.manage.filter.JcaptchaValidateFilter;
//import net.jcmob.joyplus.manage.filter.JwtAuthenticationFilter;
//import net.jcmob.joyplus.manage.jwt.JwtRealm;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.mgt.DefaultSecurityManager;
//import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
//import org.apache.shiro.mgt.DefaultSubjectDAO;
//import org.apache.shiro.mgt.SecurityManager;
//import org.apache.shiro.realm.Realm;
//import org.apache.shiro.session.mgt.DefaultSessionManager;
//import org.apache.shiro.spring.LifecycleBeanPostProcessor;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.filter.DelegatingFilterProxy;
//
//import javax.servlet.Filter;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//@Configuration
//@Slf4j
//public class ShiroConfiguration {
//
//    @Bean(name = "realm")
//    @DependsOn("lifecycleBeanPostProcessor")
//    public Realm jwtRealm() {
//        return new JwtRealm();
//    }
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(){
//        return new JwtAuthenticationFilter();
//    }
//
//    @Bean
//    public JcaptchaValidateFilter jcaptchaValidateFilter(){
//        return new JcaptchaValidateFilter();
//    }
//
//    @Bean
//    public FilterRegistrationBean delegatingFilterProxy(){
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
//        proxy.setTargetFilterLifecycle(true);
//        proxy.setTargetBeanName("shiroFilter");
//        filterRegistrationBean.setFilter(proxy);
//        return filterRegistrationBean;
//    }
//
//    @Bean(name = "shiroFilter")
//    @DependsOn("securityManager")
//    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
//
//        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
//        shiroFilter.setSecurityManager(securityManager);
//        shiroFilter.setLoginUrl("/login.html");
//        shiroFilter.setSuccessUrl("/");
//        shiroFilter.setUnauthorizedUrl("/auth/unauthorized");
//
//        //拦截器
//        LinkedHashMap<String,String> filterChainDefinitionMap = Maps.newLinkedHashMap();
//
//        filterChainDefinitionMap.put("/auth/login","jcaptchaValidate");
//        filterChainDefinitionMap.put("/auth/captcha*","anon");
//        filterChainDefinitionMap.put("/auth/token/refresh","jwt");
//
//        filterChainDefinitionMap.put("/schedule/**","jwt");
//        filterChainDefinitionMap.put("/resources/**","jwt");
//        filterChainDefinitionMap.put("/role/**","jwt");
//        filterChainDefinitionMap.put("/user/**","jwt");
//        filterChainDefinitionMap.put("/dict/**","jwt");
//        filterChainDefinitionMap.put("/dictType/**","jwt");
//        filterChainDefinitionMap.put("/advertiser/**","jwt");
//        filterChainDefinitionMap.put("/order/**","jwt");
//        filterChainDefinitionMap.put("/dudget/**","jwt");
//        filterChainDefinitionMap.put("/product/**","jwt");
//        filterChainDefinitionMap.put("/source/**","jwt");
//        filterChainDefinitionMap.put("/channels/**","jwt");
//        filterChainDefinitionMap.put("/toolbox/**","jwt");
//        filterChainDefinitionMap.put("/orderInput/**","jwt");
//        filterChainDefinitionMap.put("/orderSource/**","jwt");
//        filterChainDefinitionMap.put("/orderCreative/**","jwt");
//        filterChainDefinitionMap.put("/orderPermission/**","jwt");
//
//        filterChainDefinitionMap.put("/advConfirmLog/**","jwt");
//        filterChainDefinitionMap.put("/channelClickLog/**","jwt");
//        filterChainDefinitionMap.put("/noticeAdvLog/**","jwt");
//        filterChainDefinitionMap.put("/noticeChannelLog/**","jwt");
//
//        filterChainDefinitionMap.put("/advReport/**","jwt");
//        filterChainDefinitionMap.put("/proReport/**","jwt");
//        filterChainDefinitionMap.put("/orderReport/**","jwt");
//        filterChainDefinitionMap.put("/sourceReport/**","jwt");
//        filterChainDefinitionMap.put("/materialReport/**","jwt");
//        filterChainDefinitionMap.put("/profitReport/**","jwt");
//        filterChainDefinitionMap.put("/log/**","jwt");
//
//        filterChainDefinitionMap.put("/**","anon");
//
//        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
//
//        Map<String, Filter> filterMap = Maps.newHashMap();
//        filterMap.put("jwt",jwtAuthenticationFilter());
//        filterMap.put("jcaptchaValidate",jcaptchaValidateFilter());
//
//        shiroFilter.setFilters(filterMap);
//
//        return shiroFilter;
//    }
//
//
//    /**
//     * 保证实现了Shiro内部lifecycle函数的bean执行
//     */
//    @Bean(name = "lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }
//
//
//    @Bean(name = "securityManager")
//    public DefaultSecurityManager securityManager(Realm realm) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(realm);
//
////        用自己的Factory实现替换默认用于关闭session功能
//        securityManager.setSubjectFactory(new StatelessSubjectFactory());
//        securityManager.setSessionManager(sessionManager());
//
////        关闭session存储
//        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);
//
////        <!-- 用户授权/认证信息Cache缓存 -->
////        securityManager.setCacheManager(cacheManager);
//
//        SecurityUtils.setSecurityManager(securityManager);
//
//        return securityManager;
//    }
//
//    @Bean
//    public DefaultSessionManager sessionManager() {
//        DefaultSessionManager manager = new DefaultSessionManager();
//        // 关闭session定时检查
//        manager.setSessionValidationSchedulerEnabled(false);
//        return manager;
//    }
//
//    @Bean
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAdvisor(SecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAdvisor.setSecurityManager(securityManager);
//        return authorizationAdvisor;
//    }
//
//    /****************************** Shiro权限缓存Redis *****************************/
//
//    @Bean(name = "redisCacheManager")
//    @DependsOn(value = "shiroRedisTemplate")
//    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> shiroRedisTemplate) {
//        RedisCacheManager cacheManager = new RedisCacheManager(shiroRedisTemplate);
//        return cacheManager;
//    }
//
//    @Bean(name = "shiroRedisTemplate")
//    public RedisTemplate<String, Object> shiroRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        return template;
//    }
//
//    @Primary
//    @Bean(name = "connectionFactory")
//    @DependsOn(value = "redisConfig")
//    public RedisConnectionFactory connectionFactory(RedisConfig redisConfig) {
//        JedisConnectionFactory conn = new JedisConnectionFactory();
//        conn.setDatabase(redisConfig.getDatabase());
//        conn.setHostName(redisConfig.getHost());
//        conn.setPassword(redisConfig.getPassword());
//        conn.setPort(redisConfig.getPort());
//        conn.setTimeout(redisConfig.getTimeout());
//        return conn;
//    }
//
//}
