package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            Config config = new Config(userRepo, logoRepo);
            model.addAttribute("title", "Index page");

            if (user != null) {
                config.getUserLogo(user, model);
                model.addAttribute("contentUser", user);
            }

                User contentUser = userRepo.findByUsername("fo4ik");
                Logo contentLogo = logoRepo.findByUser(contentUser);
                model.addAttribute("image", contentLogo.getPath());
                model.addAttribute("contentUser", contentUser);
        } catch (Exception e) {
            log.error("Error in index: " + e.getMessage());
        }
        return "index";
    }

    @GetMapping("/cv")
    public String cv(@AuthenticationPrincipal User user, Model model){
        if(user != null){
            String cvPath = "files/users/" + user.getId() + "/cv.pdf";
            Config config = new Config(userRepo, logoRepo);
            config.getUserLogo(user, model);
            model.addAttribute("cvPath", cvPath);
        }
        String cvPath = "files/users/" + userRepo.findByUsername("fo4ik").getId() + "/cv.pdf";
        model.addAttribute("cvPath", cvPath);

        return "cv";
    }

}
