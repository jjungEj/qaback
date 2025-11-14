package vaatz.stereotypesdb.qa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vaatz.stereotypesdb.qa.domain.SystemStatus;
import vaatz.stereotypesdb.qa.dto.SystemStatusRequest;
import vaatz.stereotypesdb.qa.dto.SystemStatusSummaryResponse;
import vaatz.stereotypesdb.qa.repository.ResultRepository;
import vaatz.stereotypesdb.qa.repository.SystemStatusRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class SystemStatusService {

    private final SystemStatusRepository systemStatusRepository;
    private final ResultRepository resultRepository;

    public SystemStatusService(SystemStatusRepository systemStatusRepository,
                               ResultRepository resultRepository) {
        this.systemStatusRepository = systemStatusRepository;
        this.resultRepository = resultRepository;
    }

    public SystemStatusSummaryResponse getSummary() {
        SystemStatusSummaryResponse response = new SystemStatusSummaryResponse();
        SystemStatus snapshot = systemStatusRepository.findTopByOrderByRecordedAtDesc().orElse(null);

        long totalDocuments = resultRepository.count();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long todayCount = resultRepository.countByStartedAtBetween(startOfDay, endOfDay);

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

    public SystemStatus create(SystemStatusRequest request) {
        SystemStatus snapshot = new SystemStatus();
        snapshot.setAiDbStatus(request.getAiDbStatus());
        snapshot.setHelpyStatus(request.getHelpyStatus());
        snapshot.setOverallStatus(request.getOverallStatus());
        snapshot.setUptimeSince(request.getUptimeSince());
        return systemStatusRepository.save(snapshot);
    }
}

