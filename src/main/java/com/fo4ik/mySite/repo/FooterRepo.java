package com.fo4ik.mySite.repo;

import com.fo4ik.mySite.model.Footer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface FooterRepo extends JpaRepository<Footer, Long> {

    List<Footer> findAll();


}
