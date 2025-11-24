package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.config.QaWorkspaceProperties;
import vaatz.stereotypesdb.qa.dto.HtmlFileSaveRequest;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFolderResponse;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * before / after / dev 폴더 기반 파일 관리를 담당한다.
 */
@Service
public class FileWorkspaceService {

    private final Map<WorkspaceFolderType, Path> folderPaths = new EnumMap<>(WorkspaceFolderType.class);

    public FileWorkspaceService(QaWorkspaceProperties properties) {
        folderPaths.put(WorkspaceFolderType.BEFORE, normalize(properties.getBeforePath()));
        folderPaths.put(WorkspaceFolderType.AFTER, normalize(properties.getAfterPath()));
        folderPaths.put(WorkspaceFolderType.DEV, normalize(properties.getDevPath()));
    }

    @PostConstruct
    void prepareDirectories() {
        folderPaths.values().forEach(path -> {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "워크스페이스 폴더를 생성할 수 없습니다: " + path, e);
            }
        });
    }

    public WorkspaceFileResponse uploadHtml(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 HTML 파일이 필요합니다.");
        }
        String fileName = sanitizeFileName(file.getOriginalFilename());
        validateHtmlExtension(fileName);

        Path target = folderPaths.get(WorkspaceFolderType.BEFORE).resolve(fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            return toFileResponse(WorkspaceFolderType.BEFORE, target);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "HTML 파일 저장에 실패했습니다.", e);
        }
    }

    public WorkspaceFileResponse saveHtmlContent(String fileName, HtmlFileSaveRequest request) {
        String sanitized = sanitizeFileName(fileName);
        validateHtmlExtension(sanitized);
        if (request == null || request.getHtmlContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "저장할 HTML 내용이 필요합니다.");
        }
        Path target = folderPaths.get(WorkspaceFolderType.BEFORE).resolve(sanitized);
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(target, request.getHtmlContent(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return toFileResponse(WorkspaceFolderType.BEFORE, target);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "HTML 저장에 실패했습니다.", e);
        }
    }

    public List<WorkspaceFolderResponse> getWorkspaceOverview(int afterPage, int beforePage, int devPage, int size) {
        int validatedSize = normalizeSize(size);
        int afterPageSafe = normalizePage(afterPage);
        int beforePageSafe = normalizePage(beforePage);
        int devPageSafe = normalizePage(devPage);

        List<WorkspaceFolderResponse> result = new ArrayList<>();
        result.add(buildFolderResponse(WorkspaceFolderType.AFTER, afterPageSafe, validatedSize));
        result.add(buildFolderResponse(WorkspaceFolderType.BEFORE, beforePageSafe, validatedSize));
        result.add(buildFolderResponse(WorkspaceFolderType.DEV, devPageSafe, validatedSize));
        return result;
    }

    public WorkspaceFileContentResponse readFile(WorkspaceFolderType folderType, String fileName) {
        Path target = resolve(folderType, fileName);
        if (!Files.exists(target)) {
            Path folderPath = folderPaths.get(folderType);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                String.format("파일을 찾을 수 없습니다. 파일명: %s, 폴더: %s, 전체 경로: %s", 
                    fileName, folderPath, target));
        }
        try {
            String content = Files.readString(target, StandardCharsets.UTF_8);
            WorkspaceFileContentResponse response = new WorkspaceFileContentResponse();
            response.setFolder(folderType.getKey());
            response.setFileName(target.getFileName().toString());
            response.setFileSize(Files.size(target));
            response.setLastModifiedAt(toLocalDateTime(Files.getLastModifiedTime(target)));
            response.setContent(content);
            return response;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "파일을 읽을 수 없습니다: " + target, e);
        }
    }

    public void saveJsonlToAfter(String fileName, byte[] payload) {
        String sanitized = sanitizeFileName(fileName);
        validateJsonlExtension(sanitized);
        Path target = folderPaths.get(WorkspaceFolderType.AFTER).resolve(sanitized);
        try {
            Files.write(target, payload, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSONL 저장에 실패했습니다.", e);
        }
    }

    public WorkspaceFileResponse moveAfterToDev(String fileName) {
        Path source = resolve(WorkspaceFolderType.AFTER, fileName);
        if (!Files.exists(source)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "after 폴더에서 파일을 찾을 수 없습니다.");
        }
        Path target = folderPaths.get(WorkspaceFolderType.DEV).resolve(source.getFileName());
        try {
            Files.createDirectories(target.getParent());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return toFileResponse(WorkspaceFolderType.DEV, target);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dev 폴더 이동에 실패했습니다.", e);
        }
    }

    private WorkspaceFolderResponse buildFolderResponse(WorkspaceFolderType type, int requestedPage, int size) {
        Path folderPath = folderPaths.get(type);
        List<WorkspaceFileResponse> allFiles = loadFiles(type, folderPath);
        int totalElements = allFiles.size();

        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int safePage = totalPages == 0 ? 0 : Math.min(requestedPage, totalPages - 1);
        int fromIndex = totalElements == 0 ? 0 : safePage * size;
        int toIndex = totalElements == 0 ? 0 : Math.min(fromIndex + size, totalElements);
        List<WorkspaceFileResponse> pageContent = totalElements == 0
                ? Collections.emptyList()
                : new ArrayList<>(allFiles.subList(fromIndex, toIndex));

        WorkspaceFolderResponse response = new WorkspaceFolderResponse();
        response.setFolder(type.getKey());
        response.setDisplayName(type.getDisplayName());
        response.setAbsolutePath(folderPath.toString());
        response.setFiles(pageContent);
        response.setPage(totalElements == 0 ? 0 : safePage);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        return response;
    }

    private List<WorkspaceFileResponse> loadFiles(WorkspaceFolderType type, Path folderPath) {
        try (Stream<Path> stream = Files.list(folderPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> toFileResponse(type, path))
                    .sorted((a, b) -> b.getLastModifiedAt().compareTo(a.getLastModifiedAt()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "폴더 내용을 불러올 수 없습니다: " + folderPath, e);
        }
    }

    private int normalizePage(int page) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다.");
        }
        return page;
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size 값은 1 이상이어야 합니다.");
        }
        if (size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size 값은 100 이하로 제한됩니다.");
        }
        return size;
    }

    private WorkspaceFileResponse toFileResponse(WorkspaceFolderType folderType, Path path) {
        try {
            WorkspaceFileResponse response = new WorkspaceFileResponse();
            response.setFolder(folderType.getKey());
            response.setFileName(path.getFileName().toString());
            response.setFileSize(Files.size(path));
            response.setLastModifiedAt(toLocalDateTime(Files.getLastModifiedTime(path)));
            response.setExtension(extractExtension(path.getFileName().toString()));
            response.setAbsolutePath(path.toString());
            return response;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 정보를 읽을 수 없습니다.", e);
        }
    }

    private Path resolve(WorkspaceFolderType folderType, String fileName) {
        String sanitized = sanitizeFileName(fileName);
        Path folder = folderPaths.get(folderType);
        if (folder == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 폴더입니다: " + folderType);
        }
        return folder.resolve(sanitized);
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일명이 필요합니다.");
        }
        // URL 디코딩 처리 (공백이 + 또는 %20으로 인코딩된 경우)
        String decoded;
        try {
            decoded = URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            decoded = fileName; // 디코딩 실패 시 원본 사용
        }
        String trimmed = decoded.trim();
        if (trimmed.contains("..") || trimmed.contains("/") || trimmed.contains("\\") || trimmed.startsWith(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않는 파일명입니다: " + trimmed);
        }
        return trimmed;
    }

    private void validateHtmlExtension(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".html") || lower.endsWith(".htm"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HTML 파일만 업로드할 수 있습니다.");
        }
    }

    private void validateJsonlExtension(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".jsonl")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSONL 파일명만 허용됩니다.");
        }
    }

    private String extractExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private LocalDateTime toLocalDateTime(FileTime fileTime) {
        return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
    }

    private Path normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Paths.get("./workspace").toAbsolutePath().normalize();
        }
        return Paths.get(input).toAbsolutePath().normalize();
    }
}

