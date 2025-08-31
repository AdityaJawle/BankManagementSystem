package com.aj.bms.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aj.bms.Entity.Users;
import com.aj.bms.Dao.UserRepository;
import com.aj.bms.Services.AdminSearchService;

import java.util.List;

@Service
public class AdminSearchServiceImpl implements AdminSearchService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users findByAccountNo(String accountNo) {
        return userRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNo));
    }

    @Override
    public Users findByCrn(String crn) {
        return userRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("CRN not found: " + crn));
    }

    @Override
    public List<Users> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Users> findByRole(String role){
        return userRepository.findByRole(role);
    }

}
