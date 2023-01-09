package ch.cern.todo.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {
    public BadRequestException(String s) {
        super(s);
    }
}