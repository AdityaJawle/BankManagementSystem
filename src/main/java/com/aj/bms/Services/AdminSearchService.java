package com.aj.bms.Services;

import com.aj.bms.Entity.Users;

import java.util.List;

public interface AdminSearchService {

    Users findByAccountNo(String accountNo);

    Users findByCrn(String crn);

    List<Users> findByRole(String role);

    List<Users> findByName(String name);
}
