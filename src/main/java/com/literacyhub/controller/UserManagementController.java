package com.literacyhub.controller;

import com.literacyhub.dao.UserDAO;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    @Autowired
    private UserDAO userDAO;

    @GetMapping
    public String showUserHub(Model model) {
        List<User> allUsers = userDAO.findAllUsers();
        List<Professional> pendingProfs = userDAO.findPendingProfessionals();

        model.addAttribute("users", allUsers);
        model.addAttribute("pendingProfessional", pendingProfs);

        return "admin/usermanagement";
    }

    @PostMapping("/approve")
    public String approveProfessional(@RequestParam("userId") int userId) {
        userDAO.processApproval(userId, "APPROVE");
        return "redirect:/admin/users?msg=approved";
    }

    @PostMapping("/reject")
    public String rejectProfessional(@RequestParam("userId") int userId) {
        userDAO.processApproval(userId, "REJECT");
        return "redirect:/admin/users?msg=rejected";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") int userId) {
        userDAO.processApproval(userId, "REJECT");
        return "redirect:/admin/users?msg=deleted";
    }
}
