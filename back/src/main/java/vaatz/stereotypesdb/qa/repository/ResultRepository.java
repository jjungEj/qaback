package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.Result;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByPipelineId(Long pipelineId);
}
