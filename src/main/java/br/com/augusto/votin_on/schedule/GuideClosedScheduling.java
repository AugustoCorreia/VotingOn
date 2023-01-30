package br.com.augusto.votin_on.schedule;

import br.com.augusto.votin_on.config.KafkaConfig;
import br.com.augusto.votin_on.services.GuideService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuideClosedScheduling {

    private final GuideService guideService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Scheduled(fixedRate = 10000)
    public void sendGuideClosed() {
        guideService.getGuidesClosed().ifPresent(
                resultResponses -> resultResponses.forEach(resultResponse -> {
                    try {
                        kafkaTemplate.send(KafkaConfig.GUIDE_RESULT, objectMapper.writeValueAsString(resultResponse));
                        guideService.updateShare(resultResponse.getGuideId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                })
        );
    }
}
