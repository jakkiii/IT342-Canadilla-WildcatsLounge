package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String postLink;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    public static EventResponse from(Event event) {
        EventResponse r = new EventResponse();
        r.setId(event.getId());
        r.setTitle(event.getTitle());
        r.setDescription(event.getDescription());
        r.setPostLink(event.getPostLink());
        r.setStartDatetime(event.getStartDatetime());
        r.setEndDatetime(event.getEndDatetime());
        return r;
    }
}
