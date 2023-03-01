package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.Skills;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    private final UserRepo userRepo;
    private final LogoRepo logoRepo;

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    public IndexController(UserRepo userRepo, LogoRepo logoRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal User user, Model model) {
        try {
            model.addAttribute("title", "Index page");
            if (user != null) {
                Config config = new Config(userRepo, logoRepo);
                config.getUserLogo(user, model);
                Logo logo = logoRepo.findByUser(user);
                if (logo != null){
                    model.addAttribute("image", logo.getPath());
                }

                model.addAttribute("contentUser", user);
            } else {
                User contentUser = userRepo.findByUsername("fo4ik");
                Logo contentLogo = logoRepo.findByUser(contentUser);
                model.addAttribute("image", contentLogo.getPath());
                model.addAttribute("contentUser", contentUser);
            }
        } catch (Exception e) {
            log.error("Error in index: " + e.getMessage());
        }
        return "index";
    }


}
