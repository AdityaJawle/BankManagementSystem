package com.aj.bms.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "user_statements")
public class UserStatement {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    
    private LocalDateTime dateTime;

    private String transactionType;

    private double amount;

    private double balance;

    private String remarks;


     public UserStatement() {
    }

    

    public UserStatement(Long id, Users user, LocalDateTime dateTime, String transactionType, double amount,
            double balance, String remarks) {
        this.id = id;
        this.user = user;
        this.dateTime = dateTime;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balance = balance;
        this.remarks = remarks;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    
}
