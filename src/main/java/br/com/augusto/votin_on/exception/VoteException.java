package br.com.augusto.votin_on.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VoteException extends RuntimeException {
    public VoteException(String message) {
        super(message);
    }
}