package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.EventRequest;
import edu.cit.canadilla.wildcatslounge.dto.EventResponse;
import edu.cit.canadilla.wildcatslounge.entity.Event;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEndDatetimeAfterOrderByStartDatetimeAsc(LocalDateTime.now()).stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getTodayEvents() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return eventRepository.findByStartDatetimeBetweenOrderByStartDatetimeAsc(start, end).stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse create(EventRequest request, User staff) {
        Event event = new Event();
        applyRequest(event, request);
        event.setCreatedBy(staff);
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        applyRequest(event, request);
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    private void applyRequest(Event event, EventRequest request) {
        if (request.getEndDatetime().isBefore(request.getStartDatetime())) {
            throw new RuntimeException("End datetime must be after start datetime");
        }
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setPostLink(request.getPostLink());
        event.setStartDatetime(request.getStartDatetime());
        event.setEndDatetime(request.getEndDatetime());
    }
}
