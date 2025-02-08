package com.tenpo.percentageservice.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String REGION;

    @Value("${aws.endpoint}")
    private String ENDPOINT;

    @Value("${aws.access-key}")
    private String ACCESS_KEY;

    @Value("${aws.secret-key}")
    private String SECRET_KEY;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
            .region(Region.of(REGION))
            .endpointOverride(URI.create(ENDPOINT))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
            ))
            .build();
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.newTemplate(sqsAsyncClient);
    }

}
