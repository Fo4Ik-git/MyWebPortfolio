package com.fo4ik.mySite.config;

import com.fo4ik.mySite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    //@Autowired
    private final UserService userService;

    public static PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/user").hasAnyAuthority("ADMIN", "MODERATOR", "USER")
                                .requestMatchers("/login").authenticated()
                                .requestMatchers("/", "/registration", "/activate/*", "/files/users/**", "/projects", "/src/main/resources/templates/css/**").permitAll().anyRequest().authenticated()
                        //"/","/scheme",
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(getPasswordEncoder());

    }

}
