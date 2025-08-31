package com.aj.bms.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aj.bms.Entity.*;
import java.util.*;

@Repository
public interface UserStatementRepository extends JpaRepository<UserStatement, Long> {

    List<UserStatement> findByUserIdOrderByDateTimeDesc(Long userId);
    List<UserStatement> findByUserCrn(String crn); 

    List<UserStatement> findByUser_CrnOrderByDateTimeDesc(String crn);


}
