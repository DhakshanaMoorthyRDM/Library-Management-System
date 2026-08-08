package com.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElse(null);
    }

    @Override
    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id)
                .orElse(null);

        if (existingUser == null) {
            return null;
        }

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());

        return userRepository.save(existingUser);
    }

    @Override
    public User activateUser(Long id) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return null;
        }

        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return null;
        }

        user.setStatus("INACTIVE");

        return userRepository.save(user);
    }
}