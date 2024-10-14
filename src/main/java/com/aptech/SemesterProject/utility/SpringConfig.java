package com.aptech.SemesterProject.utility;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan
@Order(1)
public class SpringConfig implements WebMvcConfigurer {
    private final String salt = "[db]Hq38cn%eOd9A7za$C6uEp-GGT1A/d%z/Y;G)GRw4W=vcBG{VYl]N3XDzEer_wbZE3%KB-QqfLggtaJ||Jv0[iUsj]OV";

    public String getSalt() {
        return salt;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter(){
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/api/product/*");
        registrationBean.setOrder(2);
        return registrationBean;

    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("http://localhost:3000/**");
    }
}
