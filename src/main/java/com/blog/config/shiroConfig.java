package com.blog.config;

import com.blog.shiro.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class shiroConfig {
    //1、加载shiro 拦截器
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        //给shiro拦截器中 加入安全管理器
        filterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        //设置需要；拦截的路径
        Map<String,String> map = new HashMap<>();
        //anon 不需要拦截
        //authc 拦截的路径
        //user 记住我
        map.put("/admin","anon");
        map.put("/admin/login","anon");

        map.put("/admin/**","authc");
        //放行登录页面
        filterFactoryBean.setLoginUrl("/admin");
        // 设置未授权提示页面
        filterFactoryBean.setUnauthorizedUrl("/403");
        filterFactoryBean.setFilterChainDefinitionMap(map);
        return filterFactoryBean;
    }
    //2、创建安全管理器
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(Realm realm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //给安全管理器添加realm
        defaultWebSecurityManager.setRealm(realm);

        return defaultWebSecurityManager;
    }
    //3、创建自定义的Realm
    @Bean
    public Realm realm(){
        CustomerRealm customerRealm = new CustomerRealm();
        //修改凭证校验匹配器
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //设置加密算法为MD5
        credentialsMatcher.setHashAlgorithmName("MD5");
        //设置hash散列次数
        //credentialsMatcher.setHashIterations(1024);
        //把修改后的校验匹配器，添加到自定义的Realm中
        customerRealm.setCredentialsMatcher(credentialsMatcher);

        return customerRealm;
    }
}
