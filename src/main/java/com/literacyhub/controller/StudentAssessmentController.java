package com.literacyhub.controller;

import com.literacyhub.AssessmentService;
import com.literacyhub.dao.AssessmentDAO;
import com.literacyhub.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/student/assessment")
public class StudentAssessmentController {
    @Autowired
    private AssessmentDAO assessmentDAO;
    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/list")
    public String listAssessments(Model model) {
        List<AssessmentConfig> configs = assessmentDAO.findAllConfigs();

        System.out.println(configs.size());

        model.addAttribute("assessment", configs);
        model.addAttribute("title", "Select Assessment");

        return "student/self-assessment-list";
    }

    @GetMapping("/take/{type}")
    public String takeAssessment(@PathVariable String type, Model model) {
        AssessmentConfig config = assessmentDAO.findConfigByType(type);

        if (config == null) {
            return "redirect:/student/self-assessment-list?error=notfound";
        }

        List<AssessmentQuestion> questions = assessmentDAO.findByType(type);
        List<AssessmentOption> options = assessmentDAO.findOptionsByType(type);

        model.addAttribute("config", config);
        model.addAttribute("questions", questions);
        model.addAttribute("options", options);
        model.addAttribute("assessmentName", config.getTestName());
        model.addAttribute("instructions", config.getInstructions());
        model.addAttribute("testType", type);
        model.addAttribute("title", config.getTestName());

        return "student/self-assessment-form";
    }

    @PostMapping("/submit")
    public String submitAssessment(@RequestParam Map<String, String> allParams, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?error=session_expired";
        }

        String testType = allParams.get("testType");
        if (testType == null) {
            return "redirect:/student/self-assessment-list?error=missing";
        }

        AssessmentConfig config = assessmentDAO.findConfigByType(testType);
        if (config == null) {
            return "redirect:/student/self-assessment-list?error=notfound";
        }

        int rawScore = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("q_"))
                .mapToInt(entry -> {
                    try {
                        return Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum();

        Map<String, Object> result = assessmentService.calculateResult(rawScore, config);

        UserAssessmentSubmission submission = new UserAssessmentSubmission();
        submission.setUserId(user.getId());
        submission.setTestType(testType);
        submission.setTotalScore((Integer) result.get("score"));
        assessmentDAO.saveSubmission(submission);

        model.addAttribute("config", config);
        model.addAttribute("result", result);
        model.addAttribute("rawScore", rawScore);
        model.addAttribute("title", config.getTestName() + " Result");

        return "student/self-assessment-result";
    }
}
