package com.fo4ik.mySite.model;

import jakarta.persistence.*;

@Entity
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(nullable = false)
    private String name;

    private String description, imgPath;

    public Projects(String name, String description, String imgPath) {
        this.name = name;
        this.description = description;
        this.imgPath = imgPath;
    }

    public Projects() {
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
