package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.LocalFile;

public interface LocalFileRepository extends JpaRepository<LocalFile, Long> {

    Long countByStatus(String status);
}

