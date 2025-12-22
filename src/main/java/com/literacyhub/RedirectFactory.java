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
                return "studenthomepage";

            case "PROFESSIONAL":
                return "professionalhomepage";

            case "ADMIN":
                return "adminhomepage";

            default:
                return "redirect:/login?error=unauthorized_role";
        }
    }
}
