package com.literacyhub.controller;

import com.literacyhub.RedirectFactory;
import com.literacyhub.UserFactory;
import com.literacyhub.dao.UserDAO;
import com.literacyhub.dto.LoginDTO;
import com.literacyhub.dto.RegistrationDTO;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
public class AuthController {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RedirectFactory redirectFactory;
    @Autowired
    private UserFactory userFactory;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return redirectFactory.getDashboardRedirect(user);
        }

        model.addAttribute("loginRequest", new LoginDTO());
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("regData", new RegistrationDTO());
        return "register";
    }

    @GetMapping("/pending")
    public String showPendingPage(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("firstName", name);
        return "pending";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginRequest") LoginDTO loginDTO, HttpSession httpSession, Model model) {
        User user = userDAO.login(loginDTO.getEmail(), loginDTO.getPassword());

        if (user != null) {

            if ("PENDING".equalsIgnoreCase(user.getStatus())) {
                return "redirect:/login?error=pending";
            }

            httpSession.setAttribute("user", user);
            return redirectFactory.getDashboardRedirect(user);
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("regData") RegistrationDTO registrationDTO,
            @RequestParam("verificationDocument") MultipartFile file,
            Model model) {
        try {
            User newUser = userFactory.createNewUser(registrationDTO.getUserRole(), registrationDTO);

            if (newUser instanceof Professional && file != null && !file.isEmpty()) {
                Professional professional = (Professional) newUser;

                String pathOnDisk = saveVerificationDocument(file, registrationDTO.getEmail());
                System.out.println("PATH GENERATED: " + pathOnDisk);
                professional.setVerification_document(pathOnDisk);
            }

            userDAO.save(newUser);

            if("PROFESSIONAL".equalsIgnoreCase(registrationDTO.getUserRole())) {
                return "redirect:/pending";
            }

            return "redirect:/login?success=registered";
        }
        catch(Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Registration failed. Email might already be in use.");
//            model.addAttribute("regData", registrationDTO);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }

    private String saveVerificationDocument(MultipartFile file, String email) throws IOException {
        String UPLOAD_DIR = "uploads/verifications/";

        java.io.File directory = new File(UPLOAD_DIR);

        if(!directory.exists()) {
            directory.mkdirs();
        }

        String filename = email.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".pdf";
        java.nio.file.Path path = java.nio.file.Paths.get(UPLOAD_DIR + filename);

        java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return UPLOAD_DIR + filename;
    }
}
