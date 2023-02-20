package com.fo4ik.mySite.service;

import com.fo4ik.mySite.config.WebSecurityConfig;
import com.fo4ik.mySite.model.Role;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    // @Autowired
    private final UserRepo userRepo;
    @Autowired
    private  MailSender mailSender;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
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
                            "Welcome to Vlad Zinkovskyi site. Please, visit next link: <a href=\"http://localhost:9000/activate/%s\">Activation link</a>",
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
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
}

