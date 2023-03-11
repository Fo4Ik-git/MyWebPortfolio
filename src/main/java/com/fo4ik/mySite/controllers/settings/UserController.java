package com.fo4ik.mySite.controllers.settings;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.service.LogoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepo userRepo;
    private final LogoService logoService;

    public UserController(UserRepo userRepo, LogoService logoService) {
        this.userRepo = userRepo;
        this.logoService = logoService;
    }

    @GetMapping
    public String userList(@AuthenticationPrincipal User user, Model model) {
        try {
            Config config = new Config(userRepo, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("user", user);

            List<Role> roles = new ArrayList<>(user.getRoles());
            for (Role role : roles) {
                switch (role) {
                    case ADMIN:
                        model.addAttribute("users", userRepo.findAll());
                        break;
                    case MODERATOR:
                        model.addAttribute("users", userRepo.findAll());
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error in userList method: " + e.getMessage());
        }

        //model.addAttribute("users", userRepo.findAll());
        model.addAttribute("title", "User list");
        return "settings/userList";
    }

    // This getMaping is for user profile, it`s been /user/{id}
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, @AuthenticationPrincipal User userLogo, Model model) {
        try {
            Config config = new Config(userRepo,logoService);
            config.getUserLogo(userLogo, model);

            model.addAttribute("user", user);
            model.addAttribute("title", "User edit");
            model.addAttribute("roles", Role.values());
        } catch (Exception e) {
            log.error("Error in userEditForm method: " + e.getMessage());
        }
        return "settings/userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            //For checkbox we need to use Map and get value from it
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {
        try {
            user.setUsername(username);

            Set<String> roles = Arrays.stream(Role.values())
                    .map(Role::name)
                    .collect(Collectors.toSet());


            if (form != null) {
                user.getRoles().clear();
                for (String key : form.keySet()) {
                    if (roles.contains(key)) {
                        user.getRoles().add(Role.valueOf(key));
                    }
                }
            }

            userRepo.save(user);
        } catch (Exception e) {
            log.error("Error in userSave method: " + e.getMessage());
        }

        return "redirect:/user";
    }


}
