package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

public interface AccountManagementService {
    Users createUser(Users user, Users adminName);       
    Users createAdmin(Users user, Users adminName); 

}
