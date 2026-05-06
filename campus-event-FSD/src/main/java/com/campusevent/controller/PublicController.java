package com.campusevent.controller;

import com.campusevent.model.Event;
import com.campusevent.model.Registration;
import com.campusevent.model.User;
import com.campusevent.repository.EventRepository;
import com.campusevent.repository.RegistrationRepository;
import com.campusevent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PublicController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Highlight upcoming events (limit to 3)
        List<Event> upcomingEvents = eventRepository.findAll().stream()
                .filter(e -> e.getEventDate().isAfter(LocalDateTime.now()))
                .sorted((e1, e2) -> e1.getEventDate().compareTo(e2.getEventDate()))
                .limit(3)
                .toList();
        model.addAttribute("events", upcomingEvents);
        return "home";
    }

    @GetMapping("/events")
    public String listAllEvents(
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
        
        return "events";
    }

    @GetMapping("/event/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "event-details";
        }
        return "redirect:/events?error=NotFound";
    }

    @PostMapping("/register-event")
    public String registerForEvent(@RequestParam Long eventId, 
                                   Authentication authentication, 
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }

        Event event = eventOpt.get();
        if (event.getRegisteredCount() >= event.getCapacity()) {
            redirectAttributes.addFlashAttribute("error", "Event is already full.");
            return "redirect:/event/" + eventId;
        }

        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        // Check if already registered
        boolean alreadyRegistered = registrationRepository.findAll().stream()
                .anyMatch(r -> r.getUser().getId().equals(user.getId()) && r.getEvent().getId().equals(eventId));
        
        if (alreadyRegistered) {
            redirectAttributes.addFlashAttribute("error", "You are already registered for this event.");
            return "redirect:/event/" + eventId;
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registrationRepository.save(registration);

        // Redirect to payment flow
        return "redirect:/payment/checkout/" + registration.getId();
    }

    @GetMapping("/my-events")
    public String myEvents(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        List<Registration> registrations = registrationRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .toList();
        model.addAttribute("registrations", registrations);
        return "my-events";
    }
}
