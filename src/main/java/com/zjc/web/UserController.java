package com.zjc.web;

import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    
    @Autowired
    private IdentityService identityService; //跟用户实体相关的用户实体服务

    /**
     * 登录请求
     * @param name 登录用户名
     * @param passwd 登录密码
     * @param session session域
     * @return
     */
    @RequestMapping("/login")
    public String login(String name, String passwd, HttpSession session) {
        
    	//通过用户实体服务identityService调用checkPassword()方法来验证前端请求过来的用户名和密码是否正确
        boolean success = identityService.checkPassword(name, passwd);
        
        if(success) {//如果验证通过
        	
        	//将用户名（实质上是用户组的用户id值）存到session域中，并设置标签"user"
            session.setAttribute("user", name);
            
            //返回到main.jsp页面
            return "main";
        } else {//验证不通过
        	
        	//返回到login.jsp页面
            return "login";
        }
    }
}
