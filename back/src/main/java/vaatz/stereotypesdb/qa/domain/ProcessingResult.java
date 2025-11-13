package vaatz.stereotypesdb.qa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "processing_results")
public class ProcessingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String documentName;

    @Column(nullable = false, length = 50)
    private String status;

    private Long actualFileSize;

    private Long transferredFileSize;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Column(length = 255)
    private String originalFileName;

    private Long originalFileSize;

    private Long convertedFileSize;

    @Column(length = 500)
    private String originalViewerUri;

    @Column(length = 500)
    private String htmlRenderUri;

    @Lob
    private String metadata;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_history_id")
    private PipelineHistory pipelineHistory;

    @JsonIgnore
    @OneToMany(mappedBy = "processingResult", fetch = FetchType.LAZY)
    private List<FeedbackEntry> feedbackEntries = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

