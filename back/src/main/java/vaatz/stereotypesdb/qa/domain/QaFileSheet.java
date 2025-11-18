package vaatz.stereotypesdb.qa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

