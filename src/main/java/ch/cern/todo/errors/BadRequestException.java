package ch.cern.todo.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {
    public static final long serialVersionUID = 19650316L;
    
    public BadRequestException(String s) {
        super(s);
    }
}
