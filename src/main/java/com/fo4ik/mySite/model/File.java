package com.fo4ik.mySite.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @MapKeyColumn(name = "link")
    @Column(name = "url")
    @CollectionTable(name = "files_links", joinColumns = {@JoinColumn(name = "file_id", referencedColumnName = "id")})
    Map<String, String> links = new HashMap<>();

    private String name, version;


    public File(User user, String name, String version) {
        this.user = user;
        this.name = name;
        this.version = version;
    }

    public File() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }


    public void addLink(String url, String pathToFileInDB) {
        this.links.put(url, pathToFileInDB);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
