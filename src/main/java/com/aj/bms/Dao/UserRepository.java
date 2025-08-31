package com.aj.bms.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aj.bms.Entity.*;
import java.util.*;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByCrn(String crn);
    Optional<Users> findByAccountNo(String accountNo);
    List<Users> findByNameContainingIgnoreCase(String name);
    List<Users> findByRole(String role);
    Optional<Users> findTopByOrderByCrnDesc();
    Optional<Users> findTopByOrderByAccountNoDesc();
    Optional<Users> findByName(String name);
    List<Users> findByStatus(String status);

}
