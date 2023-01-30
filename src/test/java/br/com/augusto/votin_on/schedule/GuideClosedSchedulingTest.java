package br.com.augusto.votin_on.schedule;

import br.com.augusto.votin_on.config.KafkaConfig;
import br.com.augusto.votin_on.dtos.ResultResponse;
import br.com.augusto.votin_on.services.GuideService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static br.com.augusto.votin_on.stubs.GeneralStubs.getResultResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideClosedSchedulingTest {

    @Mock
    private GuideService guideService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GuideClosedScheduling guideClosedScheduling;

    List<ResultResponse> closedGuides = List.of(getResultResponse());

    @Test
    @SneakyThrows
    void sendGuideClosed_withClosedGuides_shouldCallUpdateShareAndSendResult() {

        when(guideService.getGuidesClosed()).thenReturn(Optional.of(closedGuides));
        when(objectMapper.writeValueAsString(any(ResultResponse.class))).thenReturn("mocked json");

        guideClosedScheduling.sendGuideClosed();

        verify(guideService, times(1)).updateShare(anyLong());
        verify(kafkaTemplate, times(1)).send(KafkaConfig.GUIDE_RESULT, "mocked json");
    }

    @Test
    void sendGuideClosed_withoutClosedGuides_shouldNotCallUpdateShareAndSendResult() {
        when(guideService.getGuidesClosed()).thenReturn(Optional.empty());

        guideClosedScheduling.sendGuideClosed();

        verify(guideService, never()).updateShare(anyLong());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
     void sendGuideClosed_withClosedGuidesAndJsonProcessingException_shouldNotCallUpdateShareAndPrintStackTrace() throws JsonProcessingException {

        when(guideService.getGuidesClosed()).thenReturn(Optional.of(closedGuides));

        JsonProcessingException exception = mock(JsonProcessingException.class);
        doThrow(exception).when(objectMapper).writeValueAsString(any(ResultResponse.class));

        guideClosedScheduling.sendGuideClosed();

        verify(guideService, never()).updateShare(1L);
        verify(exception).printStackTrace();
    }

}