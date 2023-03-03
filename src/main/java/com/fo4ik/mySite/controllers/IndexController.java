package com.fo4ik.mySite.controllers;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.config.MvcConfig;
import com.fo4ik.mySite.model.Cv;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.CvRepo;
import com.fo4ik.mySite.repo.LogoRepo;
import com.fo4ik.mySite.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class IndexController {

    private final UserRepo userRepo;
    private final LogoRepo logoRepo;
    private final CvRepo cvRepo;
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    public IndexController(UserRepo userRepo, LogoRepo logoRepo, CvRepo cvRepo) {
        this.userRepo = userRepo;
        this.logoRepo = logoRepo;
        this.cvRepo = cvRepo;
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
    public String cv(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user != null) {
                Config config = new Config(userRepo, logoRepo);
                config.getUserLogo(user, model);
            }
            Cv cv = new Cv();
            if (cvRepo.findById(1) != null) {
                cv = cvRepo.findById(1);
            }

            String filePath = cv.getFilePath();
            if (MvcConfig.isWindows()) {
                filePath = filePath.replace("\\", "/");
            }

            model.addAttribute("cvImagePath", cv.getImgPath());
            model.addAttribute("cvFilePath", filePath);


        } catch (Exception e) {
            log.error("Error in cv: " + e.getMessage());
        }
        return "cv";
    }


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String filePath) {
        Path path = Paths.get(System.getProperty("user.dir") + filePath);

        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


}
