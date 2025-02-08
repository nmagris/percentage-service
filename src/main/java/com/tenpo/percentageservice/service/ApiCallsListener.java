package com.tenpo.percentageservice.service;

import com.tenpo.percentageservice.service.dto.ApiCallMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ApiCallsListener {

    private static final Logger log = LoggerFactory.getLogger(ApiCallsListener.class);
    private final ApiCallService apiCallService;

    public ApiCallsListener(ApiCallService apiCallService) {
        this.apiCallService = apiCallService;
    }

    @SqsListener("${aws.sqs.api-call.sqs.name}")
    public void processMessage(@Payload ApiCallMessage message) {
        log.info("Message received from queue {}", message);
        apiCallService.saveApiCall(
            message.endpoint(),
            message.params(),
            message.statusCode(),
            message.responseMessage());
    }

}
