package com.bkht.kettle.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/map")
    public String map(Model model) {
        return "map/map";
    }


    private List<ProjectSale> toProjectSale(List<Map<String, Object>> result) {
        List<ProjectSale> projectSaleList = new ArrayList<>();
        result.forEach(r -> {
            System.out.println(r.get("x_").toString() + "," + r.get("y_").toString());
            ProjectSale projectSale = new ProjectSale(r.get("pre_project_id_").toString(), Integer.parseInt(r.get("totalcount").toString()), Float.parseFloat(r.get("avg_price").toString()), String.valueOf(r.get("PROJECT_ADDR_")), r.get("project_name_").toString(), r.get("PERMISSION_NO_").toString(), r.get("X_").toString(), r.get("Y_").toString());
            projectSaleList.add(projectSale);
        });
        return projectSaleList;
    }


    @RequestMapping("/getProjectInfo")
    @ResponseBody
    public List<ProjectSale> getProjectInfoes() {
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
                "             round(AVG(s.avg_price_),2) avg_price,\n" +
                "             s.pre_project_id_\n" +
                "         FROM\n" +
                "             f_house_first_sale s\n" +
                "         WHERE\n" +
                "             s.house_used_id_ = '10'\n" +
                "             AND s.avg_price_ <= 500000\n" +
                "             AND s.avg_price_ != 0\n" +
                "         GROUP BY\n" +
                "             s.pre_project_id_\n" +
                "         ORDER BY\n" +
                "             avg_price DESC\n" +
                "     ) b\n" +
                "     LEFT JOIN f_presale_project p ON b.pre_project_id_ = p.pre_projectid_\n" +
                " WHERE\n" +
                "     p.qszt_ = '1'\n" +
                "     AND p.x_ IS NOT NULL";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return toProjectSale(result);
    }
}
