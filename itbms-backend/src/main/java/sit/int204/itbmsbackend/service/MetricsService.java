package sit.int204.itbmsbackend.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsService {

    private final Counter userLoginCounter;
    private final Counter documentUploadCounter;
    private final Timer requestTimer;

    @Autowired
    public MetricsService(MeterRegistry meterRegistry) {
        // Custom counters
        this.userLoginCounter = Counter.builder("itbms_user_logins_total")
                .description("Total number of user logins")
                .register(meterRegistry);

        this.documentUploadCounter = Counter.builder("itbms_document_uploads_total")
                .description("Total number of document uploads")
                .register(meterRegistry);

        // Custom timer
        this.requestTimer = Timer.builder("itbms_request_duration")
                .description("Request processing time")
                .register(meterRegistry);
    }

    public void incrementUserLogin() {
        userLoginCounter.increment();
    }

    public void incrementDocumentUpload() {
        documentUploadCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(requestTimer);
    }
}