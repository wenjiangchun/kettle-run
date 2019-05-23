package com.haze.kettle;

public class StepFlow {

    private Step from;

    private Step to;

    private String label;

    public Step getFrom() {
        return from;
    }

    public void setFrom(Step from) {
        this.from = from;
    }

    public Step getTo() {
        return to;
    }

    public void setTo(Step to) {
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
