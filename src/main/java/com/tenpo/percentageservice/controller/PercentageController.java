package com.tenpo.percentageservice.controller;

import com.tenpo.percentageservice.service.PercentageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/percentage")
public class PercentageController {

    private final PercentageService percentageService;

    public PercentageController(PercentageService percentageService) {
        this.percentageService = percentageService;
    }

    @GetMapping("/calculated-value")
    public ResponseEntity<Double> getCalculatedValue(
        @RequestParam(value = "first_number") int firstNumber,
        @RequestParam(value = "second_number") int secondNumber
    ) {
        Double response = percentageService.getCalculatedValue(firstNumber, secondNumber, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("test/calculated-value")
    public ResponseEntity<Double> getTestCalculatedValue(
        @RequestParam(value = "first_number") int firstNumber,
        @RequestParam(value = "second_number") int secondNumber,
        @RequestParam(value = "mocked_endpoint") String mockedEndpoint
    ) {
        Double response = percentageService.getCalculatedValue(firstNumber, secondNumber, mockedEndpoint);
        return ResponseEntity.ok(response);
    }

}
