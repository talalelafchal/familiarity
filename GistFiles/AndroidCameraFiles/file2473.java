package com.nastya.service;

import com.nastya.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(String login) {


            User user = new User();
            user.setLogin(login);
            user.setPassword("password");


        return user;
    }

}
