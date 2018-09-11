package com.bkht.shiro;

import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

//@Configuration
public class ShiroConfig {

    /*@Bean
    @DependsOn({"resourceDao","roleDao"})
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SessionsSecurityManager securityManager, ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //开放的静态资源
        chainDefinitionSectionMetaSource.getObject().put("/favicon.ico", "anon");//网站图标
        chainDefinitionSectionMetaSource.getObject().put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinitionSectionMetaSource.getObject());
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionsSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager(shiroRealm());
        return defaultWebSecurityManager;
    }

    @Bean
    @DependsOn({"resourceDao","roleDao","userDao"})
    public ShiroRealm shiroRealm() {
        ShiroRealm myRealm = new ShiroRealm();
        return myRealm;
    }

    @Bean
    @DependsOn({"resourceDao","roleDao"})
    public ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource() {
        ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource = new ChainDefinitionSectionMetaSource();
        return chainDefinitionSectionMetaSource;
    }*/
}