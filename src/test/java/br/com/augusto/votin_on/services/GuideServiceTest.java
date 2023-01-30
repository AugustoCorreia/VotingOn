package br.com.augusto.votin_on.services;

import br.com.augusto.votin_on.exception.GuideException;
import br.com.augusto.votin_on.dtos.GuideCreateRequest;
import br.com.augusto.votin_on.dtos.GuideCreateResponse;
import br.com.augusto.votin_on.dtos.GuideVoteRequest;
import br.com.augusto.votin_on.dtos.ResultResponse;
import br.com.augusto.votin_on.entity.Guide;
import br.com.augusto.votin_on.integration.CpfValidationIntegration;
import br.com.augusto.votin_on.repositories.GuideRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static br.com.augusto.votin_on.stubs.GeneralStubs.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideServiceTest {
    @Mock
    private GuideRepository guideRepository;

    @Mock
    private VoteService voteService;

    @Mock
    private CpfValidationIntegration cpfValidationIntegration;

    @InjectMocks
    private GuideService guideService;

    private GuideCreateRequest guideCreateRequest;
    private GuideCreateResponse guideCreateResponse;

    private GuideVoteRequest guideVoteRequest;
    private Guide guide;
    private ResultResponse resultResponse;

    @BeforeEach
    public void setUp() {
        guideCreateRequest = getGuideCreateRequest();
        guideCreateResponse = getGuideCreateResponse();
        guideVoteRequest = getGuideVoteRequest();
        guide = getGuide();
        resultResponse = getResultResponse();
    }

    @Test
     void createGuide_ReturnsOkResponse() {
        when(guideRepository.save(any(Guide.class))).thenReturn(guide);

        ResponseEntity<GuideCreateResponse> response = guideService.createGuide(guideCreateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(guideCreateResponse, response.getBody());
    }

    @Test
     void toVoteOn_GuideIsClosed_ThrowsGuideException() {
        assertThrows(GuideException.class, () -> guideService.toVoteOn(guideVoteRequest));
    }

    @Test
    void testToVoteOn() {
        when(guideRepository.findByIdAndClosedAtIsNotNullAndStillOpen(anyLong())).thenReturn(Optional.of(guide));

        guideService.toVoteOn(guideVoteRequest);

        verify(cpfValidationIntegration, times(1)).validate(anyString());
        verify(voteService, times(1)).doVote(guideVoteRequest, guide);
    }

    @Test
    void testToVoteOnGuideClosed() {
        assertThrows(GuideException.class, () -> guideService.toVoteOn(guideVoteRequest));
    }

    @Test
    void testDoOpen() {
        when(guideRepository.findByIdAndClosedAtIsNull(anyLong())).thenReturn(Optional.of(guide));

        guideService.doOpen(1L, 1);

        verify(guideRepository, times(1)).save(guide);
    }

    @Test
    void testDoOpenGuideClosed() {
        when(guideRepository.findByIdAndClosedAtIsNull(anyLong())).thenReturn(Optional.empty());

        assertThrows(GuideException.class, () -> guideService.doOpen(1L, 1));
    }

    @Test
    void testGetResult() {
        when(guideRepository.findByIdAndClosedAtIsNotNull(anyLong())).thenReturn(Optional.of(guide));
        when(voteService.findByGuideId(anyLong())).thenReturn(List.of(getVote()));
        ResponseEntity<ResultResponse> result = guideService.getResult(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(), resultResponse);

        verify(guideRepository, times(1)).findByIdAndClosedAtIsNotNull(anyLong());
    }

    @Test
    void testGetResultGuideNotFound() {
        when(guideRepository.findByIdAndClosedAtIsNotNull(anyLong())).thenReturn(Optional.empty());
        assertThrows(GuideException.class, () -> guideService.getResult(1L));

        verify(guideRepository, times(1)).findByIdAndClosedAtIsNotNull(anyLong());
    }

    @Test
    void testGetGuidesClosedSuccess() {
        when(guideRepository.findAllClosedGuideLastOneMinuteAndSharedIsFalse()).thenReturn(Optional.of(List.of(getGuide())));

        Optional<List<ResultResponse>> result = guideService.getGuidesClosed();

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());

        verify(guideRepository, times(1)).findAllClosedGuideLastOneMinuteAndSharedIsFalse();
    }

    @Test
    void testGetGuidesClosedEmpty() {
        when(guideRepository.findAllClosedGuideLastOneMinuteAndSharedIsFalse()).thenReturn(Optional.empty());

        Optional<List<ResultResponse>> result = guideService.getGuidesClosed();

        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());

        verify(guideRepository, times(1)).findAllClosedGuideLastOneMinuteAndSharedIsFalse();
    }
    @Test
    void testUpdateShare() {
        when(guideRepository.findById(anyLong())).thenReturn(Optional.of(guide));

        guideService.updateShare(1L);

        verify(guideRepository, times(1)).findById(anyLong());
        verify(guideRepository, times(1)).save(guide);
        assertTrue(guide.getShared());
    }

    @Test
    void testUpdateShareGuideNotFound() {
        when(guideRepository.findById(anyLong())).thenReturn(Optional.empty());

        guideService.updateShare(1L);

        verify(guideRepository, times(1)).findById(anyLong());
        verify(guideRepository, times(0)).save(any(Guide.class));
    }
}