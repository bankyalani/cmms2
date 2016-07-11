package com.nibss.cmms.web.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName="CacheControlFilter",
urlPatterns={"/logout","/j_spring_security_logout"},
description = "Filter Logout request",
dispatcherTypes={DispatcherType.REQUEST,
		DispatcherType.FORWARD,
		DispatcherType.INCLUDE})
public class CacheControlFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
        resp.setHeader("Last-Modified", new Date().toString());
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");
        
        chain.doFilter(request, response);
    }

      @Override
      public void destroy() {}

      @Override
      public void init(FilterConfig arg0) throws ServletException {}

}