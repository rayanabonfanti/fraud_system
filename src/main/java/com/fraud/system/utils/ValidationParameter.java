package com.fraud.system.utils;

import com.fraud.system.exceptions.CustomException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

//@Component
public class ValidationParameter {

    public void validationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> validationErros = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            String validationErrorMessage = String.join("; ", validationErros);

            throw new CustomException(HttpStatus.BAD_REQUEST.value(), validationErrorMessage);
        }
    }
}
