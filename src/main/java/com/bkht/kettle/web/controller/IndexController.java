package com.bkht.kettle.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.Path;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/map")
    public String map(Model model) {
        LocalDate localDate = LocalDate.now();
        model.addAttribute("startDate", localDate.getYear() + "-01-01");
        model.addAttribute("endDate", localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "map/map";
    }


    private List<ProjectSale> toProjectSale(List<Map<String, Object>> result) {
        List<ProjectSale> projectSaleList = new ArrayList<>();
        result.forEach(r -> {
            ProjectSale projectSale = new ProjectSale(String.valueOf(r.get("pre_project_id_")), Integer.parseInt(r.get("totalcount").toString()), Float.parseFloat(r.get("avg_price").toString()),Float.parseFloat(r.get("totalarea").toString()), String.valueOf(r.get("PROJECT_ADDR_")), String.valueOf(r.get("project_name_")), String.valueOf(r.get("PERMISSION_NO_")), r.get("X_").toString(), r.get("Y_").toString());
            projectSaleList.add(projectSale);
        });
        return projectSaleList;
    }


    @RequestMapping("/getProjectInfo")
    @ResponseBody
    public List<ProjectSale> getProjectInfoes(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String orderIndex) {
        LocalDate localDate = LocalDate.now();
        if (StringUtils.isBlank(startDate)) {
            startDate = localDate.getYear() + "-01-01";
        }
        if (StringUtils.isBlank(endDate)) {
            endDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (StringUtils.isBlank(orderIndex)) {
            orderIndex = "0";
        }
        String orderName = "avg_Price";
        switch (orderIndex) {
            case "0":
                break;
            case "1":
                orderName = "totalCount";
                break;
            case "2":
                orderName = "totalArea";
                break;
            default:
                break;
        }
        startDate = startDate.replaceAll("-","");
        endDate = endDate.replaceAll("-","");

        String sql = "SELECT\n" +
                "     b.*,\n" +
                "     p.PERMISSION_NO_,\n" +
                "     p.project_name_,\n" +
                "     p.PROJECT_ADDR_,\n" +
                "     p.x_,\n" +
                "     p.y_\n" +
                " FROM\n" +
                "     (\n" +
                "         SELECT\n" +
                "             COUNT(1) totalcount,\n" +
                "             SUM(s.AREA_) totalarea,\n" +
                "             round(AVG(s.avg_price_),2) avg_price,\n" +
                "             s.pre_project_id_\n" +
                "         FROM\n" +
                "             f_house_first_sale s\n" +
                "         WHERE\n" +
                "             s.house_used_id_ in(10,11,12,111,112) \n" +
                "             AND s.REG_DATE_ID_ >=" + startDate + "\n" +
                "             AND s.REG_DATE_ID_ <=" + endDate + "\n" +
                "         GROUP BY\n" +
                "             s.pre_project_id_\n" +

                "     ) b\n" +
                "     LEFT JOIN f_presale_project p ON b.pre_project_id_ = p.pre_projectid_\n" +
                " WHERE\n" +
                "     p.qszt_ = '1'\n" +
                "     AND p.x_ IS NOT NULL" +
                "         ORDER BY\n" +
                "             b." + orderName + " DESC\n" ;
        logger.debug(sql);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return toProjectSale(result);
    }

    @RequestMapping("/getSaleInfo/{preProjectId}")
    public String getSaleInfo(@PathVariable Long preProjectId, Model model,@RequestParam String startDate, @RequestParam String endDate){
        LocalDate localDate = LocalDate.now();
        if (StringUtils.isBlank(startDate)) {
            startDate = localDate.getYear() + "-01-01";
        }
        if (StringUtils.isBlank(endDate)) {
            endDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        startDate = startDate.replaceAll("-","");
        endDate = endDate.replaceAll("-","");
        String sql = "select * from S_HOUSE_FIRST_SALE_INFO where PRE_PROJECT_ID_=? AND RECORD_DATE_>=? and RECORD_DATE_<=? ORDER BY HOUSE_USED_NAME_";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, preProjectId, startDate, endDate);
        model.addAttribute("results",results);
        if (!results.isEmpty()) {
            model.addAttribute("preProjectName",results.get(0).get("PRE_PROJECT_NAME_"));
            model.addAttribute("permissionNum",results.get(0).get("PERMISSION_NUM_"));
            model.addAttribute("mainBodyName",results.get(0).get("MAINBODY_NAME_"));
        }
        return "map/saleInfo";
    }
}
