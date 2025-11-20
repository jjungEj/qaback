package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vaatz.stereotypesdb.qa.domain.QaFileInfo;

import java.util.List;
import java.util.Optional;

/**
* @ClassName	: QaFileInfoRepository.java
* @Description	: QaFileInfo 엔티티에 대한 데이터 접근 레이어
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- JPA Repository를 통한 파일 정보 CRUD 처리
* 								- 업로드 날짜 기준 내림차순 정렬 조회 메서드 제공
* 2025.11.18        정은주       	- 페이징 처리 메서드 추가
* 								- 파일명 중복 체크 메서드 추가
* 								- 파일명 검색 메서드 추가
*/
public interface QaFileInfoRepository extends JpaRepository<QaFileInfo, Long> {
    List<QaFileInfo> findAllByOrderByUploadedAtDesc();
    
    Page<QaFileInfo> findAllByOrderByUploadedAtDesc(Pageable pageable);
    
    Optional<QaFileInfo> findByFileName(String fileName);
    
    Page<QaFileInfo> findByFileNameContainingIgnoreCaseOrderByUploadedAtDesc(String fileName, Pageable pageable);
}

