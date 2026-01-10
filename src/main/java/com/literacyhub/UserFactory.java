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
            Student student = new Student();
            student.setUserRole("STUDENT");
            student.setStatus("ACTIVE");

            // Mapping Student Specific Fields
            student.setStudentID(dto.getStudentId());
            student.setMajor(dto.getMajor());
            student.setAcademicYear(dto.getAcademicYear());

            user = student;
        } else if ("PROFESSIONAL".equalsIgnoreCase(role)) {
            Professional prof = new Professional();
            prof.setUserRole("PROFESSIONAL");
            prof.setStatus("PENDING");

            // Mapping Professional Specific Fields
            prof.setProfessionalId(dto.getProfessionalId());
            prof.setDepartment(dto.getDepartment());
            prof.setQualification(dto.getQualification());
            prof.setSpecialization(dto.getSpecialization());

            user = prof;
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