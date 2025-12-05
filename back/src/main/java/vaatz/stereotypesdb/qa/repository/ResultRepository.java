package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vaatz.stereotypesdb.qa.domain.Result;

/**
 * @ClassName  : ResultRepository.java
 * @Description: 결과 정보를 조회/수정하는 Repository
 * @Author     : 정은주
 * @Date       : 2025.12.04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.12.04        정은주      - 결과 Repository 인터페이스 정의
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * 파일명으로 결과가 존재하는지 확인
     */
    boolean existsByFileName(String fileName);

    /**
     * 파일명으로 폴더 필드를 업데이트
     */
    @Modifying
    @Query("UPDATE Result r SET r.folder = :folder WHERE r.fileName = :fileName")
    int updateFolderByFileName(@Param("fileName") String fileName, @Param("folder") String folder);
}

