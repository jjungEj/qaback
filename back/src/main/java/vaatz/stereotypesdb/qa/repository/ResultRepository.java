package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByPipelineId(Long pipelineId);

    Long countByStatus(String status);

    Long countByStartedAtBetween(LocalDateTime start, LocalDateTime end);
}

