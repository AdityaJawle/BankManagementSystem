package com.aj.bms.Services.impl;

import com.aj.bms.Entity.*;
import com.aj.bms.Dao.*;
import com.aj.bms.Services.UserAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatementRepository statementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Users deposit(String crn, double amount) {
        Users user = getUserByCrn(crn);

        if (!user.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new RuntimeException("Account is not active");
        }

        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        statementRepository.save(new UserStatement(
                null,
                user,
                LocalDateTime.now(),
                "DEPOSIT",
                amount,
                user.getBalance(),
                "Deposited ₹" + amount
        ));

        return user;
    }

    @Override
    public Users withdraw(String crn, double amount) {
        Users user = getUserByCrn(crn);

        if (!user.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new RuntimeException("Account is not active");
        }

        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        statementRepository.save(new UserStatement(
                null,
                user,
                LocalDateTime.now(),
                "WITHDRAW",
                amount,
                user.getBalance(),
                "Withdrawn ₹" + amount
        ));

        return user;
    }

    @Override
    public void transfer(String fromCrn, String toCrn, double amount) {
        if (fromCrn.equals(toCrn)) throw new RuntimeException("Cannot transfer to same account");

        Users sender = getUserByCrn(fromCrn);
        Users receiver = getUserByCrn(toCrn);

        if (!sender.getStatus().equalsIgnoreCase("ACTIVE") || !receiver.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new RuntimeException("Both accounts must be active");
        }

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(receiver);


        statementRepository.save(new UserStatement(
                null,
                sender,
                LocalDateTime.now(),
                "TRANSFER_OUT",
                amount,
                sender.getBalance(),
                "Transferred to CRN: " + receiver.getCrn()
        ));


        statementRepository.save(new UserStatement(
                null,
                receiver,
                LocalDateTime.now(),
                "TRANSFER_IN",
                amount,
                receiver.getBalance(),
                "Received from CRN: " + sender.getCrn()
        ));
    }

    @Override
    public Users getAccountDetails(String crn) {
        return getUserByCrn(crn);
    }

    private Users getUserByCrn(String crn) {
        return userRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("User not found with CRN: " + crn));
    }

    public void updatePin(String crn, String oldPin, String newPin) {
        Users user = userRepository.findByCrn(crn)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can change PIN here.");
        }

        if (!passwordEncoder.matches(oldPin, user.getPin())) {
            throw new RuntimeException("Old PIN is incorrect");
        }

        user.setPin(passwordEncoder.encode(newPin));
        userRepository.save(user);
    }

    public String getAdminName(String crn) {
        return userRepository.findByCrn(crn)
                .map(Users::getName)
                .orElse("Admin");
    }

    public String getUserName(String crn) {
        return userRepository.findByCrn(crn)
                .map(Users::getName)
                .orElse("User");
    }

    public Optional<Users> getAdminUser(String crn) {
        return userRepository.findByCrn(crn);
    }
    



}
