package com.literacyhub.controller;

import com.literacyhub.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @GetMapping("/student/home")
    public String displayStudentHome(HttpSession session, Model model) {
        if (sessionExpired(session, "STUDENT")) return "redirect:/login?error=denied";

        return "student/studenthomepage";
    }

    @GetMapping("/professional/home")
    public String displayProfHome(HttpSession session, Model model) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";
        return "professional/professionalhomepage";
    }

    @GetMapping("/admin/home")
    public String displayAdminHome(HttpSession session, Model model) {
        if (sessionExpired(session, "ADMIN")) return "redirect:/login?error=denied";

        return "admin/adminhomepage";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login?error=denied";

        String role = user.getUserRole();
        if ("STUDENT".equalsIgnoreCase(role)) return "redirect:/student/home";
        if ("PROFESSIONAL".equalsIgnoreCase(role)) return "redirect:/professional/home";
        if ("ADMIN".equalsIgnoreCase(role)) return "redirect:/admin/home";

        return "redirect:/login?error=denied";
    }

    private boolean sessionExpired(HttpSession session, String requiredRole) {
        User user = (User) session.getAttribute("user");
        return user == null || !requiredRole.equalsIgnoreCase(user.getUserRole());
    }
}
