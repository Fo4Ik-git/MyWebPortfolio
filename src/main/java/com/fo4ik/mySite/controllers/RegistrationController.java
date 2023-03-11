package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;
    private final UserRepo userRepo;

    public RegistrationController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("title", "Registration");
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model, @RequestParam("voucher") String voucher, @RequestParam("username") String username, @RequestParam("password") String password) {
        try {

            //Check if user already exist
            if (userService.addUser(user, voucher)) {
                model.addAttribute("message", "User exists!");
                log.error("User: " + username + " already exist");
                return "redirect:/errorPage";
            }

            //Check is fills all fields
            if (username.equals("") || password.equals("")) {
                model.addAttribute("message", "Please fill all fields");
                log.error("User didn't fill all fields");
                return "redirect:/errorPage";
            }


        } catch (Exception e) {
            model.addAttribute("message", "Error");
            log.error("Error: " + e.getMessage());
            return "redirect:/errorPage";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code, User user) {
        try {
            boolean isActivated = userService.activateUser(code);

            if (isActivated) {
                model.addAttribute("message", "User successfully activated");
                log.info("User: " + user.getUsername() + " successfully activated");
                user.setActive(true);
                userRepo.save(user);
            } else {
                model.addAttribute("message", "Activation code is not found!");
                log.error("Activation code for user: " + user.getUsername() + " is not found!");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error");
            log.error("Error: " + e.getMessage());
            model.addAttribute("link", "/");
            return "redirect:/errorPage";
        }

        return "login";
    }
}
