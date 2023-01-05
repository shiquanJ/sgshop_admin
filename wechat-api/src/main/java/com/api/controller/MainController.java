package com.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication(scanBasePackages = "/com/api")
public class MainController {
	public static void main(String[] args) {
		SpringApplication.run(MainController.class, args);
	}
	
	@RequestMapping(value={"/","/main"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/main");
		return mv;
	}
	
	public void test() {
		System.out.println("ss");
	}
	
}
