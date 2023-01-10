package ch.cern.todo.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Credit: Kalyan (SO)
// https://stackoverflow.com/questions/5757888/returning-an-error-and-message-from-a-spring-controller-or-service
@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {
    public static final long serialVersionUID = 20221003L;
    
    public NotFoundException(String s) {
        super(s);
    }
}
