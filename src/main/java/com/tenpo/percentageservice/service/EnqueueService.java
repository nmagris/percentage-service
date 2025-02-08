package com.tenpo.percentageservice.service;

import com.tenpo.percentageservice.service.dto.ApiCallMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnqueueService {

    private static final Logger log = LoggerFactory.getLogger(EnqueueService.class);
    private final SqsTemplate sqsTemplate;

    public EnqueueService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void sendMessage(String queueUrl, ApiCallMessage message) {
        sqsTemplate.send(queueUrl, message);
        log.info("Message successfully sent to SQS {}", queueUrl);
    }

}
