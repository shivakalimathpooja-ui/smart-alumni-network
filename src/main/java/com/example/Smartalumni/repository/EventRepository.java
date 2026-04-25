package com.example.Smartalumni.repository;

import com.example.Smartalumni.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}