package com.campusevent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_fk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id_fk", nullable = false)
    private Event event;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    // Payment relationship
    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private Payment payment;

    // Ticket relationship
    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private Ticket ticket;

    public Registration() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
}
