package com.campusevent.controller;

import com.campusevent.model.Ticket;
import com.campusevent.repository.TicketRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping("/view/{id}")
    public String viewTicket(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        
        if (ticketOpt.isEmpty()) {
            return "redirect:/my-events?error=TicketNotFound";
        }

        Ticket ticket = ticketOpt.get();
        
        if (!ticket.getRegistration().getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/my-events?error=Unauthorized";
        }

        model.addAttribute("ticket", ticket);
        return "ticket";
    }

    @GetMapping("/download/{id}")
    public void downloadTicket(@PathVariable Long id, HttpServletResponse response, Authentication authentication) throws IOException {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        
        if (ticketOpt.isEmpty() || !ticketOpt.get().getRegistration().getUser().getUsername().equals(authentication.getName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Ticket ticket = ticketOpt.get();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ticket_" + ticket.getTicketId() + ".pdf";
        response.setHeader(headerKey, headerValue);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD);
            Font infoFont = new Font(Font.HELVETICA, 14, Font.NORMAL);

            Paragraph title = new Paragraph("Event Ticket\n\n", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Ticket ID: " + ticket.getTicketId(), infoFont));
            document.add(new Paragraph("Event: " + ticket.getRegistration().getEvent().getTitle(), infoFont));
            document.add(new Paragraph("Date: " + ticket.getRegistration().getEvent().getEventDate(), infoFont));
            document.add(new Paragraph("Attendee: " + ticket.getRegistration().getUser().getName(), infoFont));
            document.add(new Paragraph("\nGenerated on: " + ticket.getGeneratedDate(), infoFont));

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        }
    }
}
