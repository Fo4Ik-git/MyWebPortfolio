package com.fo4ik.mySite.controllers.settings;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final LogoRepo logoRepo;
    private final UserRepo userRepo;

    public SettingsController(LogoRepo logoRepo, UserRepo userRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;
    }

    @GetMapping("/settings")
    public String settingsPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("title", "Settings");
        try {
            Config config = new Config(userRepo, logoRepo);
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

            Logo logos = logoRepo.findById(user.getId());
            if (logos != null) {
                logos.setPath(saveLogo(user, logoFile, model));
                logoRepo.save(logos);
                log.info("Logo for user " + user.getUsername() + " has been changed, new logo: " + logos.getPath());
            }
            else {
                Logo logo = new Logo(saveLogo(user, logoFile, model), user);
                logo.setId(user.getId());
                logoRepo.save(logo);
                log.info("Logo for user " + user.getUsername() + " has been added, new logo: " + logo.getPath());
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error to add logo: " + e.getMessage());
            return "redirect:/scheme";
        }

        return "redirect:/settings";
    }

    private String saveLogo(User user, MultipartFile logoFile, Model model) {
        try {
            String format = "logo." + StringUtils.getFilenameExtension(logoFile.getOriginalFilename());
            Path folder = Path.of(createUserFolder(user.getId()) + "/");
            Path path = folder.resolve(format);
            Files.write(path, logoFile.getBytes());

            log.info("Logo for user " + user.getUsername() + " has been saved");

            return path.toString();
        } catch (IOException e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error to save logo: " + e.getMessage());
            return null;
        }
    }

    public Path createUserFolder(Long userId) {
        File file = new File("files/users/" + userId + "/");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            return Path.of(file.getPath());
        } catch (Exception e) {
            log.error("Error to create user folder: " + e.getMessage());
        }
        return null;
    }

    @PostMapping("/settings/userEdit")
    public String userEdit(
            @AuthenticationPrincipal User user,@RequestParam("username") String username, @RequestParam("name") String name,
                           @RequestParam("description") String description, @RequestParam("mainTitle") String mainTitle,@RequestParam("mainDescription") String mainDescription, Model model) {
        try {
            User userFromDb = userRepo.findByUsername(username);
            if(userFromDb != null && userFromDb.getId() != user.getId()) {
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
    public String uploadCV(@AuthenticationPrincipal User user, @RequestParam("cvFile") MultipartFile file, Model model, Long userId){
        if(!file.isEmpty()){
            try{
                byte[] bytes = file.getBytes();
                Path path = Path.of("files/users/" + user.getId() + "/cv.pdf") ;
                Files.write(path, bytes);
                return "redirect:/settings";
            } catch (IOException e) {
                log.error("Error to upload CV: " + e.getMessage());
            }
        }
        return "redirect:/settings";
    }

}
