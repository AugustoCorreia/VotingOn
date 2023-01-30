package br.com.augusto.votin_on.stubs;

import br.com.augusto.votin_on.dtos.*;
import br.com.augusto.votin_on.entity.Guide;
import br.com.augusto.votin_on.entity.Vote;
import br.com.augusto.votin_on.enuns.CpfStatusEnum;
import br.com.augusto.votin_on.enuns.VoteEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralStubs {
    public static ResultResponse getResultResponse() {
        return ResultResponse.builder()
                .guideId(1L)
                .guideTitle("title")
                .noVotes(1L)
                .yesVotes(0L)
                .build();
    }

    public static GuideCreateResponse getGuideCreateResponse() {
        return GuideCreateResponse.builder().id(1L).title("title").build();
    }

    public static GuideCreateRequest getGuideCreateRequest() {
        return GuideCreateRequest.builder()
                .title("title").build();
    }

    public static GuideVoteRequest getGuideVoteRequest() {
        return GuideVoteRequest.builder().vote(VoteEnum.SIM).guideId(1L).cpf("77637796086").build();
    }

    public static Guide getGuide(){
        return Guide.builder()
                .id(1L)
                .shared(false)
                .title("title")
                .closedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Vote getVote(){
        return Vote.builder()
                .userCpf("77637796086")
                .voteIs(false)
                .id(1L)
                .guide(getGuide())
                .build();
    }
    public static CpfValidationResponse getCpfValidationResponse(){
        return  CpfValidationResponse.builder()
                .status(CpfStatusEnum.ABLE_TO_VOTE.toString())
                .build();
    }

}
