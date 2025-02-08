package com.tenpo.percentageservice.controller.dto;

import java.util.List;

public record ApiCallResponse(
    Integer page,
    Integer size,
    Integer totalPages,
    List<ApiCallDTO> apiCalls) {

}
