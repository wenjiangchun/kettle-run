package com.haze.kettle;

import java.util.Date;

public class KLog {

    private Long id;

    private String name;

    private String content;

    private int errors;

    private boolean success;

    private Date startDate;

    private Date endDate;

    private String status = "end";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        if ("end".equalsIgnoreCase(status)) {
            return "已结束";
        } else if ("start".equalsIgnoreCase(status)) {
            return "已开始执行";
        } else {
            return "未知状态";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static KLog createDefaultErrorKettleLog(String errorMessage) {
        KLog kettleLog = new KLog();
        kettleLog.setSuccess(false);
        kettleLog.setContent(errorMessage);
        return kettleLog;
    }
}
