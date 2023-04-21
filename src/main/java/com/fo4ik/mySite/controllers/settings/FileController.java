package com.fo4ik.mySite.controllers.settings;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.File;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.FileService;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Objects;

@Controller
@RequestMapping("/settings/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final String FOLDER = "pages/settings/files";
    private final UserService userService;
    private final LogoService logoService;
    private final FileService fileService;

    public FileController(UserService userService, LogoService logoService, FileService fileService) {
        this.userService = userService;
        this.logoService = logoService;
        this.fileService = fileService;
    }

    @GetMapping("")
    public String filePage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("title", "Files");
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("files", fileService.getAllFilesByUser(user));
        } catch (Exception e) {
            log.error("Error in file: " + e.getMessage());
        }
        return FOLDER + "/files";
    }

    @PostMapping("/addFile")
    public String addFile(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file,
                          @RequestParam("name") String name, @RequestParam("version") String version, Model model) {
        try {
            if (Objects.equals(file.getOriginalFilename(), "")) {
                model.addAttribute("error", "File is empty");
            } else if (name.isEmpty() || version.isEmpty()) {
                model.addAttribute("error", "Name or Version is empty");
            } else {
                fileService.saveFile(user, file, name, version);
            }
        } catch (Exception e) {
            log.error("Error in addFile: " + e.getMessage());
        }
        return "redirect:/settings/files";
    }


    @GetMapping("/editFile/{id}")
    public String editFile(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("title", "Edit File");
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("file", fileService.getFile(id));
        } catch (Exception e) {
            log.error("Error in editFile: " + e.getMessage());
        }
        return FOLDER + "/fileEdit";
    }

    @PostMapping("/editFile/{id}")
    public String editFile(@PathVariable("id") long id, @AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file,
                           @RequestParam("name") String name, @RequestParam("version") String version, Model model) {
        log.warn("File: " + file.getOriginalFilename());
        try {
            if (Objects.equals(file.getOriginalFilename(), "")) {
                model.addAttribute("error", "File is empty");
            } else if (name.isEmpty() || version.isEmpty()) {
                model.addAttribute("error", "Name or Version is empty");
            } else {
                fileService.updateFile(id, user, file, name, version);
                log.info("File: " + file.getOriginalFilename() + " was saved");
            }
        } catch (Exception e) {
            log.error("Error in editFile: " + e.getMessage());
        }
        return "redirect:/settings/files";
    }

    @PostMapping("/deleteFile/{id}")
    public String deleteFile(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model) {
        try {
            fileService.deleteFile(id);
        } catch (Exception e) {
            log.error("Error in deleteFile: " + e.getMessage());
        }
        return "redirect:/settings/files";
    }




}
