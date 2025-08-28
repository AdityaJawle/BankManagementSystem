package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

public interface UserUpdateService {

    Users updateUserName(Long userId, String newName);

    Users updateCrn(Long userId, String newCrn);

    Users updateStatus(Long userId, String newStatus); // ACTIVE / INACTIVE

    Users closeAccount(Long userId); // permanently deactivate
}
