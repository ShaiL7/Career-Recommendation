package com.example.demo.controller;

import com.example.demo.domin.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@EnableAutoConfiguration
public class IndexController {
    //自动注入userService，用来处理业务
    @Autowired
    private UserService userService;
    /**
     * 域名访问重定向
     * 作用：输入域名后重定向到index（首页）
     * */
    @RequestMapping("")
    public String index(HttpServletResponse response) {
        //重定向到 /index
        return response.encodeRedirectURL("/index");
    }
    /**
     * 首页API
     * 作用：显示首页
     * */
    @RequestMapping("/index")
    public String home(Model model) {
        //对应到templates文件夹下面的index
        return "index";
    }

    //进入注册页面，使用Get请求，REST风格的URL能更好的处理问题
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerGet() {
        return "register";
    }
    /**
     * 注册API
     * @method：post
     * @param user（从View层传回来的user对象）
     * @return 重定向
     * */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPost(Model model,
                               //这里和模板中的th:object="${user}"对应起来
                               @ModelAttribute(value = "user") User user,
                               HttpServletResponse response) {
        //我们可以用Sout这种最原始打印方式来检查数据的传输
        System.out.println("Controller信息:"+user.getUsername());
        System.out.println("Controller密码:"+user.getPassword());
        //使用userService处理业务
        String result = userService.register(user);
        //将结果放入model中，在模板中可以取到model中的值
        //这里就是交互的一个重要地方，我们可以在模板中通过这些属性值访问到数据
        model.addAttribute("result", result);
        //开始重定向，携带数据过去了。
        if (result.equals("已被使用")) {
            return "register";
        }
        else {
            return response.encodeRedirectURL("/index");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet() {
        return "login";
    }////////////////
    /**
     * 登录API
     * @method：post
     * @param user（从View层传回来的user对象）
     * @return 重定向
     * */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPost(Model model,
                            @ModelAttribute(value = "user") User user,
                            HttpServletResponse response,
                            HttpSession session) {
        String result = userService.login(user);
        if (result.equals("登陆成功")) {
            //session是作为用户登录信息保存的存在
            session.setAttribute("user",user);
            model.addAttribute("result", result);
            return "index";
        }
        else{
            return "login";
        }
    }
    /**
     * 注销API
     * @method：get
     * @return 首页
     **/
    @RequestMapping(value = "/loginOut", method = RequestMethod.GET)
    public String loginOut(HttpSession session) {
        //从session中删除user属性，用户退出登录
        session.removeAttribute("user");
        return "index";
    }
    @RequestMapping(value = "/skill", method = RequestMethod.GET)
    public String skill(HttpSession session) {

        return "skilltable";
    }
}