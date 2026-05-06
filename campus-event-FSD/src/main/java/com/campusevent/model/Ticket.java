package com.campusevent.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketId;

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    private LocalDateTime generatedDate;

    public Ticket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) { this.registration = registration; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }

    @PrePersist
    protected void onCreate() {
        this.generatedDate = LocalDateTime.now();
        if (this.ticketId == null) {
            this.ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
