package com.zjc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SysApp extends SpringBootServletInitializer{
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		
		return application;
	}
	
	public static void main(String[] args) {
		
		new SpringApplicationBuilder(SysApp.class).run(args);
	}
}
