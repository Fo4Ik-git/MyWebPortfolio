package com.fo4ik.mySite.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Footer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ElementCollection
    @MapKeyColumn(name = "link")
    @Column(name = "urlImg")
    @CollectionTable(name = "footer_links", joinColumns = {@JoinColumn(name = "footer_id", referencedColumnName = "id")})
    Map<String, String> links = new HashMap<>();

    public Footer(Map<String, String> links) {
        this.links = links;
    }

    public Footer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
