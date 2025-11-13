package vaatz.stereotypesdb.qa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vaatz.stereotypesdb.qa.domain.SystemStatusSnapshot;
import vaatz.stereotypesdb.qa.dto.SystemStatusSnapshotRequest;
import vaatz.stereotypesdb.qa.dto.SystemStatusSummaryResponse;
import vaatz.stereotypesdb.qa.repository.ProcessingResultRepository;
import vaatz.stereotypesdb.qa.repository.SystemStatusSnapshotRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class SystemStatusService {

    private final SystemStatusSnapshotRepository snapshotRepository;
    private final ProcessingResultRepository processingResultRepository;

    public SystemStatusService(SystemStatusSnapshotRepository snapshotRepository,
                               ProcessingResultRepository processingResultRepository) {
        this.snapshotRepository = snapshotRepository;
        this.processingResultRepository = processingResultRepository;
    }

    public SystemStatusSummaryResponse getSummary() {
        SystemStatusSummaryResponse response = new SystemStatusSummaryResponse();
        SystemStatusSnapshot snapshot = snapshotRepository.findTopByOrderByRecordedAtDesc().orElse(null);

        long totalDocuments = processingResultRepository.count();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long todayCount = processingResultRepository.countByStartedAtBetween(startOfDay, endOfDay);

        response.setTotalDocuments(totalDocuments);
        response.setDocumentsProcessedToday(todayCount);

        if (snapshot != null) {
            response.setOverallStatus(snapshot.getOverallStatus());
            response.setAiDbStatus(snapshot.getAiDbStatus());
            response.setHelpyStatus(snapshot.getHelpyStatus());
            response.setLastCheckedAt(snapshot.getRecordedAt());

            if (snapshot.getUptimeSince() != null) {
                Duration uptime = Duration.between(snapshot.getUptimeSince(), LocalDateTime.now());
                response.setUptimeSeconds(uptime.getSeconds());
            } else {
                response.setUptimeSeconds(null);
            }
        } else {
            response.setOverallStatus("UNKNOWN");
            response.setAiDbStatus("UNKNOWN");
            response.setHelpyStatus("UNKNOWN");
            response.setLastCheckedAt(null);
            response.setUptimeSeconds(null);
        }

        return response;
    }

    public SystemStatusSnapshot createSnapshot(SystemStatusSnapshotRequest request) {
        SystemStatusSnapshot snapshot = new SystemStatusSnapshot();
        snapshot.setAiDbStatus(request.getAiDbStatus());
        snapshot.setHelpyStatus(request.getHelpyStatus());
        snapshot.setOverallStatus(request.getOverallStatus());
        snapshot.setUptimeSince(request.getUptimeSince());
        return snapshotRepository.save(snapshot);
    }
}

