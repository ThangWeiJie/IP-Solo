package com.literacyhub.controller;

import com.literacyhub.RedirectFactory;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    @Autowired
    private RedirectFactory redirectFactory;

    @GetMapping("/")
    public String index(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return redirectFactory.getDashboardRedirect(user);
        }

        return "redirect:/login";
    }
}
