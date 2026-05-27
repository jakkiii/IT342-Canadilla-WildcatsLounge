package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.LoungeStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoungeStatusResponse {
    private String occupancyLevel;
    private String displayLabel;
    private String color; // green, yellow, red, gray
    private LocalDateTime lastUpdatedAt;

    public static LoungeStatusResponse from(LoungeStatus status) {
        LoungeStatusResponse r = new LoungeStatusResponse();
        r.setOccupancyLevel(status.getOccupancyLevel());
        r.setLastUpdatedAt(status.getLastUpdatedAt());
        switch (status.getOccupancyLevel()) {
            case "low" -> {
                r.setDisplayLabel("Available");
                r.setColor("green");
            }
            case "medium" -> {
                r.setDisplayLabel("Almost Full");
                r.setColor("yellow");
            }
            case "full" -> {
                r.setDisplayLabel("Full");
                r.setColor("red");
            }
            case "closed" -> {
                r.setDisplayLabel("Closed");
                r.setColor("gray");
            }
            default -> {
                r.setDisplayLabel("Unknown");
                r.setColor("gray");
            }
        }
        return r;
    }
}
