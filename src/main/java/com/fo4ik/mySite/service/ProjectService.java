package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Project;
import com.fo4ik.mySite.repo.ProjectRepo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectService {

    private final ProjectRepo projectRepo;

    public ProjectService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }


    public void createProject(String name, String description, boolean inProgress, String util, String link) {
        Project project = new Project(name, description, inProgress, getUtils(util), getLinks(link));
        projectRepo.save(project);
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
