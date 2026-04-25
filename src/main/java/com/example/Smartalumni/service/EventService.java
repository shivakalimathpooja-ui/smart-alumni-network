package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.EventDTO;
import com.example.Smartalumni.entity.Event;
import com.example.Smartalumni.entity.EventRegistration;
import com.example.Smartalumni.mapper.EventMapper;
import com.example.Smartalumni.repository.EventRegistrationRepository;
import com.example.Smartalumni.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    // ✅ CREATE EVENT
   public EventDTO createEvent(EventDTO dto) {

        // Use mapper (clean and consistent)
        Event event = EventMapper.toEntity(dto);

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toDTO(savedEvent);
    }

    // ✅ VIEW EVENTS (FIXED)
    public List<EventDTO> viewEvents() {

        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDTO)
                .toList();
    }

    // ✅ EDIT EVENT
    public String editEvent(Long id, EventDTO dto) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());

        // Convert LocalDate → LocalDateTime
        event.setEventDate(
                dto.getEventDate() != null
                        ? dto.getEventDate().atStartOfDay()
                        : null
        );

        eventRepository.save(event);

        return "Event updated";
    }

    // ✅ DELETE EVENT
    public String deleteEvent(Long id) {

        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }

        eventRepository.deleteById(id);

        return "Event deleted";
    }

    // ✅ REGISTER FOR EVENT
    public String registerForEvent(Long userId, Long eventId) {

        EventRegistration reg = EventRegistration.builder()
                .userId(userId)
                .eventId(eventId)
                .build();

        registrationRepository.save(reg);

        return "Registered successfully";
    }

    // ✅ TRACK PARTICIPATION
    public List<EventRegistration> trackParticipation(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
