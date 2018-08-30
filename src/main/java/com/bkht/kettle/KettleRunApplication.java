package com.bkht.kettle;

import com.bkht.arcgis.ArcGISConverter;
import com.bkht.kettle.config.KettleConfig;
import com.bkht.kettle.utils.KettleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ComponentScan("com.bkht")
public class KettleRunApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunApplication.class);

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = SpringApplication.run(KettleRunApplication.class, args);

        KettleConfig kettleConfig = ctx.getBean(KettleConfig.class);

        kettleConfig.getParams().forEach((k, v) -> {
            LOGGER.debug("{} = {}", k, v);
        });

        //ctx.getBean(ArcGISConverter.class).convert();

    }
}
