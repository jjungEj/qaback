package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.LocalFile;
import vaatz.stereotypesdb.qa.dto.LocalFileRequest;
import vaatz.stereotypesdb.qa.dto.LocalFileResponse;
import vaatz.stereotypesdb.qa.dto.LocalFileSummaryResponse;
import vaatz.stereotypesdb.qa.repository.LocalFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocalFileService {

    private final LocalFileRepository localFileRepository;

    public LocalFileService(LocalFileRepository localFileRepository) {
        this.localFileRepository = localFileRepository;
    }

    public LocalFileSummaryResponse getSummary() {
        LocalFileSummaryResponse response = new LocalFileSummaryResponse();
        long total = localFileRepository.count();
        Long pending = localFileRepository.countByStatus("PENDING");
        Long completed = localFileRepository.countByStatus("COMPLETED");
        response.setTotalDocuments(total);
        response.setPendingDocuments(pending);
        response.setCompletedDocuments(completed);
        return response;
    }

    public List<LocalFileResponse> getAll() {
        return localFileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LocalFileResponse getById(Long id) {
        LocalFile document = localFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        return toResponse(document);
    }

    public LocalFileResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일을 선택해주세요.");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 이름을 확인할 수 없습니다.");
        }

        String cleanFilename = StringUtils.cleanPath(originalFilename);
        String extension = StringUtils.getFilenameExtension(cleanFilename);

        if (!isAllowedExtension(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "xlsx 또는 csv 형식의 파일만 업로드할 수 있습니다.");
        }

        Path storageDirectory = Paths.get("uploads", "local-files").toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "업로드 디렉터리를 생성할 수 없습니다.", e);
        }

        String storedFileName = UUID.randomUUID().toString() + "-" + cleanFilename;
        Path targetLocation = storageDirectory.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.", e);
        }

        LocalFile document = new LocalFile();
        document.setFileName(cleanFilename);
        document.setFileSize(file.getSize());
        document.setFileType(extension != null ? extension.toUpperCase(Locale.ROOT) : null);
        document.setStoragePath(targetLocation.toString());
        document.setStatus("PENDING");
        document.setQueuedAt(LocalDateTime.now());
        document.setDeletable(Boolean.TRUE);

        return toResponse(localFileRepository.save(document));
    }

    public LocalFileResponse create(LocalFileRequest request) {
        LocalFile document = new LocalFile();
        applyRequest(document, request);
        return toResponse(localFileRepository.save(document));
    }

    public LocalFileResponse update(Long id, LocalFileRequest request) {
        LocalFile document = localFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        applyRequest(document, request);
        return toResponse(localFileRepository.save(document));
    }

    public void delete(Long id) {
        LocalFile document = localFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        localFileRepository.delete(document);
    }

    private void applyRequest(LocalFile document, LocalFileRequest request) {
        document.setFileName(request.getFileName());
        document.setFileSize(request.getFileSize());
        document.setFileType(request.getFileType());
        document.setStatus(request.getStatus());
        document.setQueuedAt(request.getQueuedAt());
        document.setCompletedAt(request.getCompletedAt());
        document.setDeletable(request.getDeletable());
    }

    private boolean isAllowedExtension(String extension) {
        return extension != null && ("xlsx".equalsIgnoreCase(extension) || "csv".equalsIgnoreCase(extension));
    }

    private LocalFileResponse toResponse(LocalFile document) {
        LocalFileResponse response = new LocalFileResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setFileSize(document.getFileSize());
        response.setFileType(document.getFileType());
        response.setStoragePath(document.getStoragePath());
        response.setStatus(document.getStatus());
        response.setQueuedAt(document.getQueuedAt());
        response.setCompletedAt(document.getCompletedAt());
        response.setDeletable(document.getDeletable());
        return response;
    }
}

