package com.fo4ik.mySite.config;

import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private final UserRepo userRepo;
    private final LogoRepo logoRepo;

    public Config(UserRepo userRepo, LogoRepo logoRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;

    }

    public void getUserLogo(@AuthenticationPrincipal User user, Model model) {
        try {
            model.addAttribute("user", user);
            if (user != null) {
                User userFromDb = userRepo.findByUsername(user.getUsername());
                Logo logo = logoRepo.findById(userFromDb.getId());
                if (!logo.getPath().equals("")) {
                    model.addAttribute("logo", logo.getPath());
                   // System.out.println("Logo path: " +  logo.getPath());
                }
                if(user.isActive()){
                    model.addAttribute("isActive", true);
                }
                List<Role> roles = new ArrayList<>(user.getRoles());
                for (Role role : roles) {
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

        }
    }
}
