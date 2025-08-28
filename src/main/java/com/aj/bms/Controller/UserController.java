package com.aj.bms.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.aj.bms.Entity.*;
import com.aj.bms.Services.*;
import com.aj.bms.Services.impl.*;
import com.aj.bms.Dao.*;

@Controller
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // private AccountManagementService accountManagementService;

    @Autowired
    private AccountManagementServiceImpl accountManagementServiceImpl;

    @Autowired
    private UserAccountServiceImpl userAccountServiceImpl;

    @Autowired
    private AdminSearchService adminSearchService;

    @Autowired
    private UserRepository userRepository;

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


    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {
        String userName = userAccountServiceImpl.getUserName(principal.getName());
        model.addAttribute("userName", userName);
        return "user/userDashboard";
    }


    // Create User
    // @GetMapping("/admin/create-user")
    // public String showUserRegistrationForm(Model model) {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     String crn = auth.getName();
    //     Users admin = adminSearchService.findByCrn(crn);
    //     model.addAttribute("adminName", admin.getName());
    //     model.addAttribute("user", new Users());
    //     return "admin/create-user";
    // }

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
    public String changeAdminPin(@RequestParam String oldPin,
                                @RequestParam String newPin,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            userAccountService.updatePin(principal.getName(), oldPin, newPin);
            redirectAttributes.addFlashAttribute("success", "PIN changed successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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


    


}
