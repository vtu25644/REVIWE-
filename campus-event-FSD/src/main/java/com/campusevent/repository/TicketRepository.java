package com.campusevent.repository;

import com.campusevent.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByRegistrationId(Long registrationId);
    Optional<Ticket> findByTicketId(String ticketId);
}
