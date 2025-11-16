package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.QaDocument;

import java.util.Optional;

public interface QaDocumentRepository extends JpaRepository<QaDocument, Long> {
    
    Optional<QaDocument> findByResultId(Long resultId);
}

