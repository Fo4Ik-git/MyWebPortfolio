package com.fo4ik.mySite.service;

import com.fo4ik.mySite.config.WebSecurityConfig;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    // @Autowired
    private final UserRepo userRepo;
    @Autowired
    private MailSender mailSender;

    @Value("${host}")
    private String host;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUser(String username) {
        return userRepo.findByUsername(username);
    }

    public User getUser(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public Role getRole(String username, String roleName) {
        User user = userRepo.findByUsername(username);
        return user.getRoles().stream().filter(role -> role.name().equals(roleName)).findFirst().orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean addUser(User user, @RequestParam("voucher") String voucher) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        //Check if user already exist
        if (userFromDb != null) {
            return false;
        }

        if (!voucher.equals("UIF(EJFRDW+WFGJQ{PQD")) {
            return false;
        }

        System.out.println(user.toString());
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(WebSecurityConfig.getPasswordEncoder().encode(user.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Vlad Zinkovskyi site. Please, visit next link: <a href=\"http://$s:9000/activate/%s\">Activation link</a>",
                    user.getUsername(),
                    host,
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public void updateUser(User user) {
        userRepo.save(user);
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {

            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);
        return true;
    }

    public static Path createUserFolder(Long userId) {
        return getPath(userId, log);
    }

    private static Path getPath(Long userId, Logger log) {
        File file = new File("files/users/" + userId + "/");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            return Path.of(file.getPath());
        } catch (Exception e) {
            log.error("Error to create user folder: " + e.getMessage());
        }
        return null;
    }
}

