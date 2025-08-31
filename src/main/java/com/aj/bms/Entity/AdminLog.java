package com.aj.bms.Entity;


import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "admin_user_id", nullable = false)
    private Users adminUser;

    private LocalDateTime dateTime;

    private String actionType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "target_user_id")
    private Users targetUser;

    private String description;

    public AdminLog() {
    }

    public AdminLog(Long id, Users adminUser, LocalDateTime dateTime, String actionType, Users targetUser, String description) {
        this.id = id;
        this.adminUser = adminUser;
        this.dateTime = dateTime;
        this.actionType = actionType;
        this.targetUser = targetUser;
        this.description = description;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(Users adminUser) {
        this.adminUser = adminUser;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Users getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(Users targetUser) {
        this.targetUser = targetUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
