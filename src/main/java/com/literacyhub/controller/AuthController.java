package com.literacyhub.controller;

import com.literacyhub.RedirectFactory;
import com.literacyhub.UserFactory;
import com.literacyhub.dao.UserDAO;
import com.literacyhub.dto.LoginDTO;
import com.literacyhub.dto.RegistrationDTO;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

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
    public String processRegister(@ModelAttribute("regData") RegistrationDTO registrationDTO, Model model) {
        try {
            User newUser = userFactory.createNewUser(registrationDTO.getUserRole(), registrationDTO);

            userDAO.save(newUser);

            if("PROFESSIONAL".equalsIgnoreCase(registrationDTO.getUserRole())) {
                return "redirect:/pending";
            }

            return "redirect:/login?success=registered";
        }
        catch(Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Registration failed. Email might already be in use.");
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
}
