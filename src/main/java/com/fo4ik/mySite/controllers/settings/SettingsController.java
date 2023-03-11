package com.fo4ik.mySite.controllers.settings;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.CvRepo;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.service.CvService;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final UserRepo userRepo;
    private final LogoService logoService;
    private final UserService userService;
    private final CvRepo cvRepo;
    private final CvService cvService;

    @Value("${user}")
    private String username;

    public SettingsController(UserRepo userRepo, LogoService logoService, UserService userService, CvRepo cvRepo, CvService cvService) {
        this.userRepo = userRepo;
        this.logoService = logoService;
        this.userService = userService;
        this.cvRepo = cvRepo;
        this.cvService = cvService;
    }

    @GetMapping("/settings")
    public String settingsPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("title", "Settings");
        try {
            Config config = new Config(userRepo, logoService);
            //TODO change logoRepo to logoService in config
            config.getUserLogo(user, model);
        } catch (Exception e) {
            log.error("Error in settings: " + e.getMessage());
        }
        return "settings/settings";
    }

    @PostMapping("/settings/addLogo")
    public String addLogo(@AuthenticationPrincipal User user, @RequestParam("logoFile") MultipartFile logoFile, Model model) {
        try {
            if (logoFile.getOriginalFilename().equals("")) {
                model.addAttribute("error", "File is empty");
                log.error("Error to add logo: File is empty");
                return "redirect:/settings";
            }

            User userFromDb = userService.findByUsername(user.getUsername());
            logoService.saveLogo(userFromDb, logoFile);

            //userRepo.save(userFromDb);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error to add logo: " + e.getMessage());
            return "redirect:/scheme";
        }

        return "redirect:/settings";
    }


    @PostMapping("/settings/userEdit")
    public String userEdit(
            @AuthenticationPrincipal User user, @RequestParam("username") String username, @RequestParam("name") String name,
            @RequestParam("description") String description, @RequestParam("mainTitle") String mainTitle, @RequestParam("mainDescription") String mainDescription, Model model) {
        try {
            User userFromDb = userRepo.findByUsername(username);
            if (userFromDb != null && userFromDb.getId() != user.getId()) {
                model.addAttribute("error", "User with this name already exists");
                return "redirect:/settings";
            }
            user.setUsername(username);
            user.setName(name);
            user.setMainTitle(mainTitle);
            user.setMainDescription(mainDescription);
            user.setDescription(description);
            userRepo.save(user);
            log.info("User " + user.getUsername() + " has been edited");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error to edit user: " + e.getMessage());
        }
        return "redirect:/settings";
    }

    @PostMapping("/settings/addProject")
    public String addProject(@AuthenticationPrincipal User user, @RequestParam("projectName") String projectName, @RequestParam("projectDescription") String projectDescription, Model model) {
        try {
            if (projectName.equals("")) {
                model.addAttribute("error", "Project name is empty");
                log.error("Error to add project: Project name is empty");
                return "redirect:/settings";
            }
            if (projectDescription.equals("")) {
                model.addAttribute("error", "Project description is empty");
                log.error("Error to add project: Project description is empty");
                return "redirect:/settings";
            }
            // Projects project = new Projects(projectName, projectDescription, user);
            //projectsRepo.save(project);
            // log.info("Project " + project.getName() + " has been added");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error to add project: " + e.getMessage());
            return "redirect:/settings";
        }
        return "redirect:/settings";
    }

    @PostMapping("/settings/uploadCV")
    public String uploadCV(@AuthenticationPrincipal User user, @RequestParam("cvFile") MultipartFile file, @RequestParam("cvImage") MultipartFile image, Model model) {

        try {

            //TODO create Service, in this create checkUser if user ==  null set user = userRepo.findByUsername(user.getUsername());
            if (user == null) {
                user = userRepo.findByUsername(username);
            }

            if (!file.isEmpty()) {
                cvService.saveCv(user, file, false);
            }
            if (!image.isEmpty()) {
                cvService.saveCv(user, image, true);
            }
        } catch (Exception e) {
            log.error("Error to upload CV: " + e.getMessage());
        }
        return "redirect:/settings";
    }

}
