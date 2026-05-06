package com.campusevent.controller;

import com.campusevent.model.Event;
import com.campusevent.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type) {
        
        List<Event> events;
        if (department != null && type != null) {
            events = eventRepository.searchEvents(null, department, type);
        } else if (department != null) {
            events = eventRepository.findByDepartmentContainingIgnoreCase(department);
        } else if (type != null) {
            events = eventRepository.findByTypeContainingIgnoreCase(type);
        } else {
            events = eventRepository.findAll();
        }
        
        return ResponseEntity.ok(events);
    }
}
