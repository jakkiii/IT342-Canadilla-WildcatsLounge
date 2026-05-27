package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDatetimeAfterOrderByStartDatetimeAsc(LocalDateTime after);
    List<Event> findByStartDatetimeBetweenOrderByStartDatetimeAsc(LocalDateTime start, LocalDateTime end);
}
