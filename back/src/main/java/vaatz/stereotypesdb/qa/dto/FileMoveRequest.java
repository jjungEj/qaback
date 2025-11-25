package vaatz.stereotypesdb.qa.dto;

import java.util.List;

/**
* @ClassName	: FileMoveRequest.java
* @Description	: 파일 이동 요청 DTO (after -> 배치 작업). 여러 파일을 한 번에 처리할 수 있습니다.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- after 폴더에서 dev 폴더로 파일 이동 요청 DTO
* 								- 단일 파일 및 다중 파일 이동 지원
*/
public class FileMoveRequest {

    /**
     * 단일 파일명 (하위 호환성 유지)
     */
    private String fileName;
    
    /**
     * 여러 파일명 목록 (여러 파일 처리 시 사용)
     */
    private List<String> fileNames;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public List<String> getFileNames() {
        return fileNames;
    }
    
    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
    
    /**
     * 처리할 파일명 목록을 반환한다.
     * fileNames가 있으면 fileNames를, 없으면 fileName을 List로 반환.
     */
    public List<String> getFileList() {
        if (fileNames != null && !fileNames.isEmpty()) {
            return fileNames;
        }
        if (fileName != null && !fileName.trim().isEmpty()) {
            return List.of(fileName);
        }
        throw new IllegalArgumentException("fileName 또는 fileNames 중 하나는 필수입니다.");
    }
}

