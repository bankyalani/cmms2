package com.nibss.cmms.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nibss.cmms.security.CMMSAuthenticationSuccessHandler;
import com.nibss.cmms.web.WebAppConstants;

@Controller
public class LoginController  extends CMMSAuthenticationSuccessHandler implements WebAppConstants{

	@RequestMapping("/login")
	public String login(Model model){
		/*if (SecurityContextHolder.getContext().getAuthentication() != null ||
				SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				)
			return home(model);
		else*/
		return "security/forms/login";
	}

	@RequestMapping("/")
	public String home(Model model, HttpServletRequest request, Authentication authentication,HttpServletResponse response){
		String url=determineTargetUrl(authentication, request, response);
		
		if (url==null){
			request.getSession().invalidate();
			url="/login";
		}
		/*else{
			 request.getSession().setAttribute(CURRENT_USER_EMAIL, authentication.getName());
		}*/
			
			return "redirect:"+url;
	}

	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request){
		//remove the session object
		//request.getSession().setAttribute(CURRENT_USER_USER,null);
		return "redirect:/j_spring_security_logout";
	}



}
