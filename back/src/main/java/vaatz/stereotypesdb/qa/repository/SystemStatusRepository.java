package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.SystemStatus;

import java.util.Optional;

public interface SystemStatusRepository extends JpaRepository<SystemStatus, Long> {

    Optional<SystemStatus> findTopByOrderByRecordedAtDesc();
}

