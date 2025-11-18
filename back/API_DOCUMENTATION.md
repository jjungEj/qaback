# QA 파일 관리 API 문서

## 기본 정보
- **Base URL**: `http://localhost:8080/api/qa`
- **Content-Type**: `application/json` (일부는 `multipart/form-data`)

---

## 1. 파일 업로드 및 저장

### `POST /api/qa/upload`

엑셀 파일(xlsx, xls, csv)을 업로드하고 HTML로 변환하여 DB에 저장합니다.

**Request**
- **Content-Type**: `multipart/form-data`
- **Body**: 
  - `file`: 파일 (FormData)

**Response** (201 Created)
```json
{
  "id": 1,
  "fileName": "example.xlsx",
  "fileSize": 12345,
  "fileType": "xlsx",
  "feedback": null,
  "uploadedAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "sheets": [
    {
      "id": 1,
      "sheetName": "Sheet1",
      "sheetOrder": 0,
      "htmlContent": "<table>...</table>"
    }
  ]
}
```

**에러 응답**
- `400 Bad Request`: 파일이 비어있거나 지원하지 않는 형식
- `500 Internal Server Error`: 서버 오류

**예시 (JavaScript)**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const response = await fetch('http://localhost:8080/api/qa/upload', {
  method: 'POST',
  body: formData
});

const data = await response.json();
```

---

## 2. 파일 목록 조회

### `GET /api/qa/files`

업로드된 모든 파일의 목록을 조회합니다.

**Response** (200 OK)
```json
[
  {
    "id": 1,
    "fileName": "example.xlsx",
    "fileSize": 12345,
    "fileType": "xlsx",
    "feedback": "병합 셀 확인 필요",
    "uploadedAt": "2024-01-01T10:00:00"
  },
  {
    "id": 2,
    "fileName": "data.csv",
    "fileSize": 5678,
    "fileType": "csv",
    "feedback": null,
    "uploadedAt": "2024-01-01T09:00:00"
  }
]
```

**정렬**: 업로드 시간 내림차순 (최신순)

**예시 (JavaScript)**
```javascript
const response = await fetch('http://localhost:8080/api/qa/files');
const fileList = await response.json();

// 목록 표시
fileList.forEach(file => {
  console.log(`${file.fileName} (${file.fileType}) - ${file.fileSize} bytes`);
  if (file.feedback) {
    console.log(`확인사항: ${file.feedback}`);
  }
});
```

---

## 3. 파일 상세 조회

### `GET /api/qa/files/{id}`

특정 파일의 상세 정보와 모든 시트의 HTML 내용을 조회합니다.

**Path Parameters**
- `id`: 파일 ID (Long)

**Response** (200 OK)
```json
{
  "id": 1,
  "fileName": "example.xlsx",
  "fileSize": 12345,
  "fileType": "xlsx",
  "feedback": "병합 셀 확인 필요",
  "uploadedAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "sheets": [
    {
      "id": 1,
      "sheetName": "Sheet1",
      "sheetOrder": 0,
      "htmlContent": "<table border='1'>...</table>"
    },
    {
      "id": 2,
      "sheetName": "Sheet2",
      "sheetOrder": 1,
      "htmlContent": "<table border='1'>...</table>"
    }
  ]
}
```

**에러 응답**
- `404 Not Found`: 파일을 찾을 수 없음

**예시 (JavaScript)**
```javascript
const response = await fetch(`http://localhost:8080/api/qa/files/${fileId}`);
const fileDetail = await response.json();

// HTML 테이블 표시
fileDetail.sheets.forEach(sheet => {
  const container = document.getElementById('sheet-container');
  container.innerHTML = sheet.htmlContent; // 또는 dangerouslySetInnerHTML (React)
});
```

---

## 4. 피드백 저장

### `PUT /api/qa/files/{id}/feedback`

파일의 확인 사항(피드백)을 저장합니다.

**Path Parameters**
- `id`: 파일 ID (Long)

**Request Body**
```json
{
  "feedback": "병합 셀 확인 필요. 3행 2열 부분 수정 완료."
}
```

**Response** (200 OK)
```json
{
  "id": 1,
  "fileName": "example.xlsx",
  "fileSize": 12345,
  "fileType": "xlsx",
  "feedback": "병합 셀 확인 필요. 3행 2열 부분 수정 완료.",
  "uploadedAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:30:00",
  "sheets": [...]
}
```

**에러 응답**
- `400 Bad Request`: 피드백 내용이 비어있음
- `404 Not Found`: 파일을 찾을 수 없음

**예시 (JavaScript)**
```javascript
const response = await fetch(`http://localhost:8080/api/qa/files/${fileId}/feedback`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    feedback: '병합 셀 확인 필요'
  })
});

const updatedFile = await response.json();
```

---

## 5. JSONL 변환 (선택사항)

### `POST /api/qa/convert/jsonl`

수정된 HTML을 JSONL 형식으로 변환하여 다운로드합니다.

**Request Body**
```json
{
  "fileName": "example.xlsx",
  "sheets": [
    {
      "sheetName": "Sheet1",
      "htmlContent": "<table>...</table>"
    }
  ]
}
```

**Response** (200 OK)
- **Content-Type**: `application/octet-stream`
- **Content-Disposition**: `attachment; filename="example.jsonl"`
- **Body**: JSONL 파일 (바이너리)

**JSONL 형식**
각 줄은 다음과 같은 JSON 객체입니다:
```json
{"image":"","html":"<table>...</table>"}
```

**예시 (JavaScript)**
```javascript
const response = await fetch('http://localhost:8080/api/qa/convert/jsonl', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    fileName: 'example.xlsx',
    sheets: [
      {
        sheetName: 'Sheet1',
        htmlContent: '<table>...</table>'
      }
    ]
  })
});

const blob = await response.blob();
const url = window.URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'example.jsonl';
a.click();
```

---

## 프론트엔드 구현 가이드

### 1. 파일 목록 화면

```javascript
// 파일 목록 조회
const getFileList = async () => {
  const response = await fetch('http://localhost:8080/api/qa/files');
  const files = await response.json();
  
  return files.map(file => ({
    id: file.id,
    fileName: file.fileName,
    fileSize: formatFileSize(file.fileSize), // 예: "12.3 KB"
    fileType: file.fileType.toUpperCase(),
    feedback: file.feedback || '-',
    uploadedAt: formatDate(file.uploadedAt) // 예: "2024-01-01 10:00"
  }));
};
```

**표시할 정보**
- 파일명 (`fileName`)
- 파일 크기 (`fileSize`) - 바이트를 KB/MB로 변환
- 타입 (`fileType`) - xlsx, csv 등
- 확인 사항 (`feedback`) - 없으면 "-" 표시
- 상세보기 버튼 → `/files/{id}` 페이지로 이동

### 2. 파일 상세 화면

```javascript
// 파일 상세 조회
const getFileDetail = async (fileId) => {
  const response = await fetch(`http://localhost:8080/api/qa/files/${fileId}`);
  const file = await response.json();
  
  // 각 시트의 HTML을 표시
  file.sheets.forEach((sheet, index) => {
    const container = document.getElementById(`sheet-${index}`);
    container.innerHTML = sheet.htmlContent;
  });
  
  return file;
};
```

**표시할 내용**
- 파일 정보 (파일명, 크기, 타입, 업로드 시간)
- 피드백 입력/수정 폼
- 각 시트의 HTML 테이블 표시 (시트가 여러 개면 탭 또는 아코디언으로)

### 3. 피드백 입력

```javascript
// 피드백 저장
const saveFeedback = async (fileId, feedback) => {
  const response = await fetch(`http://localhost:8080/api/qa/files/${fileId}/feedback`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ feedback })
  });
  
  if (response.ok) {
    alert('피드백이 저장되었습니다.');
    // 목록 화면으로 돌아가거나 새로고침
  }
};
```

---

## 유틸리티 함수 예시

```javascript
// 파일 크기 포맷팅
function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// 날짜 포맷팅
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}
```

---

## 주의사항

1. **CORS**: 백엔드에서 CORS 설정이 되어있으므로 별도 설정 불필요
2. **에러 처리**: 모든 API 호출에 try-catch 또는 .catch() 추가 권장
3. **HTML 표시**: `htmlContent`를 직접 표시할 때는 XSS 주의 (React의 경우 `dangerouslySetInnerHTML` 사용)
4. **파일 크기**: 큰 파일의 경우 업로드 시간이 걸릴 수 있으므로 로딩 표시 권장

