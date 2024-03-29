package com.fo4ik.mySite.controllers.project;

import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Project;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.ProjectService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class ProjectsController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);
    private final UserService userService;

    private final LogoService logoService;
    private final ProjectService projectService;

    public ProjectsController(UserService userService, LogoService logoService,  ProjectService projectService) {
        this.userService = userService;
        this.logoService = logoService;
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            Config config = new Config(userService, logoService);

            if (user != null) {
                config.getUserLogo(user, model);
            }

            Iterable<Project> projectsFalse = projectService.getAll().stream()
                    .filter(project -> !project.isInProgress())
                    .collect(Collectors.toList());
            if (projectsFalse.iterator().hasNext()) {
                model.addAttribute("projectsFalse", projectsFalse);
            }

            Iterable<Project> projectsTrue = projectService.getAll().stream()
                    .filter(Project::isInProgress)
                    .collect(Collectors.toList());
            if (projectsTrue.iterator().hasNext()) {
                model.addAttribute("projectsTrue", projectsTrue);
            }

        } catch (Exception e) {
            log.error("Error in projects: " + e.getMessage());
        }

        return "pages/projects/projects";
    }


    @GetMapping("/projects/add")
    public String addProject(@AuthenticationPrincipal User user, Model model) {
        Config config = new Config(userService, logoService);
        config.getUserLogo(user, model);
        model.addAttribute("title", "Add project");
        return "pages/projects/projectAdd";
    }

    @PostMapping("/projects/add")
    public String addProject(
            @AuthenticationPrincipal User user, Model model, @RequestParam("InputName") String name,
            @RequestParam("InputDescription") String description, @RequestParam("InputLinks") String link,
            @RequestParam("InputUtils") String util, boolean inProgress) {
        try {
            if (name.isEmpty() || description.isEmpty() || util.isEmpty()) {
                model.addAttribute("error", "Please fill all fields");
                return "pages/projects/projectAdd";
            }
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Add project");

            if (link.isEmpty()) {
                projectService.createProject(name, description, inProgress, util, null, user);
            } else {
                projectService.createProject(name, description, inProgress, util, link, user);
            }


        } catch (Exception e) {
            log.error("Error in addProject: " + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }
        return "redirect:/projects";
    }

    @GetMapping("/projects/edit/{id}")
    public String editProject(@AuthenticationPrincipal User user, @PathVariable(value = "id") long id, Model model) {
        Config config = new Config(userService, logoService);
        config.getUserLogo(user, model);

        Project project = projectService.getProject(id);
        model.addAttribute("project", project);
        model.addAttribute("title", "Edit project");
        return "pages/projects/projectsEdit";
    }

    @PostMapping("/projects/edit/{id}")
    public String editProject(
            @AuthenticationPrincipal User user, @PathVariable(value = "id") long id, Model model,
            @RequestParam("InputName") String name,
            @RequestParam("InputDescription") String description, @RequestParam("InputLinks") String link,
            @RequestParam("InputUtils") String util, boolean inProgress) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Edit project");

            if (link.isEmpty()) {
                projectService.updateProject(id, name, description, inProgress, util, null);
            } else {
                projectService.updateProject(id, name, description, inProgress, util, link);
            }
        } catch (Exception e) {
            log.error("Error in editProject: " + e.getMessage());

        }
        return "redirect:/projects";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@AuthenticationPrincipal User user, @PathVariable(value = "id") long id, Model model) {
        try {
            projectService.deleteProject(id);

        } catch (Exception e) {
            log.error("Error in deleteProject: " + e.getMessage());
        }
        return "redirect:/projects";
    }
}

