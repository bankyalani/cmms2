package com.nibss.cmms.web.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nibss.cmms.domain.User;
import com.nibss.cmms.security.Sha1PasswordEncoder;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.utils.OptionBean;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;
import com.nibss.cmms.web.validation.AjaxValidatorResponse;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private Sha1PasswordEncoder sha1PasswordEncoder;
	private static final Log logger = LogFactory.getLog( UserController.class );

	@RequestMapping("/profile/view")
	public ModelAndView viewProfile(HttpServletRequest request, Model model){
		ModelAndView mav= new ModelAndView();
		User user=(User)getCurrentUser(request);

		this.setViewMainHeading("User Profile <small>User information below</small>",model);
		this.setViewBoxHeading(user.getEmail(),model);

		List<OptionBean> breadCrumb= new ArrayList<>();
		breadCrumb.add(new OptionBean("#", "User Profile"));
		this.setBreadCrumb(breadCrumb,model);

		mav.addObject("user",user);
		mav.setViewName("user/view_user");

		return mav;

	}


	@RequestMapping(value="/passwordChange",method=RequestMethod.POST)
	public @ResponseBody AjaxValidatorResponse changePassword(HttpServletRequest request,
			@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,
			@RequestParam("cNewPassword") String confirmNewPassword){

		AjaxValidatorResponse res= new AjaxValidatorResponse();

		List<String> errors=new ArrayList<>();
		
		try {
		User user =userService.getUserById(getCurrentUser(request).getId());

		
		//if(!user.getPassword().equals(oldPassword)){
		if(!user.getPassword().equals(sha1PasswordEncoder.encodePassword(oldPassword, ""))){
			errors.add("Old password is not correct");
		}
		
			//if the passwords dont match
			if(oldPassword.equals(newPassword)){
				errors.add("New Password cannot be same as old one");
			}
			if(!confirmNewPassword.equals(newPassword)){
				errors.add("Password mis-match. Kindly type new password carefully");
			}else if (!isComplexPassword(newPassword)){
				errors.add("Password Complexity not met!");
				errors.add("Password must be minimum of 8 characters");
				errors.add("Password must contain at least an upper case character");
				errors.add("Password must contain at least a special character (!@#$%^&*_=+-/)");

			}	
			if(!errors.isEmpty()){
				res.setErrorMessageList(errors);
				res.setStatus("FAILED");

			}else{

				//user.setPassword(confirmNewPassword);
				user.setPassword(sha1PasswordEncoder.encodePassword(confirmNewPassword, ""));
				user.setDateModified(new Date());

				userService.updateUser(user);
				res.setStatus("SUCCESS");

			}
		} catch (ServerBusinessException e) {
			res.setStatus("FAILED");	
			errors.add("Unable to change Password. please try later");
			res.setErrorMessageList(errors);
			logger.error(e);
		}

		return res;

	}

	private boolean isComplexPassword(String password) {
		String SPL_CHARS = "!@#$%^&*_=+-/";
		int numOfSpecial = 0;
		int numOfLetters = 0;
		int numOfUpperLetters = 0;
		int numOfLowerLetters = 0;
		int numOfDigits = 0;
		boolean isComplex;


		//byte[] bytes = password.getBytes();
		if (password.length() >= 8) {
			for (int i = 0; i < password.length(); i++) {
				char tempChar = password.charAt(i);
				if (SPL_CHARS.contains("" + tempChar)) {
					numOfSpecial++;
				}
				if (Character.isDigit(tempChar)) {
					numOfDigits++;
				}
				if (Character.isLetter(tempChar)) {
					numOfLetters++;
				}
				if (Character.isUpperCase(tempChar)) {
					numOfUpperLetters++;
				}
				if (Character.isLowerCase(tempChar)) {
					numOfLowerLetters++;
				}
			}

			if (numOfLetters > 0
					&& numOfUpperLetters > 0 && numOfLowerLetters > 0 && numOfDigits > 0) {

				isComplex = true;
			} else {

				isComplex = false;
			}
		} else {

			isComplex = false;
		}

		return isComplex;
	}


}
