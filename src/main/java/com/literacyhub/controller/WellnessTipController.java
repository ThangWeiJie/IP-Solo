package com.literacyhub.controller;

import com.literacyhub.dao.WellnessTipDAO;
import com.literacyhub.entity.User;
import com.literacyhub.entity.WellnessTip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/{role}/wellness")
public class WellnessTipController {

    @Autowired
    private WellnessTipDAO wellnessTipDAO;

    /**
     * Shared role setup & validation
     */
    @ModelAttribute
    public void setupRole(
            @PathVariable String role,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new RuntimeException("Session expired");
        }

        String actualRole = user.getUserRole().toLowerCase();

        if (!actualRole.equals(role)) {
            throw new RuntimeException("Unauthorized role access");
        }

        model.addAttribute("user", user);
        model.addAttribute("userRole", actualRole);
    }

    /* -------------------- LIST -------------------- */

    @GetMapping("/list")
    public String listTips(Model model) {
        List<WellnessTip> tips = wellnessTipDAO.findAllTips();
        model.addAttribute("tips", tips);
        model.addAttribute("title", "Manage Wellness Tips");
        return "wellness-list"; // shared view
    }

    /* -------------------- ADD -------------------- */

    @GetMapping("/add")
    public String addTipForm(Model model) {
        model.addAttribute("tip", new WellnessTip());
        model.addAttribute("title", "Add Wellness Tip");
        return "wellness-form";
    }

    /* -------------------- SAVE -------------------- */

    @PostMapping("/save")
    public String saveTip(
            @PathVariable String role,
            @ModelAttribute WellnessTip tip
    ) {
        wellnessTipDAO.save(tip);
        return "redirect:/" + role + "/wellness/list";
    }

    /* -------------------- EDIT -------------------- */

    @GetMapping("/edit/{id}")
    public String editTipForm(
            @PathVariable String role,
            @PathVariable Long id,
            Model model
    ) {
        Optional<WellnessTip> optionalTip = wellnessTipDAO.findById(id);
        if (optionalTip.isEmpty()) {
            return "redirect:/" + role + "/wellness/list";
        }

        model.addAttribute("tip", optionalTip.get());
        model.addAttribute("title", "Edit Wellness Tip");
        return "wellness-form";
    }

    /* -------------------- UPDATE -------------------- */

    @PostMapping("/update")
    public String updateTip(
            @PathVariable String role,
            @ModelAttribute WellnessTip tip
    ) {
        Optional<WellnessTip> optionalTip = wellnessTipDAO.findById(tip.getId());
        if (optionalTip.isEmpty()) {
            return "redirect:/" + role + "/wellness/list";
        }

        WellnessTip existingTip = optionalTip.get();
        existingTip.setTip(tip.getTip());
        wellnessTipDAO.update(existingTip);

        return "redirect:/" + role + "/wellness/list";
    }

    /* -------------------- DELETE -------------------- */

    @GetMapping("/delete/{id}")
    public String deleteTip(
            @PathVariable String role,
            @PathVariable Long id
    ) {
        wellnessTipDAO.delete(id);
        return "redirect:/" + role + "/wellness/list";
    }
}
