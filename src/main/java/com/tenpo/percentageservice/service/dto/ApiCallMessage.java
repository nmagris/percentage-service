package com.tenpo.percentageservice.service.dto;

public record ApiCallMessage(String endpoint, String params, Integer statusCode, String responseMessage) {

}
