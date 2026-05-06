package com.campusevent.controller;

import com.campusevent.model.HelpRequest;
import com.campusevent.model.User;
import com.campusevent.repository.HelpRequestRepository;
import com.campusevent.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/helpdesk")
public class HelpDeskController {

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showHelpDesk(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<HelpRequest> requests = helpRequestRepository.findAllByOrderByCreatedAtDesc();
            model.addAttribute("requests", requests);
            return "admin-helpdesk";
        } else {
            List<HelpRequest> myRequests = helpRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            model.addAttribute("requests", myRequests);
            model.addAttribute("newRequest", new HelpRequest());
            return "helpdesk";
        }
    }

    @PostMapping("/submit")
    public String submitRequest(@Valid @ModelAttribute("newRequest") HelpRequest request,
                                BindingResult result,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        if (result.hasErrors()) {
            List<HelpRequest> myRequests = helpRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            model.addAttribute("requests", myRequests);
            return "helpdesk";
        }

        request.setUser(user);
        request.setStatus("OPEN");
        helpRequestRepository.save(request);

        redirectAttributes.addFlashAttribute("successMessage", "Help request submitted successfully.");
        return "redirect:/helpdesk";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Optional<HelpRequest> reqOpt = helpRequestRepository.findById(id);
        if (reqOpt.isPresent()) {
            HelpRequest req = reqOpt.get();
            req.setStatus(status);
            helpRequestRepository.save(req);
            redirectAttributes.addFlashAttribute("successMessage", "Status updated successfully.");
        }
        return "redirect:/helpdesk";
    }
}
