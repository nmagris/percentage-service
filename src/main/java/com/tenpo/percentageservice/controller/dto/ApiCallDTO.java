package com.tenpo.percentageservice.controller.dto;

import java.time.Instant;

public record ApiCallDTO(
    String endpoint,
    String params,
    Integer statusCode,
    String responseMessage,
    Instant createdAt
) {

}
