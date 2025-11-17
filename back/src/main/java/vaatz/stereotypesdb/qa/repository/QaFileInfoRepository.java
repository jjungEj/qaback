package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.QaFileInfo;

import java.util.List;

public interface QaFileInfoRepository extends JpaRepository<QaFileInfo, Long> {
    List<QaFileInfo> findAllByOrderByUploadedAtDesc();
}

