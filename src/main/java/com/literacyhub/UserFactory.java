package com.literacyhub;

import com.literacyhub.dto.RegistrationDTO;
import com.literacyhub.entity.Professional;
import com.literacyhub.entity.Student;
import com.literacyhub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User createNewUser(String role, RegistrationDTO dto) {
        User user;

        if ("STUDENT".equalsIgnoreCase(role)) {
            user = new Student();
            user.setUserRole("STUDENT");
            user.setStatus("ACTIVE");
        } else if ("PROFESSIONAL".equalsIgnoreCase(role)) {
            user = new Professional();
            user.setUserRole("PROFESSIONAL");
            ((Professional) user).setLicenseId(dto.getLicenseId());
            user.setStatus("PENDING");
        } else {
            throw new IllegalArgumentException("Unknown Role");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        return user;
    }
}