package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.ProcessingResult;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessingResultRepository extends JpaRepository<ProcessingResult, Long> {

    List<ProcessingResult> findByPipelineHistoryId(Long pipelineHistoryId);

    Long countByStatus(String status);

    Long countByStartedAtBetween(LocalDateTime start, LocalDateTime end);
}

