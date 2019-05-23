package com.haze.kettle;

import java.util.ArrayList;
import java.util.List;

public class StepWrapper {

    private List<Step> stepList = new ArrayList<>();

    private List<StepFlow> stepFlowList = new ArrayList<>();

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public List<StepFlow> getStepFlowList() {
        return stepFlowList;
    }

    public void setStepFlowList(List<StepFlow> stepFlowList) {
        this.stepFlowList = stepFlowList;
    }
}
