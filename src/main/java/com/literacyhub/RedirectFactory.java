package com.literacyhub;

import com.literacyhub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RedirectFactory {
    public String getDashboardRedirect(User user) {
        if (user == null || user.getUserRole() == null) {
            return "redirect:/login";
        }

        switch (user.getUserRole().toUpperCase()) {
            case "STUDENT":
                return "redirect:/student/home";

            case "PROFESSIONAL":
                return "redirect:/professional/home";

            case "ADMIN":
                return "redirect:/admin/home";

            default:
                return "redirect:/login?error=unauthorized_role";
        }
    }
}
