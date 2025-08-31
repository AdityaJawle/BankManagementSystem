package com.aj.bms.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aj.bms.Entity.*;
import java.util.*;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    List<AdminLog> findByAdminUserIdOrderByDateTimeDesc(Long adminId);
    List<AdminLog> findByTargetUserId(Long targetUserId);


}
