package com.example.demo.service;

public class WeatherServiceException extends RuntimeException{
    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static WeatherServiceException cityNotFound(String cityName) {
        return new WeatherServiceException("City not found: " + cityName);
    }
    
    public static WeatherServiceException invalidApiKey() {
        return new WeatherServiceException("Invalid or missing API key");
    }
    
    public static WeatherServiceException apiError(String message) {
        return new WeatherServiceException("Weather API error: " + message);
    }
    
    public static WeatherServiceException apiError(String message, Throwable cause) {
        return new WeatherServiceException("Weather API error: " + message, cause);
    }
    
    public static WeatherServiceException networkError(Throwable cause) {
        return new WeatherServiceException("Network error while fetching weather data", cause);
    }
    
    public static WeatherServiceException invalidResponse(String reason) {
        return new WeatherServiceException("Invalid API response: " + reason);
    }
}
