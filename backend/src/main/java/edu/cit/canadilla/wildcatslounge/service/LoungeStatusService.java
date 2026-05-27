package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.LoungeStatusRequest;
import edu.cit.canadilla.wildcatslounge.dto.LoungeStatusResponse;
import edu.cit.canadilla.wildcatslounge.entity.LoungeStatus;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.LoungeStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoungeStatusService {

    private final LoungeStatusRepository loungeStatusRepository;

    @Transactional(readOnly = true)
    public LoungeStatusResponse getCurrentStatus() {
        LoungeStatus status = loungeStatusRepository.findFirstByOrderByLastUpdatedAtDesc()
                .orElseGet(this::defaultStatus);
        return LoungeStatusResponse.from(status);
    }

    @Transactional
    public LoungeStatusResponse updateStatus(LoungeStatusRequest request, User staff) {
        LoungeStatus status = new LoungeStatus();
        status.setOccupancyLevel(request.getOccupancyLevel().toLowerCase());
        status.setUpdatedBy(staff);
        return LoungeStatusResponse.from(loungeStatusRepository.save(status));
    }

    private LoungeStatus defaultStatus() {
        LoungeStatus s = new LoungeStatus();
        s.setOccupancyLevel("low");
        s.setLastUpdatedAt(java.time.LocalDateTime.now());
        return s;
    }
}
