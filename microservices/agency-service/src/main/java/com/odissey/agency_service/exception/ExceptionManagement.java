package com.odissey.agency_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionManagement {

    // Per la gestione delle BAD REQUEST generate dalle annotazioni di validazione sollevate da @Valid
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<@NonNull CustomErrorResponse> argumentNotValidManagement(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setType("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400");
        customErrorResponse.setTitle(HttpStatus.BAD_REQUEST.name());
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error(">>> @Valid Error Details: "+ex.getMessage());
        customErrorResponse.setInstance(request.getRequestURI());
        customErrorResponse.setErrors(errors);

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }


    // Per la gestione delle BAD REQUEST generate dalle annotazioni di validazione sollevate da @Validated
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<@NonNull CustomErrorResponse> constraintViolationManagement(ConstraintViolationException ex, HttpServletRequest request){
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        ConstraintViolation::getMessage));

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setType("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400");
        customErrorResponse.setTitle(HttpStatus.BAD_REQUEST.name());
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error(">>> @Validated Error Details: "+ex.getMessage());
        customErrorResponse.setInstance(request.getRequestURI());
        customErrorResponse.setErrors(errors);

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({AgencyException.class})
    public ResponseEntity<@NonNull CustomErrorResponse> authExceptionManagement(AgencyException ex, HttpServletRequest request){
        Map<HttpStatus, String> status = new HashMap<>();
        status = switch(ex.message()){

            case ErrMsg.AGENCY_NOT_FOUND, ErrMsg.COUNTRY_NOT_FOUND -> Map.of(HttpStatus.NOT_FOUND, "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#404");
            case ErrMsg.VAT_ALREADY_PRESENT -> Map.of(HttpStatus.CONFLICT, "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#409");
            default -> Map.of(HttpStatus.INTERNAL_SERVER_ERROR, "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#500");
        };

        Map.Entry<HttpStatus, String> entry = status.entrySet().iterator().next();

        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                entry.getValue(),
                entry.getKey(),
                request.getRequestURI(),
                ex.getMessage()
        );

        return ResponseEntity.status(entry.getKey()).body(customErrorResponse);
    }



}
