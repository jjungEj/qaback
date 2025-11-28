# API 변경사항 - 프론트엔드 가이드

## 📅 변경일: 2025.01.XX

## 🎯 주요 변경사항

### 1. JSON 예측 파일 분할·병합 API 추가
### 2. before 폴더 검색 기능 추가
### 3. 여러 파일 동시 업로드 기능 추가

---

## 1. JSON 예측 파일 분할·병합 API

### 1.1 전체 흐름
1. `POST /api/qa/files/json/split`에 JSON 예측 파일(`predict` 또는 `predicts` 배열 포함)을 업로드합니다.
2. 백엔드는 배열 항목마다 HTML을 만들어 `before` 폴더에 저장하고, 저장된 파일 메타데이터(`WorkspaceFileResponse`) 목록을 반환합니다.
3. 프론트엔드에서 필요한 HTML만 선택해 순서를 정한 뒤, `POST /api/qa/files/json/merge`에 파일명을 전달하면 지정한 순서 그대로 `predicts` 배열을 재구성합니다.
4. 병합된 JSON은 다운로드 응답으로 전달되는 동시에 `after` 폴더에 일반 `.json`으로 저장되므로, 워크스페이스 목록에서 즉시 확인할 수 있습니다.

> ⚠️ 현재 병합 시에는 각 항목의 `title`과 `predict` 문자열만 유지하며, 추가 필드를 저장해야 한다면 백엔드에 알려주세요.

### 1.2 JSON 분할 API
| 구분 | 값 |
| --- | --- |
| Endpoint | `POST /api/qa/files/json/split` |
| Content-Type | `multipart/form-data` |
| Form field | `file` (단일 .json) |
| Response | `WorkspaceFileResponse[]` (생성된 HTML 메타데이터) |

- JSON 전체에서 이름이 `predicts` 또는 `predict`인 배열을 찾습니다. 루트 배열이 `predict`/`predicts` 형태일 경우도 자동 감지합니다.
- 각 항목은 `title` · `heading` · `name` · `id` 중 첫 번째 값과 `predict/html/body/content` 중 첫 번째 텍스트 필드를 읽어 HTML을 생성합니다.
- 파일명은 `001_제목.html` 형식으로 부여되며, 동일 제목이 있으면 `_2`, `_3` 순번이 뒤에 붙습니다.
- 반환된 리스트를 그대로 워크스페이스 목록과 동기화하면 추가 API 호출 없이 업로드 결과를 표시할 수 있습니다.

#### 템플릿 구조
분할 시 생성되는 HTML은 제목/본문을 정확히 복원할 수 있도록 data-attribute를 포함합니다.

```html
<!DOCTYPE html>
<html lang='ko'>
  <head>
    <meta charset='UTF-8' />
    <title>클러스터A</title>
  </head>
  <body data-predict-title='클러스터A'>
    <h1>클러스터A</h1>
    <div data-predict-body='true'>
      <!-- predict HTML 본문 -->
    </div>
  </body>
</html>
```

- `data-predict-title` 속성과 `<div data-predict-body="true">` 래퍼는 병합 시 제목/본문을 정확히 찾는 기준입니다. 테이블이나 이미지 편집은 자유롭게 하되, 해당 태그는 삭제하지 않는 것이 좋습니다.

### 1.3 HTML 병합 API
| 구분 | 값 |
| --- | --- |
| Endpoint | `POST /api/qa/files/json/merge` |
| Content-Type | `application/json` |
| Request | `{"fileNames":["001_foo.html","010_bar.html"],"outputFileName":"predict-review.json"}` |
| Response | JSON 파일(ByteArrayResource) + `Content-Disposition` Attachment |

- `fileNames` 배열의 순서대로 `predicts` 배열이 구성되므로, 프론트엔드에서 원하는 작업 순서로 정렬한 뒤 전달하세요.
- `outputFileName`은 확장자를 생략해도 `.json`이 자동으로 붙으며, 생략 시 `merged-predicts.json`으로 저장됩니다.
- 병합 성공 시:
  - 응답 본문: Pretty JSON 바이트 (즉시 다운로드)
  - after 폴더: 동일 내용을 가진 `.json`이 저장되고 워크스페이스 목록에서 확인 가능 (`saveJsonToAfter`)
  - `JsonMergeResult` 내부에는 저장된 파일 메타데이터가 포함되므로, 필요하다면 추후 확장에 이용할 수 있습니다.

#### Request 예시
```json
{
  "fileNames": [
    "001_클러스터A.html",
    "002_클러스터B.html"
  ],
  "outputFileName": "predict-20250128.json"
}
```

#### Response 예시 (본문)
```json
{
  "predicts": [
    {
      "title": "클러스터A",
      "predict": "<table ...>...</table>"
    },
    {
      "title": "클러스터B",
      "predict": "<table ...>...</table>"
    }
  ]
}
```

### 1.4 검증 및 주의사항
- JSON 분할 시 predict 배열을 찾지 못하면 400 에러를 반환합니다. 반드시 `predict`/`predicts` 키로 배열을 전달하세요.
- 병합 시 HTML 본문을 찾지 못하면 400 에러가 발생합니다. 분할된 템플릿의 `data-predict-body` 래퍼를 삭제하지 마세요.
- 파일 제목이나 본문에 `<` `>`와 같은 특수 문자가 포함되더라도 HTML 속성과 텍스트는 안전하게 이스케이프됩니다.
- after 폴더에 저장된 JSON은 `/api/qa/workspace` 응답의 after 섹션에서 바로 확인 가능하므로, 추가 파일 조회 API가 필요 없습니다.

---

## 2. 워크스페이스 조회 API - 검색 기능 추가

### 엔드포인트
```
GET /api/qa/workspace
```

### 변경사항
- **신규 파라미터 추가**: `beforeKeyword` (선택사항)
- before 폴더에만 검색 기능이 적용됩니다
- after, dev 폴더는 검색 기능이 없습니다

### 요청 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `afterPage` | int | 아니오 | 0 | after 폴더 페이지 번호 |
| `beforePage` | int | 아니오 | 0 | before 폴더 페이지 번호 |
| `devPage` | int | 아니오 | 0 | dev 폴더 페이지 번호 |
| `size` | int | 아니오 | 5 | 페이지당 항목 수 (최대 100) |
| `beforeKeyword` | string | 아니오 | null | **신규** before 폴더 파일명 검색어 |

### 사용 예시

#### 기본 조회 (변경 없음)
```javascript
// 기존과 동일하게 사용 가능
fetch('/api/qa/workspace?afterPage=0&beforePage=0&devPage=0&size=5')
```

#### before 폴더 검색
```javascript
// before 폴더에서 "문서"가 포함된 파일 검색
fetch('/api/qa/workspace?beforePage=0&size=5&beforeKeyword=문서')
```

#### React 예시
```jsx
const [searchKeyword, setSearchKeyword] = useState('');
const [beforePage, setBeforePage] = useState(0);

const fetchWorkspace = async () => {
  const params = new URLSearchParams({
    afterPage: 0,
    beforePage: beforePage,
    devPage: 0,
    size: 5,
  });
  
  // before 폴더 검색어가 있을 때만 추가
  if (searchKeyword.trim()) {
    params.append('beforeKeyword', searchKeyword.trim());
  }
  
  const response = await fetch(`/api/qa/workspace?${params}`);
  const data = await response.json();
  // data[1]이 before 폴더 응답 (after -> before -> dev 순서)
  return data;
};
```

### 응답 형식 (변경 없음)
```json
[
  {
    "folder": "after",
    "displayName": "수정 후 대기 중",
    "absolutePath": "C:\\qa\\qaback\\back\\workspace\\after",
    "files": [...],
    "page": 0,
    "size": 5,
    "totalPages": 2,
    "totalElements": 10
  },
  {
    "folder": "before",
    "displayName": "수정 전",
    "absolutePath": "C:\\qa\\qaback\\back\\workspace\\before",
    "files": [...],  // 검색어가 있으면 필터링된 결과
    "page": 0,
    "size": 5,
    "totalPages": 1,
    "totalElements": 3  // 검색 결과 개수
  },
  {
    "folder": "dev",
    "displayName": "Dev",
    "absolutePath": "C:\\qa\\qaback\\back\\workspace\\dev",
    "files": [...],
    "page": 0,
    "size": 5,
    "totalPages": 0,
    "totalElements": 0
  }
]
```

### 검색 기능 특징
- **대소문자 무시**: "문서"와 "문서", "TEST" 모두 동일하게 검색
- **부분 일치**: 파일명에 검색어가 포함되면 매칭
- **before 폴더 전용**: after, dev 폴더에는 검색 기능 없음
- **페이징 지원**: 검색 결과도 페이징 처리됨

---

## 3. HTML 파일 업로드 API - 다중 파일 업로드 지원

### 엔드포인트
```
POST /api/qa/files/html
Content-Type: multipart/form-data
```

### 변경사항
- **기존 기능 유지**: 단일 파일 업로드 (`file` 파라미터)는 그대로 작동
- **신규 기능 추가**: 여러 파일 동시 업로드 (`files` 파라미터)
- 단일 파일과 다중 파일 중 하나만 사용 가능

### 요청 파라미터

#### 옵션 1: 단일 파일 업로드 (기존 방식, 변경 없음)
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `file` | MultipartFile | 조건부* | 업로드할 HTML 파일 |

#### 옵션 2: 다중 파일 업로드 (신규)
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `files` | MultipartFile[] | 조건부* | 업로드할 HTML 파일 배열 |

\* `file` 또는 `files` 중 하나는 반드시 필요합니다.

### 사용 예시

#### 단일 파일 업로드 (기존 방식)
```javascript
// FormData 사용
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const response = await fetch('/api/qa/files/html', {
  method: 'POST',
  body: formData
});

const result = await response.json();
// result: WorkspaceFileResponse 객체
```

#### 다중 파일 업로드 (신규)
```javascript
// 여러 파일 선택
const formData = new FormData();
for (let i = 0; i < fileInput.files.length; i++) {
  formData.append('files', fileInput.files[i]);
}

const response = await fetch('/api/qa/files/html', {
  method: 'POST',
  body: formData
});

const results = await response.json();
// results: WorkspaceFileResponse[] 배열
```

#### React 예시 - 단일 파일
```jsx
const handleSingleUpload = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('/api/qa/files/html', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  console.log('업로드 성공:', result);
};
```

#### React 예시 - 다중 파일
```jsx
const handleMultipleUpload = async (files) => {
  const formData = new FormData();
  
  // files 배열로 추가
  Array.from(files).forEach(file => {
    formData.append('files', file);
  });
  
  const response = await fetch('/api/qa/files/html', {
    method: 'POST',
    body: formData
  });
  
  const results = await response.json();
  console.log('업로드된 파일 수:', results.length);
  results.forEach(result => {
    console.log('파일:', result.fileName);
  });
};
```

#### HTML input 예시
```html
<!-- 단일 파일 -->
<input type="file" accept=".html,.htm" id="singleFile" />

<!-- 다중 파일 -->
<input type="file" accept=".html,.htm" multiple id="multipleFiles" />

<script>
// 단일 파일 업로드
document.getElementById('singleFile').addEventListener('change', async (e) => {
  const formData = new FormData();
  formData.append('file', e.target.files[0]);
  
  const response = await fetch('/api/qa/files/html', {
    method: 'POST',
    body: formData
  });
  const result = await response.json();
});

// 다중 파일 업로드
document.getElementById('multipleFiles').addEventListener('change', async (e) => {
  const formData = new FormData();
  Array.from(e.target.files).forEach(file => {
    formData.append('files', file);
  });
  
  const response = await fetch('/api/qa/files/html', {
    method: 'POST',
    body: formData
  });
  const results = await response.json();
});
</script>
```

### 응답 형식

#### 단일 파일 업로드 응답
```json
{
  "folder": "before",
  "fileName": "문서1.html",
  "fileSize": 1024,
  "lastModifiedAt": "2025-01-XXT10:30:00",
  "extension": "html",
  "absolutePath": "C:\\qa\\qaback\\back\\workspace\\before\\문서1.html"
}
```

#### 다중 파일 업로드 응답
```json
[
  {
    "folder": "before",
    "fileName": "문서1.html",
    "fileSize": 1024,
    "lastModifiedAt": "2025-01-XXT10:30:00",
    "extension": "html",
    "absolutePath": "C:\\qa\\qaback\\back\\workspace\\before\\문서1.html"
  },
  {
    "folder": "before",
    "fileName": "문서2.html",
    "fileSize": 2048,
    "lastModifiedAt": "2025-01-XXT10:31:00",
    "extension": "html",
    "absolutePath": "C:\\qa\\qaback\\back\\workspace\\before\\문서2.html"
  }
]
```

### 주의사항
1. **파일 타입**: HTML 파일만 업로드 가능 (`.html`, `.htm`)
2. **파라미터 선택**: `file`과 `files`를 동시에 보내지 마세요. `files`가 있으면 다중 업로드로 처리됩니다.
3. **에러 처리**: 하나의 파일이라도 업로드 실패 시 전체 요청이 실패합니다.
4. **하위 호환성**: 기존 단일 파일 업로드 코드는 수정 없이 그대로 사용 가능합니다.

---

## 📝 프론트엔드 구현 가이드

### 1. 검색 기능 UI 추가

#### before 폴더에 검색 입력창 추가
```jsx
// before 폴더 섹션에 검색 입력창 추가
<div className="before-folder">
  <h3>수정 전 (before)</h3>
  <input
    type="text"
    placeholder="파일명 검색..."
    value={searchKeyword}
    onChange={(e) => setSearchKeyword(e.target.value)}
    onKeyPress={(e) => {
      if (e.key === 'Enter') {
        handleSearch();
      }
    }}
  />
  <button onClick={handleSearch}>검색</button>
  <button onClick={handleClearSearch}>초기화</button>
  
  {/* 파일 목록 */}
  {beforeFiles.map(file => (
    <div key={file.fileName}>{file.fileName}</div>
  ))}
</div>
```

### 2. 다중 파일 업로드 UI 추가

#### 파일 선택 input에 multiple 속성 추가
```jsx
<input
  type="file"
  accept=".html,.htm"
  multiple  // 여러 파일 선택 가능
  onChange={handleFileSelect}
/>
```

#### 업로드 처리
```jsx
const handleFileSelect = async (e) => {
  const files = Array.from(e.target.files);
  
  if (files.length === 0) return;
  
  const formData = new FormData();
  
  // 단일 파일인 경우
  if (files.length === 1) {
    formData.append('file', files[0]);
  } 
  // 다중 파일인 경우
  else {
    files.forEach(file => {
      formData.append('files', file);
    });
  }
  
  try {
    const response = await fetch('/api/qa/files/html', {
      method: 'POST',
      body: formData
    });
    
    if (response.ok) {
      const result = await response.json();
      // 단일 파일: 객체, 다중 파일: 배열
      const uploadedFiles = Array.isArray(result) ? result : [result];
      console.log(`${uploadedFiles.length}개 파일 업로드 완료`);
    }
  } catch (error) {
    console.error('업로드 실패:', error);
  }
};
```

---

## 🔄 마이그레이션 가이드

### 기존 코드가 있는 경우

#### 1. 워크스페이스 조회
- **변경 불필요**: 기존 코드는 그대로 작동합니다
- **선택적 추가**: 검색 기능이 필요하면 `beforeKeyword` 파라미터만 추가

#### 2. 파일 업로드
- **변경 불필요**: 기존 단일 파일 업로드 코드는 그대로 작동합니다
- **선택적 추가**: 다중 파일 업로드가 필요하면 `files` 파라미터 사용

---

## ❓ FAQ

### Q1. 검색 기능을 after나 dev 폴더에도 사용할 수 있나요?
**A**: 아니요. 검색 기능은 before 폴더에만 적용됩니다.

### Q2. 단일 파일과 다중 파일을 동시에 업로드할 수 있나요?
**A**: 아니요. `file`과 `files` 중 하나만 사용해야 합니다. `files`가 있으면 다중 업로드로 처리됩니다.

### Q3. 다중 파일 업로드 시 일부 파일만 실패하면 어떻게 되나요?
**A**: 현재는 하나의 파일이라도 실패하면 전체 요청이 실패합니다. 향후 개선 예정입니다.

### Q4. 검색어는 대소문자를 구분하나요?
**A**: 아니요. 대소문자를 구분하지 않습니다. "문서", "문서", "문서" 모두 동일하게 검색됩니다.

---

## 📞 문의
API 관련 문의사항이 있으시면 백엔드 팀에 연락해주세요.

