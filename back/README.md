# QA Application Backend

Spring Boot 기반 백엔드 애플리케이션

## 요구사항

- JDK 17
- Maven 3.6+

## 빌드 및 실행

```bash
# Maven으로 빌드
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

애플리케이션이 http://localhost:8080 에서 실행됩니다.

## API 엔드포인트

- GET /api/system-status - 시스템 상태 조회
- GET /api/pipelines - 파이프라인 목록 조회
- POST /api/pipelines - 파이프라인 생성
- PUT /api/pipelines/{id} - 파이프라인 수정
- DELETE /api/pipelines/{id} - 파이프라인 삭제
- GET /api/results - 결과 목록 조회
- GET /api/results/{id} - 결과 상세 조회
- GET /api/models - 모델 목록 조회
- POST /api/models - 모델 생성
- PUT /api/models/{id} - 모델 수정
- DELETE /api/models/{id} - 모델 삭제

