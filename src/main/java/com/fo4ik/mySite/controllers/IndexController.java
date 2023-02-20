package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.config.Config;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final UserRepo userRepo;
    private final LogoRepo logoRepo;

    public IndexController(UserRepo userRepo, LogoRepo logoRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;
    }

   /* @GetMapping("/")
    public String index() {
        return "index";
    }*/
    @GetMapping("/")
    public String index(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            model.addAttribute("title", "Index page");
            Config config = new Config(userRepo, logoRepo);
            config.getUserLogo(user, model);
        }
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorPage";
    }


}
