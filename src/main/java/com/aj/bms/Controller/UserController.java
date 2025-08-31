package com.aj.bms.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.aj.bms.Entity.*;
import com.aj.bms.Services.*;
import com.aj.bms.Services.impl.*;
import com.aj.bms.Dao.*;

@Controller
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountManagementServiceImpl accountManagementServiceImpl;

    @Autowired
    private UserAccountServiceImpl userAccountServiceImpl;

    @Autowired
    private AdminSearchService adminSearchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatementRepository userStatementRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    @Autowired
    private UserAccountService userAccountService;

    // Login Page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Admin Dashboard
    @GetMapping("/admin/adminDashboard")
    public String showAdminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String crn = auth.getName();

        Users admin = adminSearchService.findByCrn(crn);
        List<Users> users = adminSearchService.findByRole("USER");

        model.addAttribute("adminName", admin.getName());
        model.addAttribute("users", users);
        return "admin/adminDashboard";
    }

    @GetMapping("/user/userDashboard")
    public String showUserDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String crn = auth.getName();

        Users users = adminSearchService.findByCrn(crn);
        model.addAttribute("userName", users.getName());
        model.addAttribute("users", users);
        return "user/userDashboard";
    }

    // Create User
    @GetMapping("/admin/create-user")
    public String showUserRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new Users());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String crn = auth.getName();
        Users admin = adminSearchService.findByCrn(crn);
        model.addAttribute("adminName", admin.getName());

        return "admin/create-user";
    }


    // Create Admin
    @GetMapping("/admin/create-admin")
    public String showAdminRegistrationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String crn = auth.getName();
        Users admin = adminSearchService.findByCrn(crn);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("user", new Users());
        return "admin/create-admin";
    }

    @PostMapping("/admin/create-user")
    public String userSave(@ModelAttribute Users user, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            user.setPin(passwordEncoder.encode(user.getPin()));
            Users adminName = adminSearchService.findByCrn(principal.getName());
            accountManagementServiceImpl.createUserWithRole(user, "USER",adminName);
            redirectAttributes.addFlashAttribute("newUser", user);
            return "redirect:/admin/adminDashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            String crn = principal.getName();
            Users admin = adminSearchService.findByCrn(crn);
            model.addAttribute("adminName", admin.getName());
            return "admin/create-user";
        }
    }

    @PostMapping("/admin/create-admin")
    public String adminSave(@ModelAttribute Users user, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            user.setPin(passwordEncoder.encode(user.getPin()));
            Users adminName = adminSearchService.findByCrn(principal.getName());
            accountManagementServiceImpl.createUserWithRole(user, "ADMIN", adminName);
            redirectAttributes.addFlashAttribute("newUser", user);
            return "redirect:/admin/adminDashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("error", e.getMessage());
            String crn = principal.getName();
            Users admin = adminSearchService.findByCrn(crn);
            model.addAttribute("adminName", admin.getName());
            model.addAttribute("user", user);
            return "admin/create-admin";
        }
    }

    @GetMapping("/admin/change-pin")
    public String showChangePinPage(Model model, Principal principal) {
        String crn = principal.getName();
        String adminName = userAccountServiceImpl.getAdminName(crn);
        model.addAttribute("adminName", adminName);
        return "admin/change-pin";
    }



    @PostMapping("/admin/change-pin")
    public String changeAdminPin(@RequestParam("oldPin") String oldPin,
                            @RequestParam("newPin") String newPin,
                            @RequestParam("confirmPin") String confirmPin,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        Optional<Users> optionalUser = userRepository.findByCrn(principal.getName());

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (!passwordEncoder.matches(oldPin, user.getPin())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Old PIN is incorrect.");
                return "redirect:/user/change-pin";
            }

            if (!newPin.equals(confirmPin)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New PIN and Confirm PIN do not match.");
                return "redirect:/user/change-pin";
            }

            user.setPin(passwordEncoder.encode(newPin));
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "PIN changed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }

        return "redirect:/admin/change-pin";
    }

    @GetMapping("/admin/adminLogs")
        public String viewAdminLogs(Model model, Principal principal) {
            String crn = principal.getName();
            Users admin = userRepository.findByCrn(crn)
                                        .orElseThrow(() -> new RuntimeException("Admin not found"));

            List<AdminLog> logs = adminLogRepository.findByAdminUserIdOrderByDateTimeDesc(admin.getId());

            model.addAttribute("logs", logs);
            model.addAttribute("adminName", admin.getName());
            return "admin/adminLogs";
        }

    @GetMapping("/admin/update-user/{crn}")
    public String showUpdateUserForm(@PathVariable String crn, Model model, Principal principal) {

        Users admin = adminSearchService.findByCrn(principal.getName());
        model.addAttribute("adminName", admin.getName());

        Optional<Users> optionalUser = userRepository.findByCrn(crn);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
        } else {
            return "redirect:/admin/adminDashboard";
        }

        return "admin/update-user";
    }

    @PostMapping("/admin/update-user/{crn}")
    public String updateUser(
            @PathVariable String crn,
            @ModelAttribute("user") Users updatedUser,
            @RequestParam(name = "balanceUpdateDescription", required = false) String description,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        Optional<Users> optionalUser = userRepository.findByCrn(crn);

        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();

            if ("INACTIVE".equals(existingUser.getStatus())) {
                redirectAttributes.addFlashAttribute("message", "Cannot update INACTIVE user.");
                return "redirect:/admin/update-user/" + crn;
            }

            double oldBalance = existingUser.getBalance();
            double newBalance = updatedUser.getBalance();

            existingUser.setName(updatedUser.getName());
            existingUser.setBalance(newBalance);

            userRepository.save(existingUser);

            Users admin = adminSearchService.findByCrn(principal.getName());

            accountManagementServiceImpl.logAction("UPDATE_USER_DETAILS", admin, existingUser);

            if (Double.compare(oldBalance, newBalance) != 0) {
                String txnType = newBalance > oldBalance ? "CREDIT" : "DEBIT";
                double txnAmount = Math.abs(newBalance - oldBalance);

                UserStatement statement = new UserStatement();
                statement.setUser(existingUser);
                statement.setDateTime(LocalDateTime.now());
                statement.setTransactionType(txnType);
                statement.setAmount(txnAmount);
                statement.setBalance(newBalance);
                statement.setRemarks(description != null ? description : "Balance adjusted by admin");

                userStatementRepository.save(statement); 
            }

            redirectAttributes.addFlashAttribute("message", "User updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found.");
        }

        return "redirect:/admin/update-user/" + crn;
    }


    @PostMapping("/admin/reset-pin/{crn}")
    public String resetPin(@PathVariable String crn, RedirectAttributes redirectAttributes, Principal principal) {
        Optional<Users> optionalUser = userRepository.findByCrn(crn);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (!"INACTIVE".equals(user.getStatus())) {
                user.setPin(passwordEncoder.encode("0000"));
                userRepository.save(user);

                Users admin = adminSearchService.findByCrn(principal.getName());
                accountManagementServiceImpl.logAction("RESET_PIN", admin, user);

                redirectAttributes.addFlashAttribute("message", "PIN reset to 0000 successfully.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Cannot reset PIN of INACTIVE user.");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found.");
        }

        return "redirect:/admin/update-user/" + crn;
    }

    @PostMapping("/admin/close-account/{crn}")
    public String closeAccount(@PathVariable String crn, RedirectAttributes redirectAttributes, Principal principal) {
        Optional<Users> optionalUser = userRepository.findByCrn(crn);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (!"INACTIVE".equals(user.getStatus())) {
                user.setStatus("INACTIVE");
                userRepository.save(user);

                Users admin = adminSearchService.findByCrn(principal.getName());
                accountManagementServiceImpl.logAction("CLOSE_ACCOUNT", admin, user);

                redirectAttributes.addFlashAttribute("message", "Account closed successfully.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Account is already inactive.");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found.");
        }

        return "redirect:/admin/update-user/" + crn;
    }

    @GetMapping("/admin/search")
    public String searchUsers(@RequestParam("query") String query,
                            @RequestParam("sortBy") String sortBy,
                            Model model,
                            Principal principal) {

        Users admin = adminSearchService.findByCrn(principal.getName());
        model.addAttribute("adminName", admin.getName());

        List<Users> users = new ArrayList<>();

        switch (sortBy.toLowerCase()) {
        case "crn":
            Optional<Users> userByCrn = userRepository.findByCrn(query);
            userByCrn.ifPresent(users::add);
            break;

        case "name":
            users = userRepository.findByNameContainingIgnoreCase(query);
            break;

        case "accountno":
            Optional<Users> userByAccount = userRepository.findByAccountNo(query);
            userByAccount.ifPresent(users::add);
            break;

        case "closed":
            users = userRepository.findByStatus("INACTIVE");
            break;
        }

        model.addAttribute("users", users);
        return "admin/adminDashboard";
    }

    @GetMapping("/user/deposit")
    public String showDepositPage(Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));
        return "user/deposit"; 
    }

    @PostMapping("/user/deposit")
    public String handleDeposit(@RequestParam double amount, Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userAccountService.deposit(crn, amount);
            model.addAttribute("successMessage", "₹" + amount + " deposited successfully.");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));
        return "user/deposit";
    }

    @GetMapping("/user/withdraw")
    public String showWithdrawPage(Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));
        return "user/withdraw"; 
    }

    @PostMapping("/user/withdraw")
    public String handleWithdraw(@RequestParam double amount, Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userAccountService.withdraw(crn, amount);
            model.addAttribute("successMessage", "₹" + amount + " withdrawn successfully.");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));
        return "user/withdraw";
    }


    @GetMapping("/user/transfer")
    public String showTransferPage(Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));
        return "user/transfer"; 
    }

    @PostMapping("/user/transfer")
    public String handleTransfer(
            @RequestParam String toCrn,
            @RequestParam double amount,
            Model model) {

        String fromCrn = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userAccountService.transfer(fromCrn, toCrn, amount);
            model.addAttribute("successMessage", "₹" + amount + " transferred successfully to CRN: " + toCrn);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("userName", userAccountServiceImpl.getUserName(fromCrn));
        return "user/transfer";
    }

    @GetMapping("/user/getUserNameByCrn")
    @ResponseBody
    public String getUserNameByCrn(@RequestParam String crn) {
        return userAccountServiceImpl.getUserName(crn);
    }

    @GetMapping("/user/statement")
    public String viewStatement(Model model) {
        String crn = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserStatement> statements = userStatementRepository.findByUser_CrnOrderByDateTimeDesc(crn);

        model.addAttribute("statements", statements);
        model.addAttribute("userName", userAccountServiceImpl.getUserName(crn));

        return "user/statement";
    }

    @GetMapping("/user/change-pin")
    public String showChangePinForm(Model model, Principal principal) {
        Users user = userRepository.findByCrn(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("userName", user.getName());
        return "user/change-pin"; 
    }

    @PostMapping("/user/change-pin")
    public String changePin(@RequestParam("oldPin") String oldPin,
                            @RequestParam("newPin") String newPin,
                            @RequestParam("confirmPin") String confirmPin,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        Optional<Users> optionalUser = userRepository.findByCrn(principal.getName());

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (!passwordEncoder.matches(oldPin, user.getPin())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Old PIN is incorrect.");
                return "redirect:/user/change-pin";
            }

            if (!newPin.equals(confirmPin)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New PIN and Confirm PIN do not match.");
                return "redirect:/user/change-pin";
            }

            user.setPin(passwordEncoder.encode(newPin));
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "PIN changed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }

        return "redirect:/user/change-pin";
    }







}
