package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String index(@AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("title", "Index page");
            System.out.println("Current theme index: " + model.getAttribute("theme"));
            if (redirectAttributes.getFlashAttributes().get("theme") != null) {
                model.addAttribute("theme", redirectAttributes.getFlashAttributes().get("theme"));
            } else if (model.getAttribute("theme") == null) {
                model.addAttribute("theme", "dark");
            }
            if (user != null) {
                Config config = new Config(userRepo, logoRepo);
                config.getUserLogo(user, model);
                Logo logo = logoRepo.findByUser(user);
                if (logo != null) {
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

    @GetMapping("/switchTheme")
    public String switchTheme(@RequestParam("theme") String theme, Model model, RedirectAttributes redirectAttributes) {
        if (theme.equals("dark")) {
            redirectAttributes.addFlashAttribute("theme", "light");
        } else {
            redirectAttributes.addFlashAttribute("theme", "dark");
        }
        return "redirect:/";
    }


}
