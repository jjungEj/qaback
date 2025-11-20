package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.QaFileInfo;
import vaatz.stereotypesdb.qa.domain.QaFileSheet;
import vaatz.stereotypesdb.qa.dto.FeedbackRequest;
import vaatz.stereotypesdb.qa.dto.QaFileInfoResponse;
import vaatz.stereotypesdb.qa.dto.SheetsUpdateRequest;
import vaatz.stereotypesdb.qa.repository.QaFileInfoRepository;
import vaatz.stereotypesdb.qa.util.ExcelToHtmlConverter;
import vaatz.stereotypesdb.qa.util.HtmlTableParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
* @ClassName	: QaFileInfoService.java
* @Description	: QA 파일 정보 관련 비즈니스 로직 처리 서비스
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 엑셀 파일 업로드 및 HTML 변환 처리
* 								- 파일 조회, 피드백 업데이트, 시트 편집 저장
* 								- 엔티티와 DTO 간 변환 처리
*/
@Service
@Transactional
public class QaFileInfoService {

    private final QaFileInfoRepository qaFileInfoRepository;

    public QaFileInfoService(QaFileInfoRepository qaFileInfoRepository) {
        this.qaFileInfoRepository = qaFileInfoRepository;
    }

    public QaFileInfoResponse uploadFile(String fileName, long fileSize, String fileType,
                                         java.io.InputStream inputStream) throws IOException {
        List<ExcelToHtmlConverter.SheetData> sheets = ExcelToHtmlConverter.convertToHtml(inputStream, fileName);
        QaFileInfo saved = persistFileWithSheets(fileName, fileSize, fileType, sheets);
        return toResponse(saved);
    }

    public QaFileInfoResponse uploadHtmlFile(String fileName, long fileSize, java.io.InputStream inputStream) throws IOException {
        List<ExcelToHtmlConverter.SheetData> sheets = HtmlTableParser.parse(inputStream, fileName);
        String fileType = resolveFileType(fileName, "html");
        QaFileInfo saved = persistFileWithSheets(fileName, fileSize, fileType, sheets);
        return toResponse(saved);
    }

    public List<QaFileInfoResponse> getAllFiles() {
        List<QaFileInfo> files = qaFileInfoRepository.findAllByOrderByUploadedAtDesc();
        return files.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public QaFileInfoResponse getFileById(Long id) {
        QaFileInfo qaFileInfo = qaFileInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));
        return toResponse(qaFileInfo);
    }

    public QaFileInfoResponse updateFeedback(Long id, FeedbackRequest request) {
        QaFileInfo qaFileInfo = qaFileInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));
        qaFileInfo.setFeedback(request.getFeedback());
        QaFileInfo saved = qaFileInfoRepository.save(qaFileInfo);
        return toResponse(saved);
    }

    public QaFileInfoResponse updateSheets(Long id, SheetsUpdateRequest request) {
        QaFileInfo qaFileInfo = qaFileInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));
        
        // 각 시트의 HTML 내용 업데이트
        for (SheetsUpdateRequest.SheetUpdate sheetUpdate : request.getSheets()) {
            qaFileInfo.getSheets().stream()
                    .filter(sheet -> sheet.getId().equals(sheetUpdate.getId()))
                    .findFirst()
                    .ifPresent(sheet -> sheet.setHtmlContent(sheetUpdate.getHtmlContent()));
        }
        
        QaFileInfo saved = qaFileInfoRepository.save(qaFileInfo);
        return toResponse(saved);
    }

    public void deleteFile(Long id) {
        QaFileInfo qaFileInfo = qaFileInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));
        // cascade = CascadeType.ALL, orphanRemoval = true 설정으로 인해
        // QaFileInfo 삭제 시 연관된 QaFileSheet도 자동으로 삭제됨
        qaFileInfoRepository.delete(qaFileInfo);
    }

    private QaFileInfo persistFileWithSheets(String fileName, long fileSize, String fileType,
                                             List<ExcelToHtmlConverter.SheetData> sheets) {
        QaFileInfo qaFileInfo = new QaFileInfo();
        qaFileInfo.setFileName(fileName);
        qaFileInfo.setFileSize(fileSize);
        qaFileInfo.setFileType(fileType);

        int order = 0;
        for (ExcelToHtmlConverter.SheetData sheetData : sheets) {
            QaFileSheet qaFileSheet = new QaFileSheet();
            qaFileSheet.setSheetName(sheetData.getSheetName());
            qaFileSheet.setSheetOrder(order++);
            qaFileSheet.setHtmlContent(sheetData.getHtmlContent());
            qaFileSheet.setQaFileInfo(qaFileInfo);
            qaFileInfo.getSheets().add(qaFileSheet);
        }

        return qaFileInfoRepository.save(qaFileInfo);
    }

    private String resolveFileType(String fileName, String defaultType) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex + 1 < fileName.length()) {
                return fileName.substring(dotIndex + 1).toLowerCase();
            }
        }
        return defaultType;
    }

    private QaFileInfoResponse toResponse(QaFileInfo qaFileInfo) {
        QaFileInfoResponse response = new QaFileInfoResponse();
        response.setId(qaFileInfo.getId());
        response.setFileName(qaFileInfo.getFileName());
        response.setFileSize(qaFileInfo.getFileSize());
        response.setFileType(qaFileInfo.getFileType());
        response.setFeedback(qaFileInfo.getFeedback());
        response.setUploadedAt(qaFileInfo.getUploadedAt());
        response.setUpdatedAt(qaFileInfo.getUpdatedAt());
        response.setSheets(qaFileInfo.getSheets().stream()
                .map(this::toSheetResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private QaFileInfoResponse toListResponse(QaFileInfo qaFileInfo) {
        QaFileInfoResponse response = new QaFileInfoResponse();
        response.setId(qaFileInfo.getId());
        response.setFileName(qaFileInfo.getFileName());
        response.setFileSize(qaFileInfo.getFileSize());
        response.setFileType(qaFileInfo.getFileType());
        response.setFeedback(qaFileInfo.getFeedback());
        response.setUploadedAt(qaFileInfo.getUploadedAt());
        response.setUpdatedAt(qaFileInfo.getUpdatedAt());
        // 목록 조회 시에는 sheets 정보 제외 (null 또는 빈 리스트)
        response.setSheets(null);
        return response;
    }

    private QaFileInfoResponse.QaFileSheetResponse toSheetResponse(QaFileSheet sheet) {
        QaFileInfoResponse.QaFileSheetResponse response = new QaFileInfoResponse.QaFileSheetResponse();
        response.setId(sheet.getId());
        response.setSheetName(sheet.getSheetName());
        response.setSheetOrder(sheet.getSheetOrder());
        response.setHtmlContent(sheet.getHtmlContent());
        return response;
    }
}

