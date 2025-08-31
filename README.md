# 💳 Bank Management System

A full-fledged enterprise-level banking application built using Spring Boot, Thymeleaf, and MySQL.  
This application simulates a real-world bank where only the **admin can create accounts** (users or admins). It includes security, role-based access, user management, transactions, and logs.

---

## 📌 Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
  - [Admin Features](#admin-features)
  - [User Features](#user-features)
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [Security](#security)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## 🔍 Project Overview

This is a secure Bank Management System with role-based login for Admin and Users.

- Only Admins can create new accounts.
- Users can deposit, withdraw, transfer money, and view statements.
- Super Admin (CRN `1000`, PIN `admin123`) is auto-created at startup.
- Admins can manage accounts and view their own action logs.

---

## ⚙️ Tech Stack

- **Spring Boot** (v3.5.4)
- **Spring Security**  
- **Spring Data JPA + Hibernate**
- **MySQL**  
- **Thymeleaf** (HTML Templating)  
- **BCrypt Password Encryption**  
- **Maven**

---

## 🚀 Features

### 👨‍💼 Admin Features

| Feature | Description |
|--------|-------------|
| **Dashboard** | Search users by CRN, Account No, Name, or Closed status. View, update, and manage accounts. |
| **Create User** | Add new users with Name, PIN, and initial balance. CRN & Account No are auto-generated. |
| **Create Admin** | Create other admins (Name + PIN only). CRN & Account No are auto-generated. |
| **Change PIN** | Logged-in admin can update their own PIN. |
| **Update User Account** | Modify balance (with +/-), status (`ACTIVE`/`INACTIVE`), reset PIN to `0000`, or close account. |
| **Admin Logs** | View history of actions performed by the logged-in admin. |
| **Logout** | Safely log out from the session. |

> 🔒 Only users with `ACTIVE` status can log in. Closed/Inactive users are blocked by the system.

---

### 🙋‍♂️ User Features

| Feature | Description |
|--------|-------------|
| **Dashboard** | Shows CRN, Account No, Balance, Name. |
| **Deposit** | Add funds to the account. Minimum amount is 1. |
| **Withdraw** | Withdraw money (not exceeding current balance). |
| **Money Transfer** | Send money to another user by CRN. Name preview appears for verification. |
| **Statement** | View transaction history with date, time, type, amount, balance, and remarks. |
| **Logout** | Safely end session. |

---

## 🖼️ Screenshots

> Replace these with actual images or add them to a `/screenshots/` folder in your GitHub repo.

| Page | Screenshot |
|------|------------|
| Login Page | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/56210ddf-d7a3-4c73-87f5-d6941e710621" /> |
| Admin Dashboard | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/f93ac2bd-27d2-4a74-894a-077fb9c4b610" /> |
| Create User/Admin | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/91edb271-cabf-4415-89d9-30a7aabeccee" />,
                      ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/2cf9b0da-71dc-4361-a3e9-a6029465480f" /> |
| Update User | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/17ea98ff-4f2c-4145-b147-b87c1fee7637" /> |
| Admin Logs | ![] <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b20646cd-e433-4a68-874c-a98156fd86cd" /> |
| User Dashboard | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/23f52adc-2944-4be8-90b7-04d25fcf5828" /> |
| Deposit Page | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b7c52b8d-2d28-42f9-b19c-973546fd745a" /> |
| Withdraw Page | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/0a4096fe-31e7-4543-89ec-37f7ebed5dc2" /> |
| Transfer Page | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/c95d921c-865b-4f8f-9c7d-c8e12fa0de62" /> |
| Statement Page | ![]<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/61a7c4e0-e360-4c4a-9002-16c0b3da383d" /> |

---

## 🔐 Security

- **Spring Security** protects all routes.
- **BCrypt encryption** for storing PINs securely.
- **Role-based access** using `ROLE_ADMIN` and `ROLE_USER`.
- Inactive or closed accounts **cannot log in**.

---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- MySQL
- Maven

### Clone the Repository

```bash
git clone https://github.com/yourusername/BankManagementSystem.git
cd BankManagementSystem
```

### Setup Database

``` Create a MySQL database (e.g., bms_db) and configure your application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/bms_db
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update

```

### Build & Run
```
mvn clean install
mvn spring-boot:run
```

### Visit: http://localhost:8080/login
```
Super Admin credentials:
CRN: 1000
PIN: admin123
```


## 📝 License

- **This project is open-source and free to use.**
