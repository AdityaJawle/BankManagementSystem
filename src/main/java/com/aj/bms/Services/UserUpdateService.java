package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

public interface UserUpdateService {

    Users updateUserName(Long userId, String newName);
    Users updateStatus(Long userId, String newStatus);
    Users closeAccount(Long userId); 
}
