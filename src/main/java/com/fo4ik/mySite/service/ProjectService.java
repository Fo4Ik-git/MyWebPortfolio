package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Project;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.ProjectRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepo projectRepo;

    public ProjectService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    public List<Project> getAll() {
        return projectRepo.findAll();
    }

    public Project getProject(long id) {
        return projectRepo.findById(id);
    }

    public void createProject(String name, String description, boolean inProgress, String util, String link, User user) {
        Project project = new Project(name, description, inProgress, getUtils(util), getLinks(link), user);
        projectRepo.save(project);
        log.info("Project " + name + " for user: " + user.getUsername() + " has been created");
    }

    private List<String> getUtils(String util) {
        String[] items = util.trim().split(",");
        return new ArrayList<>(Arrays.asList(items));
    }

    private Map<String,String> getLinks(String link) {
        if (link == null || link.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> links = new HashMap<>();
        String[] pairs = link.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split(",");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                links.put(key, value);
            }
        }
        return links;
    }

    public void deleteProject(long id) {
        Project project = projectRepo.findById(id);
        projectRepo.delete(project);
    }

    public void updateProject(long id, String name, String description, boolean inProgress, String util,String link){
        try {
            Project project = projectRepo.findById(id);
            project.setName(name);
            project.setDescription(description);
            project.setInProgress(inProgress);
            project.setUtils(getUtils(util));
            project.setLinks(getLinks(link));
            projectRepo.save(project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
