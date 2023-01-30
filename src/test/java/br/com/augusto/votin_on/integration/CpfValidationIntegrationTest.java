package br.com.augusto.votin_on.integration;

import br.com.augusto.votin_on.dtos.CpfValidationResponse;
import br.com.augusto.votin_on.enuns.CpfStatusEnum;
import br.com.augusto.votin_on.exception.VoteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static br.com.augusto.votin_on.stubs.GeneralStubs.getCpfValidationResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.rnorth.ducttape.unreliables.Unreliables.retryUntilTrue;

@ExtendWith(MockitoExtension.class)
class CpfValidationIntegrationTest {

    public static final String CPF_UNABLE_TO_VOTE = "CPF Unable to vote";
    @InjectMocks
    private CpfValidationIntegration cpfValidationIntegration;

    @Mock
    private RestTemplate restTemplate;

    private static final String CPF = "12345678910";
    private static final String URL = String.format("https://user-info.herokuapp.com/users/%s", CPF);
    private static final CpfValidationResponse CPF_VALIDATION_RESPONSE = getCpfValidationResponse();


    @Test
     void validate_whenCpfIsValid_shouldReturnValid() {
        when(restTemplate.getForEntity(anyString(), eq(CpfValidationResponse.class)))
                .thenReturn(new ResponseEntity<>(CPF_VALIDATION_RESPONSE, HttpStatus.OK));

        cpfValidationIntegration.validate(CPF);

        verify(restTemplate).getForEntity(URL, CpfValidationResponse.class);
    }

    @Test
    void validate_whenCpfIsValid_shouldReturnBadRequest() {
        when(restTemplate.getForEntity(anyString(), eq(CpfValidationResponse.class)))
                .thenReturn(new ResponseEntity<>(CPF_VALIDATION_RESPONSE, HttpStatus.BAD_REQUEST));

        VoteException voteException = assertThrows(VoteException.class, () -> cpfValidationIntegration.validate(CPF));
        assertEquals(CPF_UNABLE_TO_VOTE, voteException.getMessage());

        verify(restTemplate).getForEntity(URL, CpfValidationResponse.class);
    }

    @Test
     void validate_whenCpfIsNotValid_shouldThrowException() {
        when(restTemplate.getForEntity(anyString(), eq(CpfValidationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        retryUntilTrue(
                1, TimeUnit.MINUTES,()->{
                    try {
                        VoteException voteException = assertThrows(VoteException.class, () -> cpfValidationIntegration.validate(CPF));
                        assertEquals(CPF_UNABLE_TO_VOTE, voteException.getMessage());
                        return true;
                    }catch (Throwable e){
                        return false;
                    }

                }
        );
    }

    @Test
     void validate_whenCpfIsNotAbleToVote_shouldThrowException() {
        when(restTemplate.getForEntity(URL, CpfValidationResponse.class))
                .thenReturn(new ResponseEntity<>(CpfValidationResponse.builder()
                        .status(CpfStatusEnum.UNABLE_TO_VOTE.toString())
                        .build(), HttpStatus.OK));

        assertThrows(VoteException.class, () -> cpfValidationIntegration.validate(CPF));

        verify(restTemplate).getForEntity(URL, CpfValidationResponse.class);
    }
}