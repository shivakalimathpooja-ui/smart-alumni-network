package com.example.Smartalumni.mapper;

import com.example.Smartalumni.dto.EventDTO;
import com.example.Smartalumni.entity.Event;

public class EventMapper {

    public static EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                 .eventDate(event.getEventDate() != null
                        ? event.getEventDate().toLocalDate()
                        : null)
                .build();
    }

    public static Event toEntity(EventDTO dto) {
        return Event.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                 .eventDate(dto.getEventDate() != null
                        ? dto.getEventDate().atStartOfDay()
                        : null)
                .build();
    }
}