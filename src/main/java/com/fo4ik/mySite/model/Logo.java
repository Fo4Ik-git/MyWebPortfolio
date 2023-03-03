package com.fo4ik.mySite.model;

import jakarta.persistence.*;

@Entity
public class Logo {


    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    private String path;
    private String name;

    public Logo(String path,String name, User user) {
        this.user = user;
        this.name = name;
        this.path = path;
    }

    public Logo() {
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
