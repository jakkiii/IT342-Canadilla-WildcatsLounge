package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.LoungeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoungeStatusRepository extends JpaRepository<LoungeStatus, Long> {
    Optional<LoungeStatus> findFirstByOrderByLastUpdatedAtDesc();
}
