CREATE DATABASE IF NOT EXISTS percentage_service;

CREATE TABLE IF NOT EXISTS api_calls (
    id SERIAL PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    params VARCHAR(255) NOT NULL,
    status_code INT NOT NULL,
    response_message VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_calls_endpoint_created_at ON api_calls (endpoint, created_at);
