package com.bkht.kettle.web.controller;

import com.bkht.kettle.KettleRunApplication;
import com.bkht.kettle.job.JobRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/kettle")
public class KettleController {

    @RequestMapping("/view")
    public String index(Model model) {
        try {
            List<Trans> transList = JobRunner.getAllTrans("/");
            model.addAttribute("transList", transList);
        } catch (KettleException e) {
            e.printStackTrace();
        }
        return "kettle/index";
    }
}
