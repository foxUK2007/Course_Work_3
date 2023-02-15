package pro.sky.socksstorageapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionsApp extends Exception {

    public ExceptionsApp(String s) {
        super(s);
    }
}
