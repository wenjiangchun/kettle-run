package com.haze.kettle;

import java.util.HashMap;
import java.util.Map;

public class KettleParams {

    private Map<String, String> kettleParams = new HashMap<>();

    public Map<String, String> getKettleParams() {
        return kettleParams;
    }

    public void setKettleParams(Map<String, String> kettleParams) {
        this.kettleParams = kettleParams;
    }
}
