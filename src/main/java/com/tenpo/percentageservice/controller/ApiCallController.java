package com.tenpo.percentageservice.controller;

import com.tenpo.percentageservice.controller.dto.ApiCallResponse;
import com.tenpo.percentageservice.service.ApiCallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-call")
public class ApiCallController {

    private final ApiCallService apiCallService;

    public ApiCallController(ApiCallService apiCallService) {
        this.apiCallService = apiCallService;
    }

    @GetMapping
    public ResponseEntity<ApiCallResponse> getPaginatedApiCalls(
        @RequestParam Integer page,
        @RequestParam Integer size
    ) {
        ApiCallResponse response = apiCallService.getPaginatedApiCalls(page, size);
        return ResponseEntity.ok(response);
    }

}
