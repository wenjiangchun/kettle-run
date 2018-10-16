package com.bkht;

import com.bkht.kettle.config.KettleConfig;
import com.bkht.shiro.ChainDefinitionSectionMetaSource;
import com.bkht.shiro.ShiroRealm;
import com.bkht.system.service.ResourceService;
import com.bkht.system.service.RoleService;
import com.bkht.system.service.UserService;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = com.bkht.core.jpa.SimpleBaseRepository.class, basePackages="com.bkht")
@EnableTransactionManagement(proxyTargetClass = true)
public class KettleRunApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunApplication.class);

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = SpringApplication.run(KettleRunApplication.class, args);

        KettleConfig kettleConfig = ctx.getBean(KettleConfig.class);

        kettleConfig.getParams().forEach((k, v) -> {
            LOGGER.debug("{} = {}", k, v);
        });

        Set<String> result = new HashSet<String>();
        WebApplicationContext wc = (WebApplicationContext) ctx;
        RequestMappingHandlerMapping bean = wc.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
        for (RequestMappingInfo rmi : handlerMethods.keySet()) {
            PatternsRequestCondition pc = rmi.getPatternsCondition();
            Set<String> pSet = pc.getPatterns();
            result.addAll(pSet);
            pSet.forEach(p -> {
                System.out.println(p);
            });

        }
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SessionsSecurityManager securityManager, ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //开放的静态资源
        chainDefinitionSectionMetaSource.getObject().put("/favicon.ico", "anon");//网站图标
        chainDefinitionSectionMetaSource.getObject().put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinitionSectionMetaSource.getObject());
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("/");
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionsSecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }

    @Bean
    @DependsOn({"userService","roleService","resourceService"})
    public ShiroRealm shiroRealm(UserService userService, RoleService roleService, ResourceService resourceService) {
        ShiroRealm shiroRealm = new ShiroRealm(userService, roleService, resourceService);
        return shiroRealm;
    }

    @Bean
    @DependsOn({"roleService","resourceService"})
    public ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource(RoleService roleService, ResourceService resourceService) {
        ChainDefinitionSectionMetaSource chainDefinitionSectionMetaSource = new ChainDefinitionSectionMetaSource(roleService, resourceService);
        chainDefinitionSectionMetaSource.setFilterChainDefinitions("/login = anon\n" +
                "                /logout = logout\n" +
                "                /index = authc\n" +
                "                / = authc\n" +
                "                /validateCode = anon\n" +
                "                /static/* = anon\n" +
                "                /resources/** = anon\n" +
                "                /** = authc\n" );
        return chainDefinitionSectionMetaSource;
    }
}
