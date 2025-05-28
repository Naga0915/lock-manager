package jp.oecu.lockmng.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class CommonValidator {
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public boolean isEmailValid(String address){
        return emailValidator.isValid(address);
    }
}