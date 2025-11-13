package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.LocalFileDocument;
import vaatz.stereotypesdb.qa.dto.LocalFileDocumentRequest;
import vaatz.stereotypesdb.qa.dto.LocalFileDocumentResponse;
import vaatz.stereotypesdb.qa.dto.LocalFileQueueSummaryResponse;
import vaatz.stereotypesdb.qa.repository.LocalFileDocumentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocalFileDocumentService {

    private final LocalFileDocumentRepository localFileDocumentRepository;

    public LocalFileDocumentService(LocalFileDocumentRepository localFileDocumentRepository) {
        this.localFileDocumentRepository = localFileDocumentRepository;
    }

    public LocalFileQueueSummaryResponse getSummary() {
        LocalFileQueueSummaryResponse response = new LocalFileQueueSummaryResponse();
        long total = localFileDocumentRepository.count();
        Long pending = localFileDocumentRepository.countByStatus("PENDING");
        Long completed = localFileDocumentRepository.countByStatus("COMPLETED");
        response.setTotalDocuments(total);
        response.setPendingDocuments(pending);
        response.setCompletedDocuments(completed);
        return response;
    }

    public List<LocalFileDocumentResponse> getAll() {
        return localFileDocumentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LocalFileDocumentResponse getById(Long id) {
        LocalFileDocument document = localFileDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        return toResponse(document);
    }

    public LocalFileDocumentResponse create(LocalFileDocumentRequest request) {
        LocalFileDocument document = new LocalFileDocument();
        applyRequest(document, request);
        return toResponse(localFileDocumentRepository.save(document));
    }

    public LocalFileDocumentResponse update(Long id, LocalFileDocumentRequest request) {
        LocalFileDocument document = localFileDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        applyRequest(document, request);
        return toResponse(localFileDocumentRepository.save(document));
    }

    public void delete(Long id) {
        LocalFileDocument document = localFileDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 로컬 파일을 찾을 수 없습니다."));
        localFileDocumentRepository.delete(document);
    }

    private void applyRequest(LocalFileDocument document, LocalFileDocumentRequest request) {
        document.setFileName(request.getFileName());
        document.setFileSize(request.getFileSize());
        document.setFileType(request.getFileType());
        document.setStatus(request.getStatus());
        document.setQueuedAt(request.getQueuedAt());
        document.setCompletedAt(request.getCompletedAt());
        document.setDeletable(request.getDeletable());
    }

    private LocalFileDocumentResponse toResponse(LocalFileDocument document) {
        LocalFileDocumentResponse response = new LocalFileDocumentResponse();
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

