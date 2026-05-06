package com.campusevent.controller;

import com.campusevent.dto.UserRegistrationDto;
import com.campusevent.model.User;
import com.campusevent.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            result.rejectValue("username", null, "Username is already taken");
            return "register";
        }

        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            result.rejectValue("email", null, "Email is already registered");
            return "register";
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setRole("ROLE_STUDENT"); // Default role
        user.setPoints(0);

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
