package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.FeedbackEntry;

import java.util.List;

public interface FeedbackEntryRepository extends JpaRepository<FeedbackEntry, Long> {

    List<FeedbackEntry> findByProcessingResultId(Long processingResultId);
}

