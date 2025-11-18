package vaatz.stereotypesdb.qa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
* @ClassName	: QaFileSheet.java
* @Description	: QA 파일의 시트 정보를 저장하는 엔티티
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 엑셀 파일의 각 시트를 HTML로 변환하여 저장
* 								- QaFileInfo와 N:1 관계로 파일에 종속
*/
@Getter
@Setter
@Entity
@Table(name = "qa_file_sheet")
public class QaFileSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String sheetName;

    @Column(nullable = false)
    private Integer sheetOrder;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String htmlContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_file_info_id", nullable = false)
    private QaFileInfo qaFileInfo;
}

