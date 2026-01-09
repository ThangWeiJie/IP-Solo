package com.literacyhub.controller;

import com.literacyhub.dao.AssessmentDAO;
import com.literacyhub.entity.AssessmentConfig;
import com.literacyhub.entity.AssessmentOption;
import com.literacyhub.entity.AssessmentQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RequestMapping("/professional/assessments")
public class ProfAssessmentController {
    @Autowired
    private AssessmentDAO assessmentDAO;

    /** ---------------------- ASSESSMENT CRUD ---------------------- **/

    @GetMapping("/list")
    public String listAllAssessments(Model model) {
        List<AssessmentConfig> configs = assessmentDAO.findAllConfigs();
        model.addAttribute("assessments", configs);
        model.addAttribute("title", "Manage Assessments");

        return "professional/assessment-list";
    }

    @GetMapping("/new")
    public String newAssessmentForm(Model model) {
        model.addAttribute("assessment", new AssessmentConfig());
        model.addAttribute("formTitle", "Add New Assessment");
        return "professional/assessment-form";
    }

    // Show form for editing an existing assessment
    @GetMapping("/edit/{id}")
    public String editAssessmentForm(@PathVariable Long id, Model model) {
        AssessmentConfig assessment = assessmentDAO.findConfigById(id);
        if (assessment == null) {
            return "redirect:/professional/assessments";
        }
        model.addAttribute("assessment", assessment);
        model.addAttribute("formTitle", "Edit Assessment");
        return "professional/assessment-form";
    }

    @PostMapping("/save")
    public String saveAssessment(@ModelAttribute AssessmentConfig assessment) {
        assessmentDAO.saveConfig(assessment);
        return "redirect:/professional/assessments/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteAssessment(@PathVariable Long id) {
        assessmentDAO.deleteAssessment(id); // make sure you add this DAO method
        return "redirect:/professional/assessments/list";
    }

    /** ---------------------- QUESTION CRUD ---------------------- **/

    @GetMapping("/{testType}")
    public String manageAssessment(@PathVariable String testType, Model model) {
        List<AssessmentQuestion> questions = assessmentDAO.findByType(testType);
        List<AssessmentOption> options = assessmentDAO.findOptionsByType(testType);

        model.addAttribute("questions", questions);
        model.addAttribute("options", options);
        model.addAttribute("testType", testType);

        model.addAttribute("newQuestion", new AssessmentQuestion());
        model.addAttribute("newOption", new AssessmentOption());

        model.addAttribute("title", "Manage Assessment: " + testType);

        return "professional/manage-assessment";
    }

    @PostMapping("/{testType}/questions/save")
    public String saveQuestion(
            @ModelAttribute AssessmentQuestion newQuestion,
            @PathVariable String testType
    ) {
        newQuestion.setType(testType);

        if (newQuestion.getCategory() == null || newQuestion.getCategory().isEmpty()) {
            newQuestion.setCategory("General"); // or any default
        }

        assessmentDAO.saveQuestion(newQuestion);

        return "redirect:/professional/assessments/" + testType;
    }

    @PostMapping("/question/delete/{id}")
    public String deleteQuestion(
            @PathVariable Long id,
            @RequestParam String testType
    ) {
        assessmentDAO.deleteQuestion(id);
        return "redirect:/professional/assessments/" + testType;
    }

    @GetMapping("/question/edit/{id}")
    public String editQuestion(@PathVariable Long id, Model model) {
        AssessmentQuestion question = assessmentDAO.findById(id);

        if (question == null) {
            return "redirect:/professional/assessments/list";
        }

        model.addAttribute("newQuestion", question);
        model.addAttribute("testType", question.getType());

        model.addAttribute("questions", assessmentDAO.findByType(question.getType()));
        model.addAttribute("options", assessmentDAO.findOptionsByType(question.getType()));
        return "professional/manage-assessment";
    }

    /** ---------------------- OPTIONS CRUD ---------------------- **/
    @PostMapping("/{testType}/options/save")
    public String saveOption(@ModelAttribute AssessmentOption newOption,
                             @PathVariable String testType) {
        newOption.setTestType(testType);
        assessmentDAO.saveOption(newOption);
        return "redirect:/professional/assessments/" + testType;
    }

    @PostMapping("/option/delete/{id}")
    public String deleteOption(@PathVariable Long id,
                               @RequestParam String testType) {
        assessmentDAO.deleteOption(id);
        return "redirect:/professional/assessments/" + testType;
    }
}
