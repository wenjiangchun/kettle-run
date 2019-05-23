package com.haze.kettle.web.controller;

import com.haze.kettle.KLog;
import com.haze.kettle.KettleParams;
import com.haze.kettle.utils.KettleUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/kettle/job")
public class JobController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);
    //String uploadPath = "/home/sofar/下载";

    @Autowired
    private KettleUtils kettleUtils;



    @RequestMapping("/view")
    public String index(Model model) {
        try {
            List<RepositoryElementMetaInterface> elementList = kettleUtils.getAllJobs("/");
            model.addAttribute("jobList", elementList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "kettle/job/index";
    }

    @RequestMapping("/getElementInfo/{objectId}/{type}")
    public String getElementInfo(@PathVariable String objectId, @PathVariable RepositoryObjectType type,  Model model) {
        String page = "kettle/job/";
        try {
            switch (type) {
                case TRANSFORMATION:
                    page += "transMetaInfo";
                    model.addAttribute("transMeta", kettleUtils.getTransMetaByObjectId(objectId));
                    break;
                case JOB:
                    page += "jobMetaInfo";
                    model.addAttribute("jobMeta", kettleUtils.getJobMetaByObjectId(objectId));
                    break;
                case DATABASE:
                    break;
                case SLAVE_SERVER:
                    break;
                case CLUSTER_SCHEMA:
                    break;
                case PARTITION_SCHEMA:
                    break;
                case STEP:
                    break;
                case JOB_ENTRY:
                    break;
                case TRANS_DATA_SERVICE:
                    break;
                case PLUGIN:
                    break;
                case UNKNOWN:
                    break;
            }

        } catch (KettleException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return page;
    }

    @RequestMapping("/getElementLog/{objectId}/{type}")
    public String getElementLog(@PathVariable String objectId, @PathVariable RepositoryObjectType type,  Model model) {
        String page = "kettle/";
        try {
            switch (type) {
                case TRANSFORMATION:
                    page += "transMetaLog";
                    model.addAttribute("logs", kettleUtils.getTransMetaLogs(objectId));
                    break;
                case JOB:
                    page += "jobMetaLog";
                    model.addAttribute("logs", kettleUtils.getJobMetaByObjectId(objectId));
                    break;
                case DATABASE:
                    break;
                case SLAVE_SERVER:
                    break;
                case CLUSTER_SCHEMA:
                    break;
                case PARTITION_SCHEMA:
                    break;
                case STEP:
                    break;
                case JOB_ENTRY:
                    break;
                case TRANS_DATA_SERVICE:
                    break;
                case PLUGIN:
                    break;
                case UNKNOWN:
                    break;
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return page;
    }
    @RequestMapping("/runJob")
    @ResponseBody
    public KLog runJob(String objectId, KettleParams params) {
        try {
            if (params.getKettleParams().containsKey("isFirst") && params.getKettleParams().get("isFirst").equals("1")) {
                for (int startYear = 1970; startYear < 2019; startYear ++) {
                    //KettleRunner.runTrans(transName, startYear + "-01" + "-01",  startYear + "-06" + "-30");
                    LOGGER.debug("开始执行：startDate = " + startYear + "-01" + "-01" + "," + "endDate = " + startYear + "-06" + "-30");
                    params.getKettleParams().put("startDate", startYear + "01" + "01");
                    params.getKettleParams().put("endDate", startYear + "06" + "30");
                    kettleUtils.runJob(objectId, params.getKettleParams());
                    LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + startYear + "-12" + "-31");
                    params.getKettleParams().put("startDate", startYear + "07" + "01");
                    params.getKettleParams().put("endDate", startYear + "12" + "31");
                    kettleUtils.runJob(objectId, params.getKettleParams());
                }
                return new KLog();
            } else {
                kettleUtils.runJob(objectId, params.getKettleParams());
                return new KLog();
            }
        } catch (Exception e) {
            return KLog.createDefaultErrorKettleLog(e.getMessage());
        }
    }

   /* *//**
     * 上传文件
     *//*
    @RequestMapping(value = "/uploadFile")
    public void uploadFile(MultipartHttpServletRequest request,
                           HttpServletResponse response) {
        int n = request.getFiles("file").size();
        request.getFiles("file").forEach(f -> {
        });
        try {
            File f = new File(uploadPath + FileSystems.getDefault().getSeparator() + request.getFiles("file").get(0).getOriginalFilename());
            request.getFiles("file").get(0).transferTo(f);
            Map<String, String> variables = new HashMap<>();
            variables.put("searchFile", f.getPath());
            String resultFile = uploadPath + FileSystems.getDefault().getSeparator() + System.currentTimeMillis();
            variables.put("resultFile", resultFile);
            variables.put("excelVersion", "2007");
            KettleRunner.runTrans("根据身份证批量查询房产信息", variables);


            BufferedInputStream fis = null;
            File file = new File(resultFile + ".xls");
            fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((file.getName()).getBytes("utf-8"), "utf-8"));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @RequestMapping("/refreshRepository")
    public String refreshRepository() {
        return "redirect:/kettle/job/view";
    }

}
