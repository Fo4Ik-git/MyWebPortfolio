package com.fo4ik.mySite.repo;


import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface LogoRepo extends JpaRepository<Logo, Long> {
    Logo findById(long id);
    Logo findByUser(User userFromDb);
}
