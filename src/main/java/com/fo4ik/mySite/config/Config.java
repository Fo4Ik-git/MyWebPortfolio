package com.fo4ik.mySite.config;

import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private final LogoService logoService;
    private final UserService userService;

    public Config(UserService userService, LogoService logoService) {
        this.logoService = logoService;
        this.userService = userService;
    }

    public void getUserLogo(User user, Model model) {
        try {
            model.addAttribute("user", user);
            if (user != null) {
                User userFromDb = userService.getUser(user.getUsername());
                Logo logo = logoService.getLogo(user);
                if (!logo.getPath().equals("")) {
                    model.addAttribute("logo", logo.getPath());
                }
                if (user.isActive()) {
                    model.addAttribute("isActive", true);
                }
                List<Role> roles = new ArrayList<>(user.getRoles());
                for (Role role : roles) {
                    switch (role) {
                        case ADMIN -> model.addAttribute("isAdmin", true);
                        case USER -> model.addAttribute("isUser", true);
                        case MODERATOR -> model.addAttribute("isModerator", true);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
