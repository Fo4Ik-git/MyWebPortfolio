package com.fo4ik.mySite.controllers.settings;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Cv;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.CvRepo;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final UserRepo userRepo;
    private final LogoRepo logoRepo;
    //TODO delete logoRepo
    private final LogoService logoService;
    private final UserService userService;
    private final CvRepo cvRepo;

    public SettingsController(UserRepo userRepo, LogoRepo logoRepo, LogoService logoService, UserService userService, CvRepo cvRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;
        this.logoService = logoService;
        this.userService = userService;
        this.cvRepo = cvRepo;
    }

    @GetMapping("/settings")
    public String settingsPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("title", "Settings");
        try {
            Config config = new Config(userRepo, logoRepo);
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
    public String uploadCV(@AuthenticationPrincipal User user, @RequestParam("cvFile") MultipartFile file, @RequestParam("cvImage") MultipartFile image, Model model, Long userId) {
        Path path = Path.of("files/users/" + user.getId() + "/pdf/");
        Cv cv = new Cv();
        try {

            if (cvRepo.findById(1) != null) {
                cv = cvRepo.findById(1);
            }

            if (!file.isEmpty()) {

                byte[] bytes = file.getBytes();
                String fileName = "cv.pdf";
                Path filePath = Path.of(path + "/" + fileName);
                File disk = new File(path.toString());
                if (!disk.exists()) {
                    disk.mkdirs();
                }
                cv.setFilePath(String.valueOf(filePath));
                cvRepo.save(cv);
                Files.write(Path.of(path + "/" + fileName), bytes);

            }
            if (!image.isEmpty()) {
                byte[] bytes = image.getBytes();
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                Path imagePath = Path.of(path + "/" + "cv.png");
                File output = new File(String.valueOf(imagePath));


                if (logoService.getLogo("cv") != null) {
                    logoService.delete(logoService.getLogo("cv"));
                }

                cv.setImgPath(String.valueOf(imagePath));
                cvRepo.save(cv);

                ImageIO.write(bufferedImage, "png", output);

            }
        } catch (IOException e) {
            log.error("Error to upload CV: " + e.getMessage());
        }
        return "redirect:/settings";
    }

}
