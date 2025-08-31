package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

public interface UserAccountService {

    Users deposit(String crn, double amount);

    Users withdraw(String crn, double amount);

    void transfer(String fromCrn, String toCrn, double amount);

    Users getAccountDetails(String crn);

    void updatePin(String crn, String oldPin, String newPin);

}
