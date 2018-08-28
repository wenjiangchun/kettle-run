package com.bkht.kettle.web.controller;

import java.io.Serializable;

public class ProjectSale implements Serializable {

    private String preProjectId;

    private int totalCount;

    private float avgPrice;

    private String projectAddress;

    private String projectName;

    private String projectNum;

    private String x;

    private String y;
    public ProjectSale(String preProjectId, int totalCount, float avgPrice, String projectAddress, String projectName, String projectNum, String x, String y) {
        this.preProjectId = preProjectId;
        this.totalCount = totalCount;
        this.avgPrice = avgPrice;
        this.projectAddress = projectAddress;
        this.projectName = projectName;
        this.projectNum = projectNum;
        this.x = x;
        this.y = y;
    }

    public String getPreProjectId() {
        return preProjectId;
    }

    public void setPreProjectId(String preProjectId) {
        this.preProjectId = preProjectId;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectNum() {
        return projectNum;
    }

    public void setProjectNum(String projectNum) {
        this.projectNum = projectNum;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
