package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.LocalFile;
import vaatz.stereotypesdb.qa.dto.LocalFileRequest;
import vaatz.stereotypesdb.qa.dto.LocalFileResponse;
import vaatz.stereotypesdb.qa.dto.LocalFileSummaryResponse;
import vaatz.stereotypesdb.qa.repository.LocalFileRepository;

import java.util.List;
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

    private LocalFileResponse toResponse(LocalFile document) {
        LocalFileResponse response = new LocalFileResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setFileSize(document.getFileSize());
        response.setFileType(document.getFileType());
        response.setStatus(document.getStatus());
        response.setQueuedAt(document.getQueuedAt());
        response.setCompletedAt(document.getCompletedAt());
        response.setDeletable(document.getDeletable());
        return response;
    }
}

