package br.com.augusto.votin_on.controllers;

import br.com.augusto.votin_on.dtos.GuideCreateRequest;
import br.com.augusto.votin_on.dtos.GuideCreateResponse;
import br.com.augusto.votin_on.dtos.GuideVoteRequest;
import br.com.augusto.votin_on.dtos.ResultResponse;
import br.com.augusto.votin_on.services.GuideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/guide")
@RequiredArgsConstructor
public class QuideController {

    private final GuideService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GuideCreateResponse> createGuide(@RequestBody @Valid GuideCreateRequest guideCreateRequest){
        return service.createGuide(guideCreateRequest);
    }

    @PostMapping("/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toVoteOn(@RequestBody @Valid GuideVoteRequest guideVoteRequest){
        service.toVoteOn(guideVoteRequest);
    }

    @PatchMapping("/{id}/open")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getOpen(@PathVariable("id") Long id, @RequestParam(value = "time", required = false, defaultValue = "1") Integer time){
        service.doOpen(id,time);
    }

    @GetMapping("/{id}/result")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResultResponse> getResult(@PathVariable("id") Long id){
        return service.getResult(id);
    }


}
