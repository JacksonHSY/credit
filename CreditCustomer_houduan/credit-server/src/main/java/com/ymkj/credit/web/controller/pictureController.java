package com.ymkj.credit.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = "/picture")
@Controller
@Slf4j
public class pictureController {
	@RequestMapping(value = "/pic")
	public String index(Model model,HttpSession httpSession){
		return "redirect:/pic/picture.html";
		
	}
}
