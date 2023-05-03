package com.fo4ik.mySite.repo;

import com.fo4ik.mySite.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface BlogRepo extends JpaRepository<Blog, Long> {

    Blog findById(long id);

    List<Blog> findAll();

    List<Blog> findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(String query, String query1);
}
