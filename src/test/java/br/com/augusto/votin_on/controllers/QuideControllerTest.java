package br.com.augusto.votin_on.controllers;

import br.com.augusto.votin_on.dtos.ResultResponse;
import br.com.augusto.votin_on.services.GuideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static br.com.augusto.votin_on.stubs.GeneralStubs.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuideController.class)
class QuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuideService service;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QuideController(service))
                .build();
    }

    @Test
     void createGuideTest() throws Exception {

        when(service.createGuide(getGuideCreateRequest()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(getGuideCreateResponse()));

        mockMvc.perform(post("/v1/guide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getGuideCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(getGuideCreateResponse().getTitle())));
    }


    @Test
     void toVoteOnTest() throws Exception {

        mockMvc.perform(post("/v1/guide/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getGuideVoteRequest())))
                .andExpect(status().isNoContent());

        verify(service, times(1)).toVoteOn(getGuideVoteRequest());
    }


    @Test
     void doOpenTest() throws Exception {
        Long id = 1L;
        Integer time = 1;

        mockMvc.perform(patch("/v1/guide/{id}/open", id)
                        .param("time", String.valueOf(time)))
                .andExpect(status().isAccepted());

        verify(service, times(1)).doOpen(id, time);
    }

    @Test
     void getResultTest() throws Exception {
        Long id = 1L;

        ResultResponse guideResult = getResultResponse();

        when(service.getResult(id)).thenReturn(ResponseEntity.ok(guideResult));

        mockMvc.perform(get("/v1/guide/{id}/result", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guideTitle", is(guideResult.getGuideTitle())));
    }



    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}