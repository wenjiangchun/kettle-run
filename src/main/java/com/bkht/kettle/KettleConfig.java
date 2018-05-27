package com.bkht.kettle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KettleConfig {

    @Value("${kettle.trans.name}")
    public String transName;
}
