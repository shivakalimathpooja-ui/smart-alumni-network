package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.EventDTO;
import com.example.Smartalumni.dto.EventRegistrationRequest;
import com.example.Smartalumni.entity.EventRegistration;
import com.example.Smartalumni.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EventController {

    private final EventService eventService;

    // ✅ CREATE EVENT
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO request) {
        EventDTO createdEvent = eventService.createEvent(request);
        return ResponseEntity.ok(createdEvent);
    }

    // ✅ GET ALL EVENTS
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.viewEvents());
    }

    // ✅ UPDATE EVENT
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEvent(@PathVariable Long id,
            @RequestBody EventDTO request) {
        return ResponseEntity.ok(eventService.editEvent(id, request));
    }

    // ✅ DELETE EVENT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.deleteEvent(id));
    }

    // ✅ REGISTER FOR EVENT
    @PostMapping("/register")
    public ResponseEntity<String> registerForEvent(@RequestBody EventRegistrationRequest request) {
        return ResponseEntity.ok(
                eventService.registerForEvent(request.getUserId(), request.getEventId()));
    }

    // ✅ VIEW PARTICIPANTS
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<EventRegistration>> getParticipants(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.trackParticipation(eventId));
    }
}
