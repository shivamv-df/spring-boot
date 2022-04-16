package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.smart.dao.UserrRepositry;
import com.smart.entites.User;
import com.smart.helper.Message;


@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserrRepositry userrRepositry;
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title","HOME_Smart Control Manager");
		
		return "home";
	}
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","ABOUT- Smart Control Manager");
		System.out.println("working about");
		return "about";
	}
	@GetMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title","SignUp-Smart Control Manager");
		model.addAttribute("user",new User());
		return "signUp";
	}
	@PostMapping("/do_register")
	public String registeruser(@Valid @ModelAttribute("user") User user,BindingResult result1
			,@RequestParam(value = "aggreement",defaultValue = "false")
	          boolean aggreement,Model model,HttpSession session) {
	try {
		if (!aggreement) {
			System.out.println("you have not accepted");
			throw new Exception(" you have not accepted terms and condition");
		}
		if(result1.hasErrors()) {
			System.out.println("error"+result1);
			model.addAttribute("user",user);
			return "signup";
		}
		user.setRole("role_user");
		user.setEnabled(true);
		user.setImageUrl("defalut.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		System.out.println("aggreement"+aggreement);
		System.out.println("user"+user);
	User result=	this.userrRepositry.save(user);
	model.addAttribute("user",new User());
	session.setAttribute("message", new Message("user", "register suceesfully-alert-success"));
	System.out.println(result);
		
	
	} catch (Exception e) {
	e.printStackTrace();
	model.addAttribute("user",user);
	session.setAttribute("message", new Message("Something went wrong"+e.getMessage(),"alert-danger"));
	}
		
		return "signUp";
	}
   @GetMapping("/signin")
	public String customLogin(Model model) {
	   model.addAttribute("tittle","login page");
    	return "login";
    }



}
