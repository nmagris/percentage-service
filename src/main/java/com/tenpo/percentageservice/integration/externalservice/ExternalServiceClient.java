package com.tenpo.percentageservice.integration.externalservice;

import com.tenpo.percentageservice.exception.PercentageServiceException;
import com.tenpo.percentageservice.integration.externalservice.response.ExternalServiceResponse;
import com.tenpo.percentageservice.service.CacheService;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@CacheConfig(cacheNames = "percentageCache")
public class ExternalServiceClient {

    @Value("${mock-server.endpoint}")
    private String MOCK_ENDPOINT;

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClient.class);

    private static final String CACHE_PERCENTAGE_KEY = "PERCENTAGE";
    private static final int MAX_RETRIES = 3;
    private static final String MOCK_HOST = "https://run.mocky.io/v3/";

    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    public ExternalServiceClient(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    public ExternalServiceResponse getPercentage(String mockedEndpoint) {
        Optional<Integer> cachedValueOpt = cacheService.getValue(CACHE_PERCENTAGE_KEY);
        return cachedValueOpt.map(ExternalServiceResponse::new).orElseGet(() -> getNewValue(mockedEndpoint));
    }

    private ExternalServiceResponse getNewValue(String mockedEndpoint) {
        log.info("Getting new value from external service");
        Optional<ExternalServiceResponse> valueOpt = getPercentageWithRetries(mockedEndpoint);
        if (valueOpt.isEmpty()) {
            log.info("Using fallback value");
            Optional<ExternalServiceResponse> fallbackValueOpt = getFallbackValue();
            if (fallbackValueOpt.isEmpty()) {
                throw new PercentageServiceException(HttpStatus.BAD_GATEWAY, "No value returned for percentage");
            }
            return fallbackValueOpt.get();
        }
        cacheService.saveValue(CACHE_PERCENTAGE_KEY, valueOpt.get().percentage(), 30);
        return valueOpt.get();
    }

    private Optional<ExternalServiceResponse> getPercentageWithRetries(String mockedEndpoint) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            Optional<ExternalServiceResponse> response = tryGetPercentage(mockedEndpoint);
            if (response.isPresent()) {
                return response;
            }
            retries++;
        }
        log.info("Max retries reached for external service");
        return Optional.empty();
    }

    private Optional<ExternalServiceResponse> getFallbackValue() {
        Optional<Integer> value = cacheService.getValue(Strings.concat(CACHE_PERCENTAGE_KEY, "_FALLBACK"));
        return value.map(ExternalServiceResponse::new);
    }

    private Optional<ExternalServiceResponse> tryGetPercentage(String mockedEndpoint) {
        try {
            log.info("Calling external service for percentage");
            if (Objects.isNull(mockedEndpoint)) {
                ResponseEntity<ExternalServiceResponse> response = restTemplate.getForEntity(
                    Strings.concat(MOCK_HOST, MOCK_ENDPOINT),
                    ExternalServiceResponse.class);
                ExternalServiceResponse responseBody = response.getBody();
                return Optional.of(responseBody);
            }
            log.info("Using dynamic endpoint for mock {}", mockedEndpoint);
            ResponseEntity<ExternalServiceResponse> response = restTemplate.getForEntity(
                Strings.concat(MOCK_HOST, mockedEndpoint),
                ExternalServiceResponse.class);
            ExternalServiceResponse responseBody = response.getBody();
            return Optional.of(responseBody);

        } catch (RestClientException e) {
            log.info("Error while getting percentage value from external service");
            return Optional.empty();
        }
    }

}
