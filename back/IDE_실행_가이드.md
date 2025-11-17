# IDE에서 실행하기 가이드

## 문제: ClassNotFoundException 발생 시

IDE에서 `ClassNotFoundException`이 발생하면 프로젝트가 제대로 빌드되지 않았거나 Maven 프로젝트로 인식되지 않은 경우입니다.

## 해결 방법

### IntelliJ IDEA

1. **Maven 프로젝트로 인식시키기**
   - 우측 상단의 `Maven` 탭 클릭
   - 또는 `View` → `Tool Windows` → `Maven`
   - `Reload All Maven Projects` 버튼 클릭 (새로고침 아이콘)

2. **프로젝트 빌드**
   - `Build` → `Rebuild Project`
   - 또는 `Ctrl + Shift + F9`

3. **Maven으로 실행**
   - `Maven` 탭에서 `Lifecycle` → `spring-boot:run` 더블클릭

4. **직접 실행**
   - `QaApplication.java` 파일 열기
   - `main` 메서드 옆의 초록색 실행 버튼 클릭
   - 또는 우클릭 → `Run 'QaApplication'`

### Eclipse / STS

1. **Maven 프로젝트로 변환**
   - 프로젝트 우클릭 → `Configure` → `Convert to Maven Project`

2. **프로젝트 업데이트**
   - 프로젝트 우클릭 → `Maven` → `Update Project...`
   - `Force Update of Snapshots/Releases` 체크
   - `OK` 클릭

3. **프로젝트 빌드**
   - 프로젝트 우클릭 → `Build Project`

4. **실행**
   - `QaApplication.java` 파일 열기
   - 우클릭 → `Run As` → `Spring Boot App`

### VS Code

1. **Java Extension Pack 설치 확인**
   - Extensions에서 "Java Extension Pack" 설치

2. **Maven 프로젝트 인식**
   - `Ctrl + Shift + P` → "Java: Clean Java Language Server Workspace"
   - 프로젝트 폴더 다시 열기

3. **실행**
   - `QaApplication.java` 파일에서 `main` 메서드 위의 `Run` 링크 클릭

## 대안: Maven Wrapper 사용

프로젝트에 `mvnw` (Maven Wrapper)가 있다면:

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

## 확인 사항

1. **Java 버전 확인**
   - IDE에서 `File` → `Project Structure` (IntelliJ) 또는 `Properties` (Eclipse)
   - Java 버전이 8 이상인지 확인

2. **Maven 설정 확인**
   - IDE의 Maven 설정에서 JDK 버전 확인
   - Maven 홈 디렉토리 설정 확인

3. **프로젝트 구조 확인**
   - `src/main/java/vaatz/stereotypesdb/qa/QaApplication.java` 파일이 존재하는지 확인

## 여전히 안 되면

1. IDE를 완전히 종료
2. 프로젝트 폴더에서 `.idea` (IntelliJ) 또는 `.settings` (Eclipse) 폴더 삭제
3. IDE 다시 열고 프로젝트 Import
4. Maven 프로젝트로 인식시키기

