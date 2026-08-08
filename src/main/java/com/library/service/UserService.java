package com.library.service;

import java.util.List;

import com.library.entity.User;

public interface UserService {

    User saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(Long id, User user);

    User activateUser(Long id);

    User deactivateUser(Long id);
}