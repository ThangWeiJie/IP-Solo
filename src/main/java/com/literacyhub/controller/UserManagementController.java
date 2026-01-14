package com.literacyhub.controller;

import com.literacyhub.UserFactory;
import com.literacyhub.dao.UserDAO;
import com.literacyhub.dto.RegistrationDTO;
import com.literacyhub.entity.Admin;
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

import javax.servlet.ServletContext;
import java.io.File;
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
    
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private UserFactory userFactory;

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

    @PostMapping("/add")
    public String addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("userRole") String userRole,
            @RequestParam(value = "staffId", required = false) String staffId
    ) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPassword("defaultpassword123");

        User newUser = userFactory.createNewUser(userRole, dto);

        if ("ADMIN".equalsIgnoreCase(userRole)) {
            ((Admin)newUser).setStaffId(staffId);
            ((Admin)newUser).setDepartment("General");
            ((Admin)newUser).setPermissions("All");
        }

        userDAO.save(newUser);

        return "redirect:/admin/users?msg=added";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userDAO.processApproval(userId, "REJECT");
        return "redirect:/admin/users?msg=deleted";
    }

    @GetMapping("/doc/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveDocument(@PathVariable String filename) throws IOException {
        // Get the real path to the uploads directory in the web application
        String realPath = servletContext.getRealPath("/uploads/verifications");
        
        if (realPath == null) {
            // Fallback to a relative path from the working directory
            realPath = "uploads/verifications";
        }
        
        Path filePath = Paths.get(realPath).resolve(filename).normalize();
        File file = filePath.toFile();
        
        // Security check: ensure the file is within the verifications directory
        String canonicalPath = file.getCanonicalPath();
        String uploadsDirCanonical = new File(realPath).getCanonicalPath();
        
        if (!canonicalPath.startsWith(uploadsDirCanonical)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (!file.exists() || !file.canRead()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        
        // Determine content type based on file extension
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
