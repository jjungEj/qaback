package vaatz.stereotypesdb.qa.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "system_status_snapshots")
public class SystemStatusSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String aiDbStatus;

    @Column(nullable = false, length = 50)
    private String helpyStatus;

    @Column(nullable = false, length = 50)
    private String overallStatus;

    private LocalDateTime uptimeSince;

    private Long totalDocuments;

    private Long documentsProcessedToday;

    @CreationTimestamp
    private LocalDateTime recordedAt;
}

