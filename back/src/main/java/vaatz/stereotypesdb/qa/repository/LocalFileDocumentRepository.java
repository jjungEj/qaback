package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.LocalFileDocument;

public interface LocalFileDocumentRepository extends JpaRepository<LocalFileDocument, Long> {

    Long countByStatus(String status);
}

