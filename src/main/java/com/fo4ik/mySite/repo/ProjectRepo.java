package com.fo4ik.mySite.repo;

import com.fo4ik.mySite.model.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface ProjectRepo extends JpaRepository<Projects, Long> {
    Projects findById(long id);

}
