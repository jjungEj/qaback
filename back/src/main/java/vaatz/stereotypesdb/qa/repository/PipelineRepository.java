package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.Pipeline;

import java.util.List;

public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    List<Pipeline> findByStatus(String status);
}

