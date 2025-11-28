# QA Application Backend 안내서

코드 구조와 데이터 흐름을 빠르게 이해하고, 처음 접하는 분도 주석을 어디에 달면 좋은지 감을 잡을 수 있도록 정리했습니다.

## 1. 프로젝트 개요
- 스택: Spring Boot 2.7, Spring MVC + JPA, Lombok, H2 인메모리 DB(개발용)
- 목적: 문서 처리 파이프라인의 상태, 결과, 피드백, 로컬 파일 목록을 조회·관리하는 REST API 백엔드
- 계층: Controller → Service → Repository → Database, DTO를 통해 입·출력 분리
- 주요 도메인: `Pipeline`(작업 흐름), `Result`(처리 결과), `Feedback`(사용자 피드백), `LocalFile`(처리 대상 파일), `SystemStatus`(시스템 상태)

## 2. 실행 환경 및 빠른 시작
```bash
# 의존성 다운로드 및 테스트 포함 빌드
mvn clean install

# 개발 모드 실행
mvn spring-boot:run
```
- Maven 3.6+ 권장. `pom.xml`의 `java.version`은 8로 설정되어 있으나, Spring Boot 2.7은 JDK 11~17에서도 동작합니다.
- 기본 실행 포트는 `application.yml` 기준 8080입니다.
- 개발 중에는 H2 메모리 DB가 사용되며, `/h2-console`에서 콘솔 접속이 가능합니다.

## 3. 전체 동작 흐름
### 3.1 HTTP 요청 처리 단계
```
[클라이언트 화면] → [Controller] → [Service] → [Repository] → [JPA/Hibernate] → [H2 DB]
                       ↓                                  ↑
                     [Request DTO] → [Domain(Entity)] → [Response DTO]
```
- Controller: URL·HTTP 메서드 매핑, 입력 DTO 검증
- Service: 트랜잭션 경계, 비즈니스 로직, 엔티티 ↔ DTO 변환
- Repository: Spring Data JPA 기반 CRUD 및 파생 쿼리
- Domain(Entity): DB 테이블과 매핑, 연관관계(`Pipeline`↔`Result`↔`Feedback`)

### 3.2 핵심 데이터 흐름
1. 파이프라인이 생성되면 `Pipeline` 엔티티에 기록됩니다.
2. 각 파이프라인 수행 결과는 `Result` 로 축적되며, 결과에 대한 사용자 피드백은 `Feedback`에 저장됩니다.
3. 문서 원본은 `LocalFile` 로 관리되고, 시스템 상태 요약은 `SystemStatus`로 스냅샷을 남깁니다.
4. `SystemStatusService`는 최신 스냅샷과 당일 처리 건수를 합쳐 요약 정보를 제공합니다.

### 3.3 JSON Prediction 분할/병합 워크플로우
최근 추가된 `PredictionFileService`와 `QaController` 엔드포인트를 통해 JSON 예측 결과를 HTML로 쪼개고 다시 합칠 수 있습니다.

1. **분할** – `POST /api/qa/files/json/split`
   - `predict` 혹은 `predicts` 배열이 포함된 JSON을 업로드하면 항목마다 HTML 파일을 생성해 `before` 폴더에 저장합니다.
   - 파일명 규칙: `001_제목.html`, `002_제목.html` 식으로 순번+제목을 조합하며, 중복 제목에는 `_2`, `_3` 접미사가 붙습니다.
   - 응답은 `WorkspaceFileResponse` 배열이라 프론트에서 워크스페이스 목록과 바로 동기화할 수 있습니다.

2. **병합** – `POST /api/qa/files/json/merge`
   - 요청 본문
     ```json
     {
       "fileNames": ["001_foo.html","010_bar.html"],
       "outputFileName": "predict-20250128.json"
     }
     ```
   - `fileNames` 순서대로 `predicts` 배열이 만들어지며, 생략 시 `outputFileName`은 `merged-predicts.json`으로 저장됩니다.
   - 병합된 JSON은 곧바로 다운로드되고, `saveJsonToAfter`를 통해 동일 파일이 `after` 폴더에도 저장되어 `/api/qa/workspace` 응답에 즉시 반영됩니다.

3. **HTML 템플릿 규칙**
   - 분할 시 생성되는 HTML에는 `<body data-predict-title="...">`와 `<div data-predict-body="true">...</div>`가 포함되어, 병합 시 제목/본문을 원본 그대로 복원합니다.
   - 표나 이미지 편집은 자유롭게 가능하지만 이 data-attribute 래퍼는 삭제하지 않는 것이 좋습니다.
   - 현재 병합 시에는 각 항목의 `title`과 `predict` 필드만 유지하며, 추가 메타데이터가 필요하면 사전에 협의가 필요합니다.

## 4. API와 예상 화면 연계
| 엔드포인트 | HTTP | 설명 | 예상 화면/기능 |
| --- | --- | --- | --- |
| `/api/system-status/summary` | GET | 전체 시스템 상태 요약 | 대시보드 메인 카드 |
| `/api/system-status` | POST | 새로운 상태 스냅샷 저장 | 운영자가 상태 입력하는 폼 |
| `/api/pipelines` | GET | 파이프라인 목록 (status 필터 가능) | 파이프라인 목록 화면 |
| `/api/pipelines/{id}` | GET | 단일 파이프라인 상세 | 파이프라인 상세 팝업 |
| `/api/pipelines` | POST | 파이프라인 생성 | 파이프라인 등록 화면 |
| `/api/pipelines/{id}` | PUT | 파이프라인 수정 | 파이프라인 편집 화면 |
| `/api/pipelines/{id}` | DELETE | 파이프라인 삭제 | 목록 화면에서 삭제 |
| `/api/results/summary` | GET | 처리 결과 통계 | 결과 요약 카드 |
| `/api/results` | GET | 처리 결과 목록 (pipelineId 필터 가능) | 결과 리스트/탭 |
| `/api/results/{id}` | GET | 결과 상세 | 결과 상세 화면 |
| `/api/results` | POST | 결과 생성 | 결과 등록 화면 |
| `/api/results/{id}` | PUT | 결과 수정 | 결과 편집 화면 |
| `/api/results/{id}` | DELETE | 결과 삭제 | 결과 리스트 |
| `/api/feedback` | GET | 피드백 목록 (resultId 필터 가능) | 결과 상세의 피드백 탭 |
| `/api/feedback/{id}` | GET | 단일 피드백 조회 | 피드백 상세 모달 |
| `/api/feedback` | POST | 피드백 생성 | 피드백 작성 폼 |
| `/api/feedback/{id}` | PUT | 피드백 수정 | 피드백 편집 |
| `/api/feedback/{id}` | DELETE | 피드백 삭제 | 피드백 목록 |
| `/api/local-files/summary` | GET | 로컬 파일 통계 | 파일 관리 대시보드 |
| `/api/local-files` | GET | 로컬 파일 목록 | 파일 목록 화면 |
| `/api/local-files/{id}` | GET | 단일 파일 상세 | 파일 상세 팝업 |
| `/api/local-files` | POST | 파일 정보 생성 | 파일 등록 화면 |
| `/api/local-files/{id}` | PUT | 파일 정보 수정 | 파일 편집 |
| `/api/local-files/{id}` | DELETE | 파일 정보 삭제 | 파일 목록 |

## 5. 패키지 및 파일 상세 설명

### 5.1 `vaatz.stereotypesdb.qa.config`
| 파일 | 주요 역할 | 주석 추천 위치 |
| --- | --- | --- |
| `CorsConfig` | 글로벌 CORS 허용 설정. 모든 출처/헤더/메서드를 허용해 프론트엔드 개발 편의성을 높임. | 클래스 선언부: "개발 환경 기본 CORS"; `config.addAllowedOriginPattern("*")`: 실제 운영 시 조정 필요하다는 안내 |

### 5.2 `controller` 패키지
| 파일 | 주요 역할 | 화면/맥락 | 주석 추천 위치 |
| --- | --- | --- | --- |
| `FeedbackController` | 피드백 CRUD와 결과 연동 | 결과 상세 화면의 피드백 탭, 운영자 피드백 관리 | 클래스 상단: "피드백 REST 진입점"; `getFeedback(Long resultId)`: 선택 필터 동작 설명; `createFeedback`: 201 응답 반환 이유 |
| `LocalFileController` | 로컬 파일 목록·요약·CRUD | 파일 관리 화면, 큐 모니터링 | `getSummary()`: 통계 항목 의미; `createFile`: 요청 DTO 필드 의미 요약; 삭제 시 204 반환 이유 |
| `PipelineController` | 파이프라인 목록, 단건, CRUD | 배치 파이프라인 목록/상세 화면 | 클래스 상단: 상태 필터 소개; `createPipeline`: DTO 필드=스케줄 값 매핑; `deletePipeline`: 연관 자료 주의 사항 |
| `ResultController` | 처리 결과 통계/목록/CRUD | 결과 요약 카드, 결과 상세 페이지 | `getResults(Long pipelineId)`: 파이프라인 필터/연관성 설명; `createResult`: 파일 사이즈 필드 의미; `getSummary`: 성공·실패 카운트 해석 |
| `SystemStatusController` | 시스템 상태 요약 조회 및 스냅샷 저장 | 메인 대시보드, 운영자 입력 화면 | `getSummary()`: null 처리 시 기본값; `createStatus`: 요청 DTO 검증 포인트 |

### 5.3 `service` 패키지
| 파일 | 주요 역할 | 주석 추천 위치 |
| --- | --- | --- |
| `FeedbackService` | 피드백 비즈니스 로직, 결과 상태 동기화, DTO 변환 | 클래스 상단: 트랜잭션 범위 설명; `applyResultUpdate`: 결과 상태/메타데이터 동시 갱신 로직; `toResponse`: DTO 변환 규칙 |
| `LocalFileService` | 로컬 파일 CRUD 및 통계(H2 카운트) | `getSummary()`: 상태 코드 정의(PENDING/COMPLETED); `applyRequest`: null 허용 필드 정리; `delete(Long id)`: 삭제 전 예외 처리 |
| `PipelineService` | 파이프라인 CRUD, Duration 계산 | `applyRequest`: Duration 계산 시 null 처리; `getAll(String status)`: 조건 검색 분기 설명 |
| `ResultService` | 결과 CRUD, 파이프라인 연동, 요약 통계 | `create/update`: 파이프라인 존재 확인 중요성; `applyRequest`: 파일 사이즈 필드 의미; `getSummary`: 상태 값 표준 |
| `SystemStatusService` | 시스템 상태 요약 계산, 스냅샷 저장 | `getSummary()`: 오늘 처리 건수 계산 로직; `create`: 업타임 처리 방식; 스냅샷 null일 때 기본값 결정 |

### 5.4 `repository` 패키지
| 파일 | 주요 역할 | 주석 추천 위치 |
| --- | --- | --- |
| `FeedbackRepository` | 피드백 CRUD, 결과 ID 기반 검색 | `findByResultId`: N:1 관계 설명 |
| `LocalFileRepository` | 로컬 파일 CRUD, 상태별 카운트 | `countByStatus`: 상태 코드 목록 명시 |
| `PipelineRepository` | 파이프라인 CRUD, 상태별 목록 | `findByStatus`: 인덱스 필요성, 대소문자 주의 |
| `ResultRepository` | 결과 CRUD, 파이프라인별 목록, 상태/일자 통계 | `countByStartedAtBetween`: 시간대(UTC/로컬) 주석; `findByPipelineId`: Lazy 로딩 주의 |
| `SystemStatusRepository` | 시스템 상태 최신 스냅샷 조회 | `findTopByOrderByRecordedAtDesc`: 정렬 기준 주석 |

### 5.5 `domain` 패키지 (엔티티)
| 파일 | 핵심 필드/관계 | 주석 추천 위치 |
| --- | --- | --- |
| `Feedback` | `Result`와 N:1 (`@ManyToOne`), `feedback_entries` 테이블 | 클래스 상단: 테이블명; `status`: 상태 값 예시; `@JsonIgnore result`: 직렬화 차단 이유 |
| `LocalFile` | 파일 메타데이터, 상태, 타임스탬프 | `status`: PENDING/COMPLETED 같은 상태값 안내; `deletable`: 사용 목적 |
| `Pipeline` | 문서명, 상태, 시작/종료 시각, 결과 목록 | `results`: 지연 로딩 및 순환 참조 주의; `errorMessage`: 실패시 메시지 |
| `Result` | 파이프라인 N:1, 피드백 1:N, 파일 사이즈/URI/메타데이터 | `metadata`: 대용량 텍스트(`@Lob`) 목적; `originalViewerUri/htmlRenderUri`: 표시 용도 |
| `SystemStatus` | AI DB/Helpy 상태, 업타임, 전체 문서 수 | `uptimeSince`: null 허용 이유; `recordedAt`: 자동 기록 시점 |

### 5.6 `dto` 패키지
#### Feedback 관련
| 파일 | 용도 | 주석 추천 위치 |
| --- | --- | --- |
| `FeedbackRequest` | 피드백 생성/수정 요청, 결과 ID·내용·상태·연동정보 포함 | 필드별 `@NotNull/@NotBlank` 이유; `updatedResultStatus/updatedMetadata`: 결과 동시 수정 용도 |
| `FeedbackResponse` | 피드백 응답, 작성/수정 시각 포함 | `createdAt/updatedAt`: 타임스탬프 포맷 언급 |

#### LocalFile 관련
| 파일 | 용도 | 주석 추천 위치 |
| --- | --- | --- |
| `LocalFileRequest` | 로컬 파일 생성/수정 요청 | `status`: 값 표준; `queuedAt/completedAt`: 시간대 주의 |
| `LocalFileResponse` | 로컬 파일 응답 | `deletable`: UI에서 버튼 노출 조건 |
| `LocalFileSummaryResponse` | 총 문서/대기/완료 개수 | 각 필드: 계산 기준(전체 vs 필터) |

#### Pipeline 관련
| 파일 | 용도 | 주석 추천 위치 |
| --- | --- | --- |
| `PipelineRequest` | 파이프라인 생성/수정 요청 | `startedAt/finishedAt`: ISO-8601 포맷 권장; `errorMessage`: 실패 시만 |
| `PipelineResponse` | 파이프라인 응답 | `durationSeconds`: 계산 방식 설명 |

#### Result 관련
| 파일 | 용도 | 주석 추천 위치 |
| --- | --- | --- |
| `ResultRequest` | 결과 생성/수정 요청, 파이프라인 ID 필수 | `pipelineId`: 연관 필수성; 파일 사이즈 필드: 단위(B) 명시; URI 필드: 저장 포맷 |
| `ResultDetailResponse` | 결과 응답 상세 | `metadata`: JSON 문자열 여부 |
| `ResultSummaryResponse` | 결과 요약 (총/완료/실패) | 상태별 카운트 정의 |

#### SystemStatus 관련
| 파일 | 용도 | 주석 추천 위치 |
| --- | --- | --- |
| `SystemStatusRequest` | 시스템 상태 스냅샷 생성 요청 | 각 상태 필드: 값 범위(OK/WARNING 등); `uptimeSince`: null 허용 이유 |
| `SystemStatusSummaryResponse` | 요약 응답, 업타임·당일 처리 수 포함 | `uptimeSeconds`: 계산 기준; `lastCheckedAt`: 타임존 명시 |

## 6. 설정 및 메타 파일
| 파일 | 설명 | 주석 추천 위치 |
| --- | --- | --- |
| `src/main/resources/application.yml` | 서버 포트, H2 DB, JPA 설정, H2 콘솔 노출 | 각 섹션 위에 "운영 전환 시 체크" 메모; `ddl-auto: update` 위험성 |
| `pom.xml` | Maven 빌드 스크립트, 의존성, 플러그인, System scope 라이브러리 | 시스템 의존성(`lib/`) 필요성; `java.version`과 실제 JDK 차이 메모; Nexus 저장소 사용 이유 |

## 7. 주석 작성 일반 팁
- 클래스 주석: 해당 화면/기능에서 어떤 데이터를 주고받는지, 외부 시스템과 연관이 있는지 선명하게 적습니다.
- 메서드 주석: 파라미터 사용 방식(예: 필터 조건, 필수/선택), 성공·실패 시나리오, 반환 DTO 구성 요소를 설명합니다.
- DTO 주석: 필드 단위로 단위(바이트, 초 등), 포맷(ISO-8601), 값 범위(ENUM 문자열) 등을 적어두면 프론트엔드와 협업이 쉬워집니다.
- 엔티티 주석: 테이블명, 연관관계 방향, Lazy/Eager 로딩 주의점을 남겨두면 유지보수 시 혼란을 줄일 수 있습니다.
- 설정 파일 주석: 개발/운영 전환 시 수정해야 하는 값을 미리 표시해두면 배포 사고를 예방할 수 있습니다.

## 8. 다음 단계 제안
- 실제 운영 DB와 연동할 경우 `application.yml`의 데이터소스와 CORS 허용 범위를 조정하세요.
- 반복적으로 사용하는 상태 문자열(PENDING, COMPLETED 등)은 ENUM으로 옮기고, 해당 ENUM 클래스에 추가 설명 주석을 남기면 더 안전합니다.
- API 문서화를 위해 Spring REST Docs 또는 Swagger(OpenAPI)를 도입하면, 이 README의 설명과 주석이 자동 문서화와 연결되어 일관성을 유지할 수 있습니다.

