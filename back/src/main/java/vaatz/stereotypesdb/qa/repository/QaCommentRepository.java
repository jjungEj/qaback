package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.QaComment;

import java.util.List;

public interface QaCommentRepository extends JpaRepository<QaComment, Long> {

    List<QaComment> findByResultId(Long resultId);
}

