package com.fo4ik.mySite.config;

import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

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
            if (user != null) {
                User userFromDb = userService.getUser(user.getUsername());
                model.addAttribute("user", userFromDb);
                Logo logo = new Logo();
                if (logoService.getLogo(user) != null) {
                    logo = logoService.getLogo(user);
                }
                model.addAttribute("logo", logo.getPath());
                model.addAttribute("isActive", user.isActive());
                for (Role role : user.getRoles()) {
                    switch (role) {
                        case ADMIN:
                            model.addAttribute("isAdmin", true);
                            break;
                        case USER:
                            model.addAttribute("isUser", true);
                            break;
                        case MODERATOR:
                            model.addAttribute("isModerator", true);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in Config: " + e.getMessage());
        }
    }
}


