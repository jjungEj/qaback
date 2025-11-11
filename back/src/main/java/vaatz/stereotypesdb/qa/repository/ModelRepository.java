package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
