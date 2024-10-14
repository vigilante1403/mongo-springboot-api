package com.aptech.SemesterProject.filter;

import com.aptech.SemesterProject.utility.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token;
        if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                Cookie[] cookies=request.getCookies();
                if(cookies!=null&&cookies.length>0){
                    Cookie cookie = Arrays.stream(request.getCookies()).filter(c->c.getName().equals("token-vtravel-lib0-authw")).findAny().orElse(null);
                    if(cookie!=null){
                        token = cookie.getValue();
                    }else{
                        filterChain.doFilter(request, response);
                        return;
                    }
                }else{
                    filterChain.doFilter(request, response);
                    return;
                }


            }else{
                token = authorizationHeader.substring("Bearer ".length());
            }

            String username = jwtTokenProvider.getSubject(token);

            if (jwtTokenProvider.isTokenValid(username,token)) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request,response);
    }
}