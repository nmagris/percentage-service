package com.tenpo.percentageservice.service;

import com.tenpo.percentageservice.controller.dto.ApiCallDTO;
import com.tenpo.percentageservice.exception.PercentageServiceException;
import com.tenpo.percentageservice.integration.externalservice.ExternalServiceClient;
import com.tenpo.percentageservice.integration.externalservice.response.ExternalServiceResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PercentageService {

    private final ExternalServiceClient externalServiceClient;
    private final ApiCallService apiCallService;

    private static final String PERCENTAGE_ENDPOINT = "/percentage/calculated-value";
    private static final String PERCENTAGE_ENDPOINT_PARAMS = "first_number: %d, second_number: %d";
    private static final int RATE_LIMIT = 3;

    public PercentageService(ExternalServiceClient externalServiceClient, ApiCallService apiCallService) {
        this.externalServiceClient = externalServiceClient;
        this.apiCallService = apiCallService;
    }

    public Double getCalculatedValue(int firstNumber, int secondNumber, String mockedEndpoint) {
        try {
            checkRateLimit();
            double percentage = ((double) getPercentage(mockedEndpoint) / 100) + 1.00;
            Double response = (firstNumber + secondNumber) * percentage;
            apiCallService.processApiCall(
                PERCENTAGE_ENDPOINT,
                String.format(PERCENTAGE_ENDPOINT_PARAMS, firstNumber, secondNumber),
                HttpStatus.ACCEPTED.value(),
                response.toString()
            );
            return response;
        } catch (PercentageServiceException e) {
            apiCallService.processApiCall(
                PERCENTAGE_ENDPOINT,
                String.format(PERCENTAGE_ENDPOINT_PARAMS, firstNumber, secondNumber),
                e.getStatus().value(),
                e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            apiCallService.processApiCall(
                PERCENTAGE_ENDPOINT,
                String.format(PERCENTAGE_ENDPOINT_PARAMS, firstNumber, secondNumber),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
            );
            throw e;
        }
    }

    public Integer getPercentage(String mockedEndpoint) {
        ExternalServiceResponse response = externalServiceClient.getPercentage(mockedEndpoint);
        return response.percentage();
    }

    private void checkRateLimit() {
        List<ApiCallDTO> apiCalls = apiCallService.getLastApiCalls(PERCENTAGE_ENDPOINT);
        if (apiCalls.size() < RATE_LIMIT) {
            return;
        }
        throw new PercentageServiceException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }

}
