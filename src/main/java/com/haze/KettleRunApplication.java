package com.haze;

import com.haze.kettle.config.KettleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = com.haze.core.jpa.SimpleBaseRepository.class, basePackages= "com.haze")
@EnableTransactionManagement(proxyTargetClass = true)
public class KettleRunApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunApplication.class);

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = SpringApplication.run(KettleRunApplication.class, args);

        KettleConfig kettleConfig = ctx.getBean(KettleConfig.class);

        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            LOGGER.debug("beanName=[{}], type=[{}]", beanDefinitionName, ctx.getBean(beanDefinitionName).toString());
        }

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
                LOGGER.debug(p);
            });

        }
    }


    /*@Bean
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
    //@DependsOn({"userService","roleService","resourceService"})
    public ShiroRealm shiroRealm(UserService userService, RoleService roleService, ResourceService resourceService) {
        ShiroRealm shiroRealm = new ShiroRealm(userService, roleService, resourceService);
        return shiroRealm;
    }

    @Bean
    //@DependsOn({"roleService","resourceService"})
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
    }*/
}
