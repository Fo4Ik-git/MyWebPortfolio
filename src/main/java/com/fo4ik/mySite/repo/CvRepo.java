package com.fo4ik.mySite.repo;

import com.fo4ik.mySite.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface CvRepo extends JpaRepository<Cv, Long> {

    List<Cv> findAll();
    Cv findById(long id);

}
