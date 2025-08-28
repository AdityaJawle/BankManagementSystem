package com.aj.bms.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false, unique = true)
    private String crn; // auto-generated e.g., "1000"

    @Column(nullable = false, unique = true)
    private String accountNo; // auto-generated e.g., "00001"

    private double balance;

    private String status; // ACTIVE or INACTIVE

    private String role; // ROLE_USER or ROLE_ADMIN

    // --- Constructors ---

    public Users() {
    }

    public Users(Long id, String name, String crn, String pin, String accountNo, double balance, String status, String role) {
        this.id = id;
        this.name = name;
        this.crn = crn;
        this.pin = pin;
        this.accountNo = accountNo;
        this.balance = balance;
        this.status = status;
        this.role = role;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
