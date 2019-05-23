package com.haze.kettle.web.controller;

import com.haze.kettle.KLog;
import com.haze.kettle.KettleParams;
import com.haze.kettle.StepWrapper;
import com.haze.kettle.utils.KettleUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/kettle/trans")
public class TransController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransController.class);
    //String uploadPath = "/home/sofar/下载";

    private final KettleUtils kettleRunner;

    @Autowired
    public TransController(KettleUtils kettleRunner) {
        this.kettleRunner = kettleRunner;
    }

    @RequestMapping("/view")
    public String index(Model model) {
        try {
            List<RepositoryElementMetaInterface> elementList = kettleRunner.getAllTrans("/");
            //List<Trans> transList = new ArrayList<>();
            model.addAttribute("transList", elementList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "kettle/trans/index";
    }

    @RequestMapping("/getElementInfo/{objectId}/{type}")
    public String getElementInfo(@PathVariable String objectId, @PathVariable RepositoryObjectType type,  Model model) {
        String page = "kettle/trans/";
        try {
            switch (type) {
                case TRANSFORMATION:
                    page += "transMetaInfo";
                    model.addAttribute("transMeta", kettleRunner.getTransMetaByObjectId(objectId));
                    break;
                case JOB:
                    page += "jobMetaInfo";
                    model.addAttribute("jobMeta", kettleRunner.getJobMetaByObjectId(objectId));
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
        String page = "kettle/trans/";
        try {
            switch (type) {
                case TRANSFORMATION:
                    page += "transMetaLog";
                    model.addAttribute("logs", kettleRunner.getTransMetaLogs(objectId));
                    break;
                case JOB:
                    page += "jobMetaLog";
                    model.addAttribute("logs", kettleRunner.getJobMetaByObjectId(objectId));
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
    @RequestMapping("/runTrans")
    @ResponseBody
    public KLog runTrans(String objectId, KettleParams params) {
        try {
            //params.getKettleParams().put("isFirst", "1");
            if (params.getKettleParams().containsKey("isFirst") && params.getKettleParams().get("isFirst").equals("1")) {
                for (int startYear = 1970; startYear < 2019; startYear ++) {
                    //KettleRunner.runTrans(transName, startYear + "-01" + "-01",  startYear + "-06" + "-30");
                    LOGGER.debug("开始执行：startDate = " + startYear + "-01" + "-01" + "," + "endDate = " + startYear + "-07" + "-01");
                    params.getKettleParams().put("startDate", startYear + "01" + "01");
                    params.getKettleParams().put("endDate", startYear + "07" + "01");
                    kettleRunner.runTrans(objectId, params.getKettleParams());
                    LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + (startYear + 1) + "-01" + "-01");
                    params.getKettleParams().put("startDate", startYear + "07" + "01");
                    params.getKettleParams().put("endDate", (startYear + 1) + "01" + "01");
                    kettleRunner.runTrans(objectId, params.getKettleParams());
                }
                return new KLog();
            } else {
                return kettleRunner.runTrans(objectId, params.getKettleParams());
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
        return "redirect:/trans/view";
    }


    @RequestMapping("/previewTrans/{objectId}/{type}")
    public String previewTrans(@PathVariable String objectId, @PathVariable RepositoryObjectType type,  Model model) {
        String page = "kettle/trans/preview";
        model.addAttribute("objectId", objectId);
        return page;
    }

    @RequestMapping("/preview/{objectId}")
    @ResponseBody
    public StepWrapper preview(@PathVariable String objectId, Model model) {
        try {
            return kettleRunner.getStepWrapper(objectId);
        } catch (KettleException e) {
            e.printStackTrace();
        }
        return null;
    }
}
