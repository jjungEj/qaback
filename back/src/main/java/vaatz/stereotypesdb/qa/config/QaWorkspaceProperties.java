package vaatz.stereotypesdb.qa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Workspace 경로를 외부 설정에서 주입받아 관리한다.
 */
@ConfigurationProperties(prefix = "qa.workspace")
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
}

