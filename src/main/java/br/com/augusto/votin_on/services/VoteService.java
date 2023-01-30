package br.com.augusto.votin_on.services;

import br.com.augusto.votin_on.exception.VoteException;
import br.com.augusto.votin_on.dtos.GuideVoteRequest;
import br.com.augusto.votin_on.entity.Guide;
import br.com.augusto.votin_on.entity.Vote;
import br.com.augusto.votin_on.enuns.VoteEnum;
import br.com.augusto.votin_on.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    public List<Vote> findByGuideId(Long guideId) {
        return voteRepository.findByGuideId(guideId)
                .orElseThrow(()-> new VoteException("No votes found with guide id:"+guideId));
    }


    public void doVote(GuideVoteRequest guideVoteRequest, Guide guide) {
        voteRepository.findByGuideIdAndUserCpf(guideVoteRequest.getGuideId(), guideVoteRequest.getCpf())
                .ifPresentOrElse(vote -> { throw new VoteException("This CPF has voted on this guide");},()->
                    voteRepository.save(Vote.builder()
                            .voteIs(VoteEnum.SIM.equals(guideVoteRequest.getVote()))
                            .guide(guide)
                            .userCpf(guideVoteRequest.getCpf())
                            .build())
                );


    }
}
