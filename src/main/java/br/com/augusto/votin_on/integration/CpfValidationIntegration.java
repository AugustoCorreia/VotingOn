package br.com.augusto.votin_on.integration;

import br.com.augusto.votin_on.dtos.CpfValidationResponse;
import br.com.augusto.votin_on.enuns.CpfStatusEnum;
import br.com.augusto.votin_on.exception.VoteException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

@Log4j2
@Component
@AllArgsConstructor
public class CpfValidationIntegration {


    private static final String BASE_URL = "https://user-info.herokuapp.com/users/%s";
    private static final Random RAND = new SecureRandom();

    private final RestTemplate restTemplate;

    public void validate(String cpf){
        try {
            ResponseEntity<CpfValidationResponse> response = restTemplate.getForEntity(String.format(BASE_URL, cpf), CpfValidationResponse.class);

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                log.error("Cpf Validation not working");
                throw new VoteException("CPF Unable to vote");
            }

            if (CpfStatusEnum.UNABLE_TO_VOTE.toString().equals(Objects.requireNonNull(response.getBody()).getStatus())){
                throw new VoteException("CPF Unable to vote");
            }
        } catch (HttpClientErrorException e){
            log.error(e.getMessage());

            if (CpfStatusEnum.UNABLE_TO_VOTE.equals(generateRandomValueForValidateCpf())){
                throw new VoteException("CPF Unable to vote");
            }
        }
    }

    private CpfStatusEnum generateRandomValueForValidateCpf(){
        return RAND.nextInt(10) > 2 ? CpfStatusEnum.ABLE_TO_VOTE : CpfStatusEnum.UNABLE_TO_VOTE;
    }
}
