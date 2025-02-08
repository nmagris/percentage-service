package com.tenpo.percentageservice.controller;

import com.tenpo.percentageservice.controller.dto.ErrorDTO;
import com.tenpo.percentageservice.exception.PercentageServiceException;
import com.tenpo.percentageservice.service.ApiCallService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({PercentageServiceException.class})
    protected ResponseEntity<ErrorDTO> handlePercentageServiceException(PercentageServiceException ex) {
        ErrorDTO error = new ErrorDTO(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(error, new HttpHeaders(), ex.getStatus());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        ErrorDTO error = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
