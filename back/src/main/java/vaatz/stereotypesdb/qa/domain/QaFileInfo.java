package vaatz.stereotypesdb.qa.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "qa_file_info")
public class QaFileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 10)
    private String fileType; // xlsx, xls, csv

    @Column(length = 1000)
    private String feedback; // 확인 사항/피드백

    @OneToMany(mappedBy = "qaFileInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sheetOrder ASC")
    private List<QaFileSheet> sheets = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

