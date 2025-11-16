package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vaatz.stereotypesdb.qa.domain.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByPipelineId(Long pipelineId);

    Long countByStatus(String status);

    Long countByStartedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Result r WHERE r.id IN (SELECT lf.result.id FROM LocalFile lf WHERE lf.result IS NOT NULL)")
    List<Result> findAllFromLocalFiles();
}

