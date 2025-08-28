package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

public interface AccountManagementService {
    Users createUser(Users user, Users adminName);       // For ROLE_USER
    Users createAdmin(Users user, Users adminName);      // For ROLE_ADMIN
}
