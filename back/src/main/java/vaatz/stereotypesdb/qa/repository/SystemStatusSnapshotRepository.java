package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.SystemStatusSnapshot;

import java.util.Optional;

public interface SystemStatusSnapshotRepository extends JpaRepository<SystemStatusSnapshot, Long> {

    Optional<SystemStatusSnapshot> findTopByOrderByRecordedAtDesc();
}

