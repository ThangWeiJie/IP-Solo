package com.literacyhub.controller;

import com.literacyhub.dao.UserDAO;
import com.literacyhub.dto.ProfileUpdateDTO;
import com.literacyhub.entity.Admin;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.Student;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    UserDAO userDAO;

    @GetMapping("/profile")
    public String showProfile(
            @RequestParam(value = "edit", required = false) Boolean edit,
            HttpSession session,
            Model model) {
        Object obj = session.getAttribute("user");

        if (obj == null) {
            return "redirect:/login";
        }

        if (!(obj instanceof User)) {
            return "redirect:/login";
        }

        User user = (User) obj;

        // Flags for Thymeleaf
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getUserRole());
        boolean isStudent = "STUDENT".equalsIgnoreCase(user.getUserRole());
        boolean isProfessional = "PROFESSIONAL".equalsIgnoreCase(user.getUserRole());
        model.addAttribute("editMode", Boolean.TRUE.equals(edit));


        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProfessional", isProfessional);

        // Add specialized objects for Thymeleaf (null-safe)
        if (isStudent && user instanceof Student) {
            model.addAttribute("student", (Student) user);
        }

        if (isProfessional && user instanceof Professional) {
            model.addAttribute("professional", (Professional) user);
        }

        if (isAdmin && user instanceof Admin) {
            model.addAttribute("admin", (Admin) user);
        }

        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        // Student-specific
        if (isStudent && user instanceof Student student) {
            dto.setMajor(student.getMajor());
            dto.setAcademicYear(student.getAcademicYear());
        }

        // Professional-specific
        if (isProfessional && user instanceof Professional prof) {
            dto.setDepartment(prof.getDepartment());
            dto.setSpecialization(prof.getSpecialization());
            dto.setQualification(prof.getQualification());
        }

        model.addAttribute("profile", dto);

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            HttpSession session,
            @ModelAttribute("profile") ProfileUpdateDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        User user = (User) session.getAttribute("user");
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        // Student-specific updates
        if ("STUDENT".equalsIgnoreCase(user.getUserRole()) && user instanceof Student student) {
            student.setMajor(dto.getMajor());
            student.setAcademicYear(dto.getAcademicYear());
        }

        // Professional-specific updates
        if ("PROFESSIONAL".equalsIgnoreCase(user.getUserRole()) && user instanceof Professional prof) {
            prof.setDepartment(dto.getDepartment());
            prof.setSpecialization(dto.getSpecialization());
            prof.setQualification(dto.getQualification());
        }

        userDAO.update(user);

        session.setAttribute("user", user);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            HttpSession session,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        // Validate current password
        if (!user.getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/profile";
        }

        // Validate new password confirmation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/profile";
        }

        // Validate password length
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long");
            return "redirect:/profile";
        }

        // Update password
        user.setPassword(newPassword);
        userDAO.update(user);
        session.setAttribute("user", user);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        return "redirect:/profile";
    }
}
