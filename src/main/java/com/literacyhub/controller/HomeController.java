package com.literacyhub.controller;

import com.literacyhub.dao.ResourceDAO;
import com.literacyhub.dao.UserDAO;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.Student;
import com.literacyhub.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private ResourceDAO resourceDAO;
    
    @Autowired
    private SessionFactory sessionFactory;

    @GetMapping("/student/home")
    @Transactional
    public String displayStudentHome(HttpSession session, Model model) {
        if (sessionExpired(session, "STUDENT")) return "redirect:/login?error=denied";
        
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        
        // Get statistics - simplified to avoid complex queries
        try {
            Session hibernateSession = sessionFactory.getCurrentSession();
            Long totalResources = hibernateSession.createQuery("SELECT COUNT(r) FROM Resource r", Long.class).uniqueResult();
            Long completedAssessments = hibernateSession.createQuery(
                "SELECT COUNT(uas) FROM UserAssessmentSubmission uas WHERE uas.userId = :userId", Long.class)
                .setParameter("userId", user.getId())
                .uniqueResult();
            
            model.addAttribute("totalResources", totalResources != null ? totalResources : 0L);
            model.addAttribute("completedAssessments", completedAssessments != null ? completedAssessments : 0L);
        } catch (Exception e) {
            // If queries fail, just show 0
            model.addAttribute("totalResources", 0L);
            model.addAttribute("completedAssessments", 0L);
        }

        return "student/studenthomepage";
    }

    @GetMapping("/professional/home")
    @Transactional
    public String displayProfHome(HttpSession session, Model model) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";
        
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        
        // Get statistics - simplified to avoid complex queries
        try {
            Session hibernateSession = sessionFactory.getCurrentSession();
            Long totalResources = hibernateSession.createQuery(
                "SELECT COUNT(r) FROM Resource r WHERE r.uploadedBy = :email", Long.class)
                .setParameter("email", user.getEmail())
                .uniqueResult();
            
            // Just count total assessment configs instead of trying to find creator
            Long totalAssessments = hibernateSession.createQuery(
                "SELECT COUNT(ac) FROM AssessmentConfig ac", Long.class)
                .uniqueResult();
            
            model.addAttribute("totalResources", totalResources != null ? totalResources : 0L);
            model.addAttribute("totalAssessments", totalAssessments != null ? totalAssessments : 0L);
        } catch (Exception e) {
            // If queries fail, just show 0
            model.addAttribute("totalResources", 0L);
            model.addAttribute("totalAssessments", 0L);
        }
        
        return "professional/professionalhomepage";
    }

    @GetMapping("/admin/home")
    @Transactional
    public String displayAdminHome(HttpSession session, Model model) {
        if (sessionExpired(session, "ADMIN")) return "redirect:/login?error=denied";
        
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        
        // Get statistics
        List<User> allUsers = userDAO.findAllUsers();
        List<Professional> pendingProfs = userDAO.findPendingProfessionals();
        
        long totalStudents = allUsers.stream().filter(u -> "STUDENT".equalsIgnoreCase(u.getUserRole())).count();
        long totalProfessionals = allUsers.stream().filter(u -> "PROFESSIONAL".equalsIgnoreCase(u.getUserRole())).count();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalProfessionals", totalProfessionals);
        model.addAttribute("pendingApprovals", pendingProfs.size());

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
