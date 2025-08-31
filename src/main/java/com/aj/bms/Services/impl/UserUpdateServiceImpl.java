package com.aj.bms.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aj.bms.Entity.Users;
import com.aj.bms.Dao.UserRepository;
import com.aj.bms.Services.UserUpdateService;

import java.util.Optional;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users updateUserName(Long userId, String newName) {
        Users user = findUserOrThrow(userId);
        user.setName(newName);
        return userRepository.save(user);
    }


    @Override
    public Users updateStatus(Long userId, String newStatus) {
        Users user = findUserOrThrow(userId);
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    @Override
    public Users closeAccount(Long userId) {
        Users user = findUserOrThrow(userId);
        user.setStatus("INACTIVE");
        return userRepository.save(user);
    }

    private Users findUserOrThrow(Long id) {
        Optional<Users> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        Users user = userOpt.get();
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Admins cannot be updated via this method.");
        }
        return user;
    }
}
