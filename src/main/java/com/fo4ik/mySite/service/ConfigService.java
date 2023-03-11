package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {


    //TODO merge all methods from config class to this service
    @Value("${user}")
    private String username;

    private final UserService userService;

    public ConfigService(UserService userService) {
        this.userService = userService;
    }

    public User checkUser(User user) {
        if (user == null) {
            return userService.getUser(username);
        } else return user;
    }

}
