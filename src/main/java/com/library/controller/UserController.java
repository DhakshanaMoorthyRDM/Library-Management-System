package com.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.entity.User;
import com.library.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {

        logger.info("Creating user with email: {}", user.getEmail());

        User savedUser = userService.saveUser(user);

        logger.info("User created successfully with id: {}", savedUser.getId());

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("Fetching all users");

        List<User> users = userService.getAllUsers();

        logger.info("Total users found: {}", users.size());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        logger.info("Fetching user with id: {}", id);

        User user = userService.getUserById(id);

        if (user == null) {
            logger.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("User found with id: {}", id);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        logger.info("Updating user with id: {}", id);

        User updatedUser = userService.updateUser(id, user);

        if (updatedUser == null) {
            logger.warn("Cannot update. User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("User updated successfully with id: {}", id);

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {

        logger.info("Activating user with id: {}", id);

        User user = userService.activateUser(id);

        if (user == null) {
            logger.warn("Cannot activate. User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("User activated successfully with id: {}", id);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {

        logger.info("Deactivating user with id: {}", id);

        User user = userService.deactivateUser(id);

        if (user == null) {
            logger.warn("Cannot deactivate. User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("User deactivated successfully with id: {}", id);

        return ResponseEntity.ok(user);
    }
}