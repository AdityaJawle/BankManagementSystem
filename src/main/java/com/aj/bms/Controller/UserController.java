package com.aj.bms.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
// import java.util.Optional;
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
// import org.springframework.web.bind.annotation.PathVariable;
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


    @GetMapping("/user/userDashboard")
    public String userDashboard(Model model, Principal principal) {
        String userName = userAccountServiceImpl.getUserName(principal.getName());
        model.addAttribute("users", userName);
        return "user/userDashboard";
    }

    // @GetMapping("/user/userDashboard")
    // public String showUserDashboard(Model model, Principal principal) {
    //     Optional<Users> user = userRepository.findByCrn(principal.getName()); 
    //     model.addAttribute("user", user);
    //     return "user/userDashboard";
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
    public String changeAdminPin(@RequestParam String oldPin,@RequestParam String newPin,Principal principal,RedirectAttributes redirectAttributes) {
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

    @GetMapping("/admin/update-user/{crn}")
    public String showUpdateUserForm(@PathVariable String crn, Model model, Principal principal) {
        // Get the logged-in admin's name for the sidebar
        Users admin = adminSearchService.findByCrn(principal.getName());
        model.addAttribute("adminName", admin.getName());

        // Fetch the user to update
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
            RedirectAttributes redirectAttributes,
            Principal principal) {

        // Fetch user to be updated
        Optional<Users> optionalUser = userRepository.findByCrn(crn);

        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();

            // Prevent updates if user is inactive
            if ("INACTIVE".equals(existingUser.getStatus())) {
                redirectAttributes.addFlashAttribute("message", "Cannot update INACTIVE user.");
                return "redirect:/admin/update-user/" + crn;
            }

            // Update allowed fields only
            existingUser.setName(updatedUser.getName());
            existingUser.setBalance(updatedUser.getBalance());

            // Save the updated user
            userRepository.save(existingUser);

            // Log this action
            Users admin = adminSearchService.findByCrn(principal.getName());
            accountManagementServiceImpl.logAction("UPDATE_USER_DETAILS", admin, existingUser);

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

                // Log the reset action
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

                // Log the close action
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

        // Get admin name for the sidebar
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
        return "admin/adminDashboard"; // return the same dashboard view
    }




}
