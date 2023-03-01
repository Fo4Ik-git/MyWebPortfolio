package com.fo4ik.mySite.model;

import jakarta.persistence.*;

import java.util.List;

public class Skills {


    @Id
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    private String name;
    @ElementCollection
    private List<String> skills;

    public Skills(User user, String name, List<String> skills) {
        this.user = user;
        this.name = name;
        this.skills = skills;
    }

    public Skills() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
