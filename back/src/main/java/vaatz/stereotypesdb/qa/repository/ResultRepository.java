package vaatz.stereotypesdb.qa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vaatz.stereotypesdb.qa.model.ResultEntity;

/**
* @ClassName  : ResultRepository.java
* @Description: 결과 테이블 접근용 Spring Data JPA 저장소
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
*/
@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
}
