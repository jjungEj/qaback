package vaatz.stereotypesdb.qa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.QaFileInfo;
import vaatz.stereotypesdb.qa.domain.QaFileSheet;
import vaatz.stereotypesdb.qa.dto.*;
import vaatz.stereotypesdb.qa.repository.QaFileInfoRepository;
import vaatz.stereotypesdb.qa.util.ExcelToHtmlConverter;
import vaatz.stereotypesdb.qa.util.HtmlTableParser;

import java.io.IOException;
import java.util.ArrayList;
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
        // 중복 파일 체크
        if (qaFileInfoRepository.findByFileName(fileName).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 업로드한 파일입니다: " + fileName);
        }
        
        List<ExcelToHtmlConverter.SheetData> sheets = ExcelToHtmlConverter.convertToHtml(inputStream, fileName);
        QaFileInfo saved = persistFileWithSheets(fileName, fileSize, fileType, sheets);
        return toResponse(saved);
    }

    public QaFileInfoResponse uploadHtmlFile(String fileName, long fileSize, java.io.InputStream inputStream) throws IOException {
        // 중복 파일 체크
        if (qaFileInfoRepository.findByFileName(fileName).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 업로드한 파일입니다: " + fileName);
        }
        
        List<ExcelToHtmlConverter.SheetData> sheets = HtmlTableParser.parse(inputStream, fileName);
        String fileType = resolveFileType(fileName, "html");
        QaFileInfo saved = persistFileWithSheets(fileName, fileSize, fileType, sheets);
        return toResponse(saved);
    }
    
    public MultiUploadResponse uploadMultipleFiles(MultipartFile[] files) {
        List<QaFileInfoResponse> successFiles = new ArrayList<>();
        List<MultiUploadResponse.DuplicateFileInfo> duplicateFiles = new ArrayList<>();
        List<MultiUploadResponse.ErrorFileInfo> errorFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                errorFiles.add(new MultiUploadResponse.ErrorFileInfo("알 수 없는 파일", "파일명이 없습니다."));
                continue;
            }
            
            try {
                // 중복 체크
                if (qaFileInfoRepository.findByFileName(fileName).isPresent()) {
                    duplicateFiles.add(new MultiUploadResponse.DuplicateFileInfo(fileName, "이미 업로드한 파일입니다"));
                    continue;
                }
                
                // 파일 타입에 따라 처리
                String lowerFileName = fileName.toLowerCase();
                if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".csv")) {
                    String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                    QaFileInfoResponse response = uploadFile(fileName, file.getSize(), fileType, file.getInputStream());
                    successFiles.add(response);
                } else if (lowerFileName.endsWith(".html") || lowerFileName.endsWith(".htm")) {
                    QaFileInfoResponse response = uploadHtmlFile(fileName, file.getSize(), file.getInputStream());
                    successFiles.add(response);
                } else {
                    errorFiles.add(new MultiUploadResponse.ErrorFileInfo(fileName, "지원하지 않는 파일 형식입니다."));
                }
            } catch (ResponseStatusException e) {
                if (e.getStatus() == HttpStatus.CONFLICT) {
                    duplicateFiles.add(new MultiUploadResponse.DuplicateFileInfo(fileName, e.getReason()));
                } else {
                    errorFiles.add(new MultiUploadResponse.ErrorFileInfo(fileName, e.getReason() != null ? e.getReason() : "업로드 실패"));
                }
            } catch (Exception e) {
                errorFiles.add(new MultiUploadResponse.ErrorFileInfo(fileName, e.getMessage() != null ? e.getMessage() : "업로드 중 오류가 발생했습니다."));
            }
        }
        
        MultiUploadResponse response = new MultiUploadResponse();
        response.setSuccessFiles(successFiles);
        response.setDuplicateFiles(duplicateFiles);
        response.setErrorFiles(errorFiles);
        return response;
    }

    public List<QaFileInfoResponse> getAllFiles() {
        List<QaFileInfo> files = qaFileInfoRepository.findAllByOrderByUploadedAtDesc();
        return files.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }
    
    public PageResponse<QaFileInfoResponse> getFilesWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QaFileInfo> filePage = qaFileInfoRepository.findAllByOrderByUploadedAtDesc(pageable);
        
        List<QaFileInfoResponse> content = filePage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(content, page, size, filePage.getTotalElements());
    }
    
    public PageResponse<QaFileInfoResponse> searchFiles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QaFileInfo> filePage = qaFileInfoRepository.findByFileNameContainingIgnoreCaseOrderByUploadedAtDesc(keyword, pageable);
        
        List<QaFileInfoResponse> content = filePage.getContent().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(content, page, size, filePage.getTotalElements());
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

