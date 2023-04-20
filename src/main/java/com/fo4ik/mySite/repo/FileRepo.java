package com.fo4ik.mySite.repo;

import com.fo4ik.mySite.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface FileRepo extends JpaRepository<File, Long> {

    List<File> findAll();
    List<File> findAllByUser_Id(long id);

    File findById(long id);

    @Query("SELECT f FROM File f JOIN f.links l WHERE KEY(l) = :link")
    File findByLink(@Param("link") String link);
}
