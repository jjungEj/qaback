package vaatz.stereotypesdb.qa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.QaFileInfo;
import vaatz.stereotypesdb.qa.domain.QaFileSheet;
import vaatz.stereotypesdb.qa.dto.FeedbackRequest;
import vaatz.stereotypesdb.qa.dto.MultiUploadResponse;
import vaatz.stereotypesdb.qa.dto.QaFileInfoResponse;
import vaatz.stereotypesdb.qa.dto.QaFilePageResponse;
import vaatz.stereotypesdb.qa.dto.SheetsUpdateRequest;
import vaatz.stereotypesdb.qa.repository.QaFileInfoRepository;
import vaatz.stereotypesdb.qa.util.ExcelToHtmlConverter;
import vaatz.stereotypesdb.qa.util.HtmlTableParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

    private static final String DUPLICATE_MESSAGE = "이미 업로드 된 파일입니다.";
    private static final List<String> SUPPORTED_SPREADSHEET_EXTENSIONS =
            Arrays.asList("xlsx", "xls", "csv");

    private final QaFileInfoRepository qaFileInfoRepository;

    public QaFileInfoService(QaFileInfoRepository qaFileInfoRepository) {
        this.qaFileInfoRepository = qaFileInfoRepository;
    }

    public QaFileInfoResponse uploadFile(String fileName, long fileSize, String fileType,
                                         InputStream inputStream) throws IOException {
        assertNotDuplicate(fileName);
        List<ExcelToHtmlConverter.SheetData> sheets = ExcelToHtmlConverter.convertToHtml(inputStream, fileName);
        return saveFileWithSheets(fileName, fileSize, fileType, sheets);
    }

    public QaFileInfoResponse uploadHtmlFile(String fileName, long fileSize, InputStream inputStream) throws IOException {
        assertNotDuplicate(fileName);
        List<ExcelToHtmlConverter.SheetData> sheets = HtmlTableParser.parse(inputStream, fileName);
        String fileType = resolveFileType(fileName, "html");
        return saveFileWithSheets(fileName, fileSize, fileType, sheets);
    }

    public QaFilePageResponse getFiles(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Page<QaFileInfo> filePage = (keyword == null || keyword.isBlank())
                ? qaFileInfoRepository.findAll(pageable)
                : qaFileInfoRepository.findByFileNameContainingIgnoreCase(keyword, pageable);

        QaFilePageResponse response = new QaFilePageResponse();
        response.setFiles(filePage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList()));
        response.setCurrentPage(filePage.getNumber());
        response.setPageSize(filePage.getSize());
        response.setTotalElements(filePage.getTotalElements());
        response.setTotalPages(filePage.getTotalPages());
        response.setHasNext(filePage.hasNext());
        return response;
    }

    public MultiUploadResponse uploadMultipleFiles(List<MultipartFile> files) {
        MultiUploadResponse response = new MultiUploadResponse();
        if (files == null || files.isEmpty()) {
            response.setTotalRequested(0);
            return response;
        }

        response.setTotalRequested(files.size());
        for (MultipartFile file : files) {
            String originalFileName = file != null ? file.getOriginalFilename() : null;

            if (file == null || file.isEmpty()) {
                response.addFailedFile(originalFileName, "파일이 비어 있습니다.");
                continue;
            }

            if (originalFileName == null) {
                response.addFailedFile("unknown", "파일 이름을 확인할 수 없습니다.");
                continue;
            }

            if (!isSupportedSpreadsheet(originalFileName)) {
                response.addFailedFile(originalFileName, "지원하지 않는 파일 형식입니다.");
                continue;
            }

            if (isDuplicateFileName(originalFileName)) {
                response.addDuplicateFile(originalFileName, DUPLICATE_MESSAGE);
                continue;
            }

            try (InputStream inputStream = file.getInputStream()) {
                String fileType = resolveFileType(originalFileName, resolveDefaultSpreadsheetType(originalFileName));
                List<ExcelToHtmlConverter.SheetData> sheets = ExcelToHtmlConverter.convertToHtml(inputStream, originalFileName);
                QaFileInfoResponse saved = saveFileWithSheets(originalFileName, file.getSize(), fileType, sheets);
                response.addUploadedFile(saved);
            } catch (IOException e) {
                response.addFailedFile(originalFileName, "업로드 중 오류가 발생했습니다.");
            }
        }

        return response;
    }

    public boolean isDuplicateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        return qaFileInfoRepository.existsByFileNameIgnoreCase(fileName);
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

    private QaFileInfoResponse saveFileWithSheets(String fileName, long fileSize, String fileType,
                                                  List<ExcelToHtmlConverter.SheetData> sheets) {
        QaFileInfo saved = persistFileWithSheets(fileName, fileSize, fileType, sheets);
        return toResponse(saved);
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

    private void assertNotDuplicate(String fileName) {
        if (isDuplicateFileName(fileName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, DUPLICATE_MESSAGE);
        }
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

    private boolean isSupportedSpreadsheet(String fileName) {
        String extension = resolveFileType(fileName, "");
        return SUPPORTED_SPREADSHEET_EXTENSIONS.contains(extension);
    }

    private String resolveDefaultSpreadsheetType(String fileName) {
        String extension = resolveFileType(fileName, "xlsx");
        return SUPPORTED_SPREADSHEET_EXTENSIONS.contains(extension) ? extension : "xlsx";
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

