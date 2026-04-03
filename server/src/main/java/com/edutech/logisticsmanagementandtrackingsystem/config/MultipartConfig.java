package com.edutech.logisticsmanagementandtrackingsystem.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {

@Bean
public MultipartConfigElement multipartConfigElement() {
MultipartConfigFactory factory = new MultipartConfigFactory();

// :white_check_mark: Max size per file
factory.setMaxFileSize(DataSize.ofMegabytes(10));

// :white_check_mark: Max size of the whole request (all files + form data)
factory.setMaxRequestSize(DataSize.ofMegabytes(20));

return factory.createMultipartConfig();
}
}