package com.tenpo.percentageservice.service;

import com.tenpo.percentageservice.controller.dto.ApiCallDTO;
import com.tenpo.percentageservice.controller.dto.ApiCallResponse;
import com.tenpo.percentageservice.persistence.model.ApiCall;
import com.tenpo.percentageservice.persistence.repository.ApiCallRepository;
import com.tenpo.percentageservice.service.dto.ApiCallMessage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ApiCallService {

    @Value("${aws.sqs.api-call.sqs.name}")
    private String API_CALLS_SQS;

    private final EnqueueService enqueueService;
    private final ApiCallRepository apiCallRepository;

    public ApiCallService(EnqueueService enqueueService, ApiCallRepository apiCallRepository) {
        this.enqueueService = enqueueService;
        this.apiCallRepository = apiCallRepository;
    }

    public void processApiCall(
        String endpoint,
        String params,
        Integer statusCode,
        String responseMessage
    ) {
        ApiCallMessage message = new ApiCallMessage(endpoint, params, statusCode, responseMessage);
        enqueueService.sendMessage(API_CALLS_SQS, message);
    }

    public void saveApiCall(
        String endpoint,
        String params,
        Integer statusCode,
        String responseMessage
    ) {
        ApiCall apiCall = ApiCall.build(endpoint, params, statusCode, responseMessage);
        apiCallRepository.save(apiCall);
    }

    public List<ApiCallDTO> getLastApiCalls(String endpoint) {
        Instant aMinuteAgo = Instant.now().minus(1, ChronoUnit.MINUTES);
        Instant now = Instant.now();
        List<ApiCall> apiCalls = apiCallRepository.findRecentApiCalls(endpoint, aMinuteAgo, now);
        return apiCalls.stream().map(ApiCall::map).toList();
    }

    public ApiCallResponse getPaginatedApiCalls(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ApiCall> apiCallPage = apiCallRepository.findAll(pageable);

        return new ApiCallResponse(
            page,
            apiCallPage.getSize(),
            apiCallPage.getTotalPages(),
            apiCallPage.getContent().stream().map(ApiCall::map).toList());
    }

}
