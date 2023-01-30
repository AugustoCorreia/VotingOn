package br.com.augusto.votin_on.services;

import br.com.augusto.votin_on.exception.VoteException;
import br.com.augusto.votin_on.entity.Guide;
import br.com.augusto.votin_on.entity.Vote;
import br.com.augusto.votin_on.repositories.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static br.com.augusto.votin_on.stubs.GeneralStubs.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private VoteService voteService;

    @Test
     void findByGuideId_withVotes_shouldReturnVotes() {
        Long guideId = 1L;
        List<Vote> expectedVotes = Arrays.asList(
                Vote.builder().id(1L).build(),
                Vote.builder().id(2L).build()
        );
        when(voteRepository.findByGuideId(guideId)).thenReturn(Optional.of(expectedVotes));

        List<Vote> votes = voteService.findByGuideId(guideId);

        assertThat(votes).isEqualTo(expectedVotes);
    }

    @Test
     void findByGuideId_withoutVotes_shouldThrowException() {
        Long guideId = 1L;
        when(voteRepository.findByGuideId(guideId)).thenReturn(Optional.empty());

        assertThrows(VoteException.class, () -> voteService.findByGuideId(guideId));
    }

    @Test
     void doVote_newVote_shouldSaveVote() {
        Guide guide = getGuide();
        when(voteRepository.findByGuideIdAndUserCpf(anyLong(), anyString())).thenReturn(Optional.empty());

        voteService.doVote(getGuideVoteRequest(), guide);

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(voteCaptor.capture());
        Vote vote = voteCaptor.getValue();
        assertThat(vote.getVoteIs()).isTrue();
        assertThat(vote.getGuide()).isEqualTo(guide);
        assertThat(vote.getUserCpf()).isEqualTo(getVote().getUserCpf());
    }

    @Test
     void testDoVote_AlreadyVoted_ShouldThrowVoteException() {
        when(voteRepository.findByGuideIdAndUserCpf(anyLong(), anyString())).thenReturn(Optional.of(getVote()));

            VoteException response = assertThrows(VoteException.class,()->voteService.doVote(getGuideVoteRequest(), getGuide()));

            assertEquals("This CPF has voted on this guide", response.getMessage());
            verify(voteRepository, never()).save(any());
    }
}