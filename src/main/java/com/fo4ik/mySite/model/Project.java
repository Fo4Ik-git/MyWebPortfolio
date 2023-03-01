package com.fo4ik.mySite.model;

import jakarta.persistence.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private boolean inProgress;

    @ElementCollection
    @CollectionTable(name = "project_utils")
    @Column(name = "util")
    private List<String> utils;

    @ElementCollection
    @MapKeyColumn(name = "link")
    @Column(name = "urlImg")
    @CollectionTable(name = "project_links", joinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "id")})
    Map<String, String> links = new HashMap<>();

    public Project(String name, String description, boolean inProgress, List<String> utils, Map<String, String> links) {
        this.name = name;
        this.description = description;
        this.inProgress = inProgress;
        this.utils = utils;
        this.links = links;
    }

    public Project(String name, String description, boolean inProgress, List<String> utils) {
        this.name = name;
        this.description = description;
        this.inProgress = inProgress;
        this.utils = utils;
    }

    public Project() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public List<String> getUtils() {
        return utils;
    }

    public String getUtilsString() {
        return utils.stream().collect(Collectors.joining(", "));
    }

    public void setUtils(List<String> utils) {
        this.utils = utils;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public String getLinksString() {
        return links.entrySet().stream().map(entry -> entry.getKey() + ", " + entry.getValue() + ";\n").collect(Collectors.joining(""));
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

}
