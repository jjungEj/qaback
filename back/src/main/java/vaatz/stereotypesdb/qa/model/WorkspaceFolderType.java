package vaatz.stereotypesdb.qa.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/**
* @ClassName	: WorkspaceFolderType.java
* @Description	: 워크스페이스 내 3개의 물리 폴더를 표현한다.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- after, before, dev 폴더 타입 enum
* 								- 폴더 타입별 키값 및 표시명 관리
*/
public enum WorkspaceFolderType {
    AFTER("after", "수정 후 대기 중"),
    BEFORE("before", "수정 전"),
    DEV("dev", "Dev");

    private final String key;
    private final String displayName;

    WorkspaceFolderType(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static WorkspaceFolderType from(String value) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "폴더 타입이 필요합니다.");
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        for (WorkspaceFolderType type : values()) {
            if (type.key.equals(normalized)) {
                return type;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 폴더 타입: " + value);
    }
}

