package br.com.augusto.votin_on.services;

import br.com.augusto.votin_on.exception.GuideException;
import br.com.augusto.votin_on.dtos.GuideCreateRequest;
import br.com.augusto.votin_on.dtos.GuideCreateResponse;
import br.com.augusto.votin_on.dtos.GuideVoteRequest;
import br.com.augusto.votin_on.dtos.ResultResponse;
import br.com.augusto.votin_on.entity.Guide;
import br.com.augusto.votin_on.entity.Vote;
import br.com.augusto.votin_on.integration.CpfValidationIntegration;
import br.com.augusto.votin_on.repositories.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.augusto.votin_on.mappers.GuideMapper.toCreateResponse;
import static br.com.augusto.votin_on.mappers.GuideMapper.toEntity;

@Service
@RequiredArgsConstructor
public class GuideService {

    public static final String GUIDE_WITH_ID = "Guide with id ";
    private final GuideRepository guideRepository;

    private final VoteService voteService;

    private final CpfValidationIntegration cpfValidationIntegration;

    public ResponseEntity<GuideCreateResponse> createGuide(GuideCreateRequest guideCreateRequest) {
        return ResponseEntity.ok(toCreateResponse(guideRepository.save(toEntity(guideCreateRequest))));
    }

    public void toVoteOn(GuideVoteRequest guideVoteRequest) {
        guideRepository.findByIdAndClosedAtIsNotNullAndStillOpen(guideVoteRequest.getGuideId()).ifPresentOrElse(guide ->{
            cpfValidationIntegration.validate(guideVoteRequest.getCpf());
            voteService.doVote(guideVoteRequest,guide);
        },()-> {
            throw new GuideException(GUIDE_WITH_ID + guideVoteRequest.getGuideId() + " is closed");
        });
    }

    public void doOpen(Long id, Integer time) {
        guideRepository.findByIdAndClosedAtIsNull(id).ifPresentOrElse(guide -> {
            guide.setClosedAt(LocalDateTime.now().plusMinutes(time));
            guideRepository.save(guide);
        },()-> {
            throw new GuideException(GUIDE_WITH_ID + id + " was open before");
        });
    }

    public ResponseEntity<ResultResponse> getResult(Long id) {
        ResultResponse response = new ResultResponse();
        guideRepository.findByIdAndClosedAtIsNotNull(id).ifPresentOrElse(guide -> fillResponseData(response, guide),()-> {
            throw new GuideException(GUIDE_WITH_ID + id + " was not open yet");
        });

        return ResponseEntity.ok(response);
    }

    public Optional<List<ResultResponse>> getGuidesClosed() {
        List<ResultResponse> responseList = new ArrayList<>();

        guideRepository.findAllClosedGuideLastOneMinuteAndSharedIsFalse().ifPresent(guide ->
            guide.forEach(guide1 -> {
                ResultResponse response = new ResultResponse();
                fillResponseData(response, guide1);
                responseList.add(response);
            })
        );

        return Optional.of(responseList);
    }

    public void updateShare(Long guideId) {
        guideRepository.findById(guideId).ifPresent(guideData -> {
            guideData.setShared(true);
            guideRepository.save(guideData);
        });
    }

    private void fillResponseData(ResultResponse response, Guide guide) {
        response.setGuideTitle(guide.getTitle());
        response.setGuideId(guide.getId());
        List<Vote> votes = voteService.findByGuideId(guide.getId());
        response.setNoVotes(votes.stream().filter(vote-> !vote.getVoteIs()).count());
        response.setYesVotes(votes.stream().filter(Vote::getVoteIs).count());
    }



}
