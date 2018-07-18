package com.zjc.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 之前朋友遇到过的问题，在此记录一下
 * 由于是boot项目，控制层可以使用@RestController注解
 * 但是这样的注解默返回的是json数据，如果想返回jsp页面就要用ModelAndView视图解析器
 * 与此同时配置文件中也要配置页面的前后缀：
 * spring.mvc.view.prefix: /pages/
 * spring.mvc.view.suffix: .jsp   
 * 
 */
@RestController
public class TestController {
	
	/**
	 * 访问系统首页
	 * 当前项目跟根径或者根路径下的index都能访问，最终返回到登录界面login.jsp
	 * @return
	 */
	@RequestMapping({"/","/index"})
	public ModelAndView index(){

		return new ModelAndView("login");
	}
}
