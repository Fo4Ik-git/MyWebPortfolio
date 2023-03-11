package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.service.LogoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    private static final Logger log = LoggerFactory.getLogger(AboutController.class);

    private final UserRepo userRepo;
    private final LogoService logoService;

    public AboutController(UserRepo userRepo, LogoService logoService) {
        this.userRepo = userRepo;
        this.logoService = logoService;
    }

    @GetMapping("/about")
    public String about(@AuthenticationPrincipal User user, Model model) {

        try{
            model.addAttribute("title", "About me");
            Config config = new Config(userRepo,  logoService);
            if (user != null) {
                config.getUserLogo(user, model);
                model.addAttribute("contentUser", user);
            }

            User contentUser = userRepo.findByUsername("fo4ik");
            Logo contentLogo = logoService.getLogo(contentUser);
            model.addAttribute("image", contentLogo.getPath());
            model.addAttribute("contentUser", contentUser);

        } catch (Exception e) {
            log.error("Error in about: " + e.getMessage());
        }
        return "about";
    }

}
