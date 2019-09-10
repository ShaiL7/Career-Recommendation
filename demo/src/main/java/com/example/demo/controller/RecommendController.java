package com.example.demo.controller;

import com.example.demo.domin.User;
import com.example.demo.service.RecommendJobService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import scala.Tuple2;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@EnableAutoConfiguration
public class RecommendController {
    //自动注入userService，用来处理业务
    @Autowired
    private RecommendJobService recommendJobService;

    @RequestMapping(value = "/recommandjob", method = RequestMethod.GET)
    public String recommandjobGet() {
        return "skilltable";
    }


    @RequestMapping(value = "/skillName", method = RequestMethod.POST)
    public String skillNamePost(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        recommendJobService.initSkill_IdDict();//先初始化字典
        String a[] = null;
        a = request.getParameterValues("vehicle");
        ArrayList<String> skillName = recommendJobService.getSkillById(a);
        //add id_skill map
        Map<String, String> id_skill = new HashMap<>();
        for (int i = 0; i < a.length; i++)
            id_skill.put(a[i], skillName.get(i));
        session.setAttribute("id_skill", id_skill);
        // session.setAttribute("skillName",skillName);
        return response.encodeRedirectURL("/skillName");
    }

    @RequestMapping(value = "/ast", method = RequestMethod.POST)
    public String ast(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
//        System.out.println(request.getParameter("sqa"));
        Map<String, String> id_skill = (Map<String, String>) session.getAttribute("id_skill");
        Map<String, String> id_score = new HashMap<>();
        ArrayList<String> idStillArr = new ArrayList<>();
        id_skill.forEach((x, y) ->
        {
            idStillArr.add(x);
            id_score.put(x, request.getParameter(x.toString()));
        });
        ArrayList<String> idJobArr = new ArrayList<>();
        List<Tuple2<String, Double>> jobRecommend = recommendJobService.recommandjob(id_score, idJobArr);
        session.setAttribute("jobRecommend", jobRecommend);
        ArrayList<ArrayList<Tuple2<String, String>>> extraSkill = recommendJobService.getExtraSkill(idJobArr, idStillArr);
        session.setAttribute("extraSkill", extraSkill);
        return response.encodeRedirectURL("/output");
    }
}