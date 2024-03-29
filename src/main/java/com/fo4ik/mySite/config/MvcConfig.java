package com.fo4ik.mySite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static String OS = null;

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public static String uploadDirectory = System.getProperty("user.dir");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (isWindows()) {
            registry.addResourceHandler("/**").addResourceLocations("file:" + uploadDirectory + "\\");
            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/**");
        } else {
            registry.addResourceHandler("/**").addResourceLocations("file://" + uploadDirectory + "/");
            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/**");
        }
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/templates/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/templates/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/templates/js/");

    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
