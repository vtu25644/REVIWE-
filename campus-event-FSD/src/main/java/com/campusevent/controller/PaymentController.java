package com.campusevent.controller;

import com.campusevent.model.Payment;
import com.campusevent.model.Registration;
import com.campusevent.model.Ticket;
import com.campusevent.model.User;
import com.campusevent.repository.PaymentRepository;
import com.campusevent.repository.RegistrationRepository;
import com.campusevent.repository.TicketRepository;
import com.campusevent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.campusevent.config.CustomUserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/checkout/{registrationId}")
    public String showPaymentPage(@PathVariable Long registrationId, Model model, Authentication authentication) {
        Optional<Registration> registrationOpt = registrationRepository.findById(registrationId);
        
        if (registrationOpt.isEmpty()) {
            return "redirect:/my-events?error=RegistrationNotFound";
        }

        Registration registration = registrationOpt.get();
        
        // Security check
        if (!registration.getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/my-events?error=Unauthorized";
        }

        model.addAttribute("registration", registration);
        return "payment";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam Long registrationId, 
                                 @RequestParam String cardNumber,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {
        
        Optional<Registration> registrationOpt = registrationRepository.findById(registrationId);
        if (registrationOpt.isEmpty()) {
            return "redirect:/my-events?error=RegistrationNotFound";
        }

        Registration registration = registrationOpt.get();

        // Simulate payment processing
        boolean success = cardNumber != null && cardNumber.length() >= 16; // Dummy validation
        
        Payment payment = new Payment();
        payment.setRegistration(registration);
        payment.setAmount(50.0); // Dummy amount for all events
        payment.setStatus(success ? "SUCCESS" : "FAILED");
        paymentRepository.save(payment);

        if (success) {
            // Generate ticket
            Ticket ticket = new Ticket();
            ticket.setRegistration(registration);
            ticketRepository.save(ticket);

            // Add 10 points to user
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + 10);
                userRepository.save(user);

                // Update session points immediately so the UI reflects it
                if (authentication.getPrincipal() instanceof CustomUserDetails) {
                    ((CustomUserDetails) authentication.getPrincipal()).setPoints(user.getPoints());
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Payment successful! 10 points awarded.");
            return "redirect:/ticket/view/" + ticket.getId();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment failed. Please try again.");
            return "redirect:/payment/checkout/" + registrationId;
        }
    }
}
