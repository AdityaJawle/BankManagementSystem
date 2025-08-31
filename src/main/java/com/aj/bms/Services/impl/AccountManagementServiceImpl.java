package com.aj.bms.Services.impl;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aj.bms.Entity.AdminLog;
import com.aj.bms.Entity.Users;
import com.aj.bms.Dao.*;
import com.aj.bms.Services.AccountManagementService;


import lombok.AllArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority; 

@Service
@AllArgsConstructor
public class AccountManagementServiceImpl implements AccountManagementService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AccountManagementService.class);
    private final UserRepository userRepository;
    private final AdminLogRepository adminLogRepository;

    @Override
    public Users createUser(Users user, Users adminName) {
        return createUserWithRole(user, "ROLE_USER", adminName);
    }

    @Override
    public Users createAdmin(Users user, Users adminName) {
        return createUserWithRole(user, "ROLE_ADMIN", adminName);
    }


    @Override
    public UserDetails loadUserByUsername(String crn) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByCrn(crn);
        if (user.isPresent()) {
            var userObj = user.get();
            return new org.springframework.security.core.userdetails.User(
                userObj.getCrn(),
                userObj.getPin(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userObj.getRole())) 
            );
        } else {
            logger.error("User not found: {}", crn);
            throw new UsernameNotFoundException("User not found: " + crn);
        }
    }


    public Users createUserWithRole(Users user, String role, Users adminUser) {
        user.setCrn(generateNextCrn());
        user.setAccountNo(generateNextAccountNo());
        user.setStatus("ACTIVE");
        user.setRole(role);
        user.setBalance(user.getBalance() == 0 ? 0.0 : user.getBalance());

        AdminLog log = new AdminLog();
        log.setAdminUser(adminUser);
        log.setTargetUser(user);
        log.setDateTime(LocalDateTime.now());
        log.setActionType("CREATE_ACCOUNT");
        log.setDescription("Created user " + user.getName());

        adminLogRepository.save(log);

        return userRepository.save(user);
    }

    private String generateNextCrn() {
        Optional<Users> lastUser = userRepository.findTopByOrderByCrnDesc();
        int nextCrn = lastUser.map(u -> Integer.parseInt(u.getCrn())).orElse(999) + 1;
        return String.valueOf(nextCrn);
    }

    private String generateNextAccountNo() {
        Optional<Users> lastUser = userRepository.findTopByOrderByAccountNoDesc();
        int nextAcc = lastUser.map(u -> Integer.parseInt(u.getAccountNo())).orElse(0) + 1;
        return String.format("%05d", nextAcc);
    }


    public void logAction(String actionType, Users adminUser, Users targetUser) {
        AdminLog log = new AdminLog();
        log.setActionType(actionType);
        log.setAdminUser(adminUser);
        log.setTargetUser(targetUser);
        log.setDateTime(LocalDateTime.now());
        // log.setActionType("UPDATE_DETAILS");
        log.setDescription("Update User " + targetUser.getName());
        adminLogRepository.save(log);
    }

}
