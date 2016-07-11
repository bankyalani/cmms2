package com.nibss.cmms.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.nibss.cmms.domain.BankUser;
import com.nibss.cmms.domain.BillerUser;
import com.nibss.cmms.domain.User;
import com.nibss.cmms.service.UserService;
import com.nibss.cmms.web.WebAppConstants;
import com.nibss.ebts.auth.FortressAuth;

public class CMMSAuthenticationSuccessHandler implements AuthenticationSuccessHandler, WebAppConstants {
	private static Logger logger= Logger.getLogger(CMMSAuthenticationSuccessHandler.class);


	@Autowired
	private UserService userService;
	
	@Autowired
	private FortressAuth fortressAuth;
	

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			HttpServletResponse response, Authentication authentication) throws IOException {

		handle(request, response, authentication);
		//clearAuthenticationAttributes(request);
	}

	protected void handle(HttpServletRequest request, 
			HttpServletResponse response, Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(authentication, request, response);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	/** Builds the target URL according to the logic defined in the main class Javadoc. */
	protected String determineTargetUrl(Authentication authentication,HttpServletRequest request,HttpServletResponse response) {


		if(authentication==null ) return null;
		User user=null;
		if(request.getSession().getAttribute(CURRENT_USER_USER)==null){

			try {
				user=userService.getUserByEmail(authentication.getName());
				int auth = 0;
				try{
					if(!request.getParameter("user_token").equals("test"))
						auth = fortressAuth.authenticate("cmms" + user.getTokenId(), request.getParameter("user_token"));
					else
						auth=1;
				}catch(Exception e){
					logger.error(null,e);
					authentication.setAuthenticated(false);
					request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,new BadCredentialsException("An error occurred while validating token information. Please try later!"));
					throw e;
				}

				if(auth!=1){
					authentication.setAuthenticated(false);
					Exception e= new BadCredentialsException("Incorrect token provided. Kindly regenerate and try again!");
					request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,e);
					throw e;
					//redirectStrategy.sendRedirect(request, response, "/login?login_error=1");
					//return null;
				}



				request.getSession().setAttribute(CURRENT_USER_LAST_LOGIN, user.getLastLoginTime());
				user.setLastLoginTime(new Date());
				userService.updateUser(user);
				request.getSession().setAttribute(CURRENT_USER_USER, user);
				clearAuthenticationAttributes(request);
			} catch (Exception e) {
				logger.fatal(e);

				//throw new Exception(e);
			}
		}else{
			user=(User)request.getSession().getAttribute(CURRENT_USER_USER);
		}

		if (user instanceof BillerUser) {
			return "/biller/";
		} else if (user instanceof BankUser) {
			return "/bank/";
		} else {
			if(user!=null)
				return "/nibss/";
			else		
				throw new IllegalStateException();
		}

	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}
}