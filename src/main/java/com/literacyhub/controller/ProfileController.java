package com.literacyhub.controller;

import com.literacyhub.entity.Professional;
import com.literacyhub.entity.Student;
import com.literacyhub.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        System.out.println("[DEBUG] Entered showProfile");

        Object obj = session.getAttribute("user");
        System.out.println("[DEBUG] User from session: " + obj);

        if (obj == null) {
            System.out.println("[DEBUG] No user in session, redirecting to login");
            return "redirect:/login";
        }

        if (!(obj instanceof User)) {
            System.out.println("[DEBUG] Object in session is not User! Type: " + obj.getClass());
            return "redirect:/login";
        }

        User user = (User) obj;
        System.out.println("[DEBUG] Logged-in user: " + user.getEmail() + ", role: " + user.getUserRole());

        // Flags for Thymeleaf
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getUserRole());
        boolean isStudent = "STUDENT".equalsIgnoreCase(user.getUserRole());
        boolean isProfessional = "PROFESSIONAL".equalsIgnoreCase(user.getUserRole());

        System.out.println("[DEBUG] Flags - isAdmin: " + isAdmin + ", isStudent: " + isStudent + ", isProfessional: " + isProfessional);

        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProfessional", isProfessional);

        // Add specialized objects for Thymeleaf (null-safe)
        if (isStudent) {
            if (user instanceof Student) {
                System.out.println("[DEBUG] Casting user to Student");
                model.addAttribute("student", (Student) user);
            } else {
                System.out.println("[DEBUG] Warning: userRole=STUDENT but not a Student instance!");
            }
        } else if (isProfessional) {
            if (user instanceof Professional) {
                System.out.println("[DEBUG] Casting user to Professional");
                model.addAttribute("professional", (Professional) user);
            } else {
                System.out.println("[DEBUG] Warning: userRole=PROFESSIONAL but not a Professional instance!");
            }
        }

        System.out.println("[DEBUG] Returning profile view");
        return "profile";
    }
}
