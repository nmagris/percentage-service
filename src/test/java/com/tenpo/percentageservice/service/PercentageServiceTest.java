package com.tenpo.percentageservice.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenpo.percentageservice.controller.dto.ApiCallDTO;
import com.tenpo.percentageservice.exception.PercentageServiceException;
import com.tenpo.percentageservice.integration.externalservice.ExternalServiceClient;
import com.tenpo.percentageservice.integration.externalservice.response.ExternalServiceResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class PercentageServiceTest {

    @InjectMocks
    private PercentageService percentageService;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @Mock
    private ApiCallService apiCallService;

    @Test
    void whenGraterThanZeroResultFromSum_thenReturnPositiveValue() {
        when(externalServiceClient.getPercentage(null))
            .thenReturn(new ExternalServiceResponse(10));

        Double result = percentageService.getCalculatedValue(5, 5, null);

        assertEquals(11.00, result);
        verify(apiCallService, times(1)).processApiCall(anyString(), anyString(), any(), any());
    }

    @Test
    void whenResultFromSumZero_thenReturnZero() {
        when(externalServiceClient.getPercentage(null))
            .thenReturn((new ExternalServiceResponse(10)));

        Double result = percentageService.getCalculatedValue(-5, 5, null);

        assertEquals(0, result);
        verify(apiCallService, times(1)).processApiCall(anyString(), anyString(), any(), any());
    }

    @Test
    void whenPercentageIsZero_thenReturnSum() {
        when(externalServiceClient.getPercentage(null))
            .thenReturn(new ExternalServiceResponse(0));

        Double result = percentageService.getCalculatedValue(5, 5, null);

        assertEquals(10, result);
        verify(apiCallService, times(1)).processApiCall(anyString(), anyString(), any(), any());
    }

    @Test
    void whenPercentageIsGraterThan100_thenReturnExpectedIncrement() {
        when(externalServiceClient.getPercentage(null))
            .thenReturn(new ExternalServiceResponse(110));

        Double result = percentageService.getCalculatedValue(5, 5, null);

        assertEquals(21, result);
        verify(apiCallService, times(1)).processApiCall(anyString(), anyString(), any(), any());
    }

    @Test
    void whenRateLimitExceeded_thenThrowTooManyRequests() {
        ApiCallDTO apiCall = new ApiCallDTO("test", "test", HttpStatus.ACCEPTED.value(), "test", Instant.now());
        when(apiCallService.getLastApiCalls(anyString())).thenReturn(List.of(apiCall, apiCall, apiCall));

        assertThatExceptionOfType(PercentageServiceException.class).isThrownBy(() -> {
                percentageService.getCalculatedValue(5, 5, null);
            })
            .extracting("message")
            .isEqualTo("Rate limit exceeded");
        verify(apiCallService, times(1)).processApiCall(anyString(), anyString(), any(), any());
    }

}
