package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    private static final Logger log = LoggerFactory.getLogger(AboutController.class);
    private final UserService userService;

    private final LogoService logoService;

    @Value("${user}")
    private String username;

    public AboutController(UserService userService, LogoService logoService) {
        this.userService = userService;
        this.logoService = logoService;
    }


    @GetMapping("/about")
    public String about(@AuthenticationPrincipal User user, Model model) {

        try{
            model.addAttribute("title", "About me");
            Config config = new Config(userService,  logoService);
            if (user != null) {
                config.getUserLogo(user, model);
                model.addAttribute("contentUser", user);
            }

            User contentUser = userService.getUser(username);
            Logo contentLogo = logoService.getLogo(contentUser);
            model.addAttribute("image", contentLogo.getPath());
            model.addAttribute("contentUser", contentUser);

        } catch (Exception e) {
            log.error("Error in about: " + e.getMessage());
        }
        return "pages/about";
    }

}
