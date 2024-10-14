package com.aptech.SemesterProject.utility;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Order(1)
public class TransactionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        logger.info("Starting a transaction for req:{}",req.getRequestURI());
        filterChain.doFilter(servletRequest,servletResponse);
        logger.info("Committing a transaction for req: {}",req.getRequestURI());

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
