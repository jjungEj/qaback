package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.PipelineHistory;

import java.util.List;

public interface PipelineHistoryRepository extends JpaRepository<PipelineHistory, Long> {

    List<PipelineHistory> findByStatus(String status);
}

