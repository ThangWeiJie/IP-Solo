package com.literacyhub.controller;

import com.literacyhub.dao.UserDAO;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        model.addAttribute("pendingProfessionals", pendingProfs);

        return "admin/usermanagement";
    }

    @PostMapping("/approve")
    public String approveProfessional(@RequestParam("userId") Long userId) {
        userDAO.processApproval(userId, "APPROVE");
        return "redirect:/admin/users?msg=approved";
    }

    @PostMapping("/reject")
    public String rejectProfessional(@RequestParam("userId") Long userId) {
        userDAO.processApproval(userId, "REJECT");
        return "redirect:/admin/users?msg=rejected";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userDAO.processApproval(userId, "REJECT");
        return "redirect:/admin/users?msg=deleted";
    }

    @GetMapping("/doc/{filename.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveDocument(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads/verifications").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
