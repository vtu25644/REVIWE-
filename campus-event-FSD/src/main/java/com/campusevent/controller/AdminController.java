package com.campusevent.controller;

import com.campusevent.model.Event;
import com.campusevent.repository.EventRepository;
import com.campusevent.repository.RegistrationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type,
            Model model) {
        
        List<Event> events;
        if ((search != null && !search.isBlank()) || (department != null && !department.isBlank()) || (type != null && !type.isBlank())) {
            events = eventRepository.searchEvents(
                    search != null && !search.isBlank() ? search : null,
                    department != null && !department.isBlank() ? department : null,
                    type != null && !type.isBlank() ? type : null
            );
        } else {
            events = eventRepository.findAll();
        }
        
        model.addAttribute("events", events);
        model.addAttribute("search", search);
        model.addAttribute("department", department);
        model.addAttribute("type", type);
        
        // Calculate total registrations across all events
        long totalRegistrations = registrationRepository.count();
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("totalEvents", eventRepository.count());

        return "admin/dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events/new")
    public String createEvent(@Valid @ModelAttribute("event") Event event, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/event-form";
        }
        eventRepository.save(event);
        redirectAttributes.addFlashAttribute("successMessage", "Event created successfully");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        return "admin/event-form";
    }

    @PostMapping("/events/update/{id}")
    public String updateEvent(@PathVariable("id") long id, 
                              @Valid @ModelAttribute("event") Event event, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            event.setId(id);
            return "admin/event-form";
        }
        eventRepository.save(event);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        eventRepository.delete(event);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
        return "redirect:/admin/dashboard";
    }
}
