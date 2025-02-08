package com.tenpo.percentageservice.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenpo.percentageservice.exception.PercentageServiceException;
import com.tenpo.percentageservice.integration.externalservice.ExternalServiceClient;
import com.tenpo.percentageservice.integration.externalservice.response.ExternalServiceResponse;
import java.util.Optional;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @InjectMocks
    private ExternalServiceClient externalServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheService cacheService;

    private static final String CACHE_PERCENTAGE_KEY = "PERCENTAGE";
    private static final String FALLBACK_KEY = "_FALLBACK";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(externalServiceClient, "MOCK_ENDPOINT", "TEST_ENDPOINT");
    }

    @Test
    void whenValuePresentInCache_thenUseValue() {
        when(cacheService.getValue(CACHE_PERCENTAGE_KEY)).thenReturn(Optional.of(10));

        ExternalServiceResponse response = externalServiceClient.getPercentage(null);

        verify(restTemplate, never()).getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT",
            ExternalServiceResponse.class);
        assertEquals(10, response.percentage());
    }

    @Test
    void whenValueNotPresentInCache_thenTryGettingNewValue() {
        when(cacheService.getValue(CACHE_PERCENTAGE_KEY)).thenReturn(Optional.empty());
        when(restTemplate.getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT", ExternalServiceResponse.class))
            .thenReturn(new ResponseEntity<>(new ExternalServiceResponse(10), HttpStatus.OK));

        ExternalServiceResponse response = externalServiceClient.getPercentage(null);

        verify(restTemplate, times(1)).getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT",
            ExternalServiceResponse.class);
        assertEquals(10, response.percentage());
    }

    @Test
    void whenNoCachedValueAndServiceFailed_returnOldValue() {
        when(cacheService.getValue(CACHE_PERCENTAGE_KEY)).thenReturn(Optional.empty());
        when(restTemplate.getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT", ExternalServiceResponse.class))
            .thenThrow(new RestClientException("message"));
        when(cacheService.getValue(Strings.concat(CACHE_PERCENTAGE_KEY, FALLBACK_KEY))).thenReturn(Optional.of(10));

        ExternalServiceResponse response = externalServiceClient.getPercentage(null);

        verify(restTemplate, times(3)).getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT",
            ExternalServiceResponse.class);
        assertEquals(10, response.percentage());
    }

    @Test
    void whenNoCachedValueAndServiceFailedAndNoOldValue_returnBadGateway() {
        when(cacheService.getValue(CACHE_PERCENTAGE_KEY)).thenReturn(Optional.empty());
        when(restTemplate.getForEntity("https://run.mocky.io/v3/TEST_ENDPOINT", ExternalServiceResponse.class))
            .thenThrow(new RestClientException("message"));
        when(cacheService.getValue(Strings.concat(CACHE_PERCENTAGE_KEY, FALLBACK_KEY))).thenReturn(Optional.empty());

        assertThatExceptionOfType(PercentageServiceException.class).isThrownBy(() -> {
                externalServiceClient.getPercentage(null);
            })
            .extracting("message")
            .isEqualTo("No value returned for percentage");
    }

}
