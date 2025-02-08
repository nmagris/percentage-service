package com.tenpo.percentageservice.persistence.model;

import com.tenpo.percentageservice.controller.dto.ApiCallDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "api_calls")
public class ApiCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "params")
    private String params;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_message")
    private String responseMessage;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static ApiCall build(
        String endpoint,
        String params,
        Integer statusCode,
        String responseMessage
    ) {
        ApiCall entity = new ApiCall();
        entity.setEndpoint(endpoint);
        entity.setParams(params);
        entity.setStatusCode(statusCode);
        entity.setResponseMessage(responseMessage.length() > 250 ? responseMessage.substring(0, 250) : responseMessage);
        return entity;
    }

    public static ApiCallDTO map(ApiCall apiCall) {
        return new ApiCallDTO(
            apiCall.getEndpoint(),
            apiCall.getParams(),
            apiCall.getStatusCode(),
            apiCall.getResponseMessage(),
            apiCall.getCreatedAt()
        );
    }

}
