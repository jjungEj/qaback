package vaatz.stereotypesdb.qa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
* @ClassName	: QaWorkspaceProperties.java
* @Description	: Workspace 경로를 외부 설정에서 주입받아 관리한다.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- application.yml에서 워크스페이스 경로 설정 관리
* 								- before, after, dev 폴더 경로 설정
*/

// 각 폴더의 기본 경로 저장, yml 값으로 덮어짐
@ConfigurationProperties(prefix = "qa.workspace")
@Component
public class QaWorkspaceProperties {

    /**
     * 최초 업로드(수정 전) HTML 파일이 적재되는 경로.
     */
    private String beforePath = "./workspace/before";

    /**
     * JSONL 변환을 마친 파일을 임시로 보관하는 경로.
     */
    private String afterPath = "./workspace/after";

    /**
     * 외부 DB(Dev)로 전달될 최종 경로.
     */
    private String devPath = "./workspace/dev";
    
    /**
     * maked 파일경로
     */
    private String maskedpath = "./workspace/masked";
    
    //getter, setter 만 존재 service 쪽에서 경로를 읽어 directory 준비
    public String getBeforePath() {
        return beforePath;
    }

    public void setBeforePath(String beforePath) {
        this.beforePath = beforePath;
    }

    public String getAfterPath() {
        return afterPath;
    }

    public void setAfterPath(String afterPath) {
        this.afterPath = afterPath;
    }

    public String getDevPath() {
        return devPath;
    }

    public void setDevPath(String devPath) {
        this.devPath = devPath;
    }
    
    public String getMaskedpath() {
        return maskedpath;
    }

    public void setMaskedpath(String maskedpath) {
        this.maskedpath = maskedpath;
    }
}
