package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.WeatherModel;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Arrays;

import com.example.demo.model.WeatherApiResponse;

@Service
public class WeatherService {

    private static final List<String> EUROPEAN_CITIES = Arrays.asList(
        "London", "Paris", "Berlin", "Rome", "Madrid", "Amsterdam", "Vienna", 
        "Prague", "Warsaw", "Budapest", "Stockholm", "Oslo", "Copenhagen", 
        "Helsinki", "Dublin", "Lisbon", "Athens", "Brussels", "Bern", 
        "Ljubljana", "Bratislava", "Tallinn", "Riga", "Vilnius", "Bucharest",
        "Sofia", "Zagreb", "Sarajevo", "Podgorica", "Skopje", "Tirana",
        "Kiev", "Minsk", "Chisinau", "Moscow", "Reykjavik"
    );

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiURL;

    private RestTemplate restTemplate = new RestTemplate();

    public WeatherModel getWeatherByCoordinates(double lat, double lon) {
        // Validate coordinates
        if (!isValidCoordinates(lat, lon)) {
            throw new IllegalArgumentException("Invalid coordinates: lat=" + lat + ", lon=" + lon);
        }

        String completeUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric", apiURL, lat, lon, apiKey);
        
        try {
            WeatherApiResponse response = restTemplate.getForObject(completeUrl, WeatherApiResponse.class);
            
            if (response == null) {
                throw WeatherServiceException.invalidResponse("API returned null response");
            }
            
            validateResponse(response, "coordinates (" + lat + ", " + lon + ")");
            
            return new WeatherModel(
                response.getName(),                                           
                String.format("%.1f°C", response.getMain().getTemp()),        
                response.getWeather()[0].getDescription(),                   
                String.format("%d%%", response.getMain().getHumidity()),     
                String.format("%.1f m/s", response.getWind().getSpeed()),   
                String.format("%.1f°C", response.getMain().getFeels_like())   
            );
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw WeatherServiceException.cityNotFound("Location at coordinates (" + lat + ", " + lon + ")");
            } else if (e.getStatusCode().value() == 401) {
                System.out.println("API key not active yet, returning mock data for coordinates: " + lat + ", " + lon);
                return createMockWeatherDataForLocation(lat, lon);
            } else {
                throw WeatherServiceException.apiError("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            throw WeatherServiceException.networkError(e);
        } catch (Exception e) {
            throw WeatherServiceException.apiError("Unexpected error occurred", e);
        }
    }

    public WeatherModel getWeatherByCity(String city) {
        if (!isValidCity(city)) {
            throw new IllegalArgumentException("Invalid city name " + city);
        }

        if (!hasOneDigit(city)) {
            throw WeatherServiceException.cityNotFound(city);
        }

        String correctedCity = correctCityName(city);
        String completeUrl = String.format("%s?q=%s&appid=%s&units=metric", apiURL, correctedCity, apiKey);
        
        try {
            WeatherApiResponse response = restTemplate.getForObject(completeUrl, WeatherApiResponse.class);
            
            if (response == null) {
                throw WeatherServiceException.invalidResponse("API returned null response");
            }
            
            validateResponse(response, city);
            
            return new WeatherModel(
                response.getName(),                                           
                String.format("%.1fC", response.getMain().getTemp()),        
                response.getWeather()[0].getDescription(),                   
                String.format("%d%%", response.getMain().getHumidity()),     
                String.format("%.1f km/h", response.getWind().getSpeed()),   
                String.format("%.1fC", response.getMain().getFeels_like())   
            );
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw WeatherServiceException.cityNotFound(city);
            } else if (e.getStatusCode().value() == 401) {
                System.out.println("API key not active yet, returning mock data for: " + city);
                return createMockWeatherData(city);
            } else {
                throw WeatherServiceException.apiError("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            throw WeatherServiceException.networkError(e);
        } catch (Exception e) {
            throw WeatherServiceException.apiError("Unexpected error occurred", e);
        }
    }

    public boolean isValidCity(String city) {
        return city != null &&
            !city.trim().isEmpty() &&
            city.matches("^[a-zA-Z0-9\\s-']+$");
    }

    private void validateResponse(WeatherApiResponse response, String city) {
        if (response.getMain() == null) {
            throw WeatherServiceException.invalidResponse("Missing temperature data for " + city);
        }
        
        if (response.getWeather() == null || response.getWeather().length == 0) {
            throw WeatherServiceException.invalidResponse("Missing weather description for " + city);
        }
        
        if (response.getWind() == null) {
            throw WeatherServiceException.invalidResponse("Missing wind data for " + city);
        }
        
        if (response.getName() == null || response.getName().isEmpty()) {
            throw WeatherServiceException.invalidResponse("Missing city name in response");
        }
    }

    private WeatherModel createMockWeatherData(String city) {
        if (city.toLowerCase().contains("london")) {
            return new WeatherModel(city, "15.2C", "Rainy", "85%", "8.5 km/h", "13.1C");
        } else if (city.toLowerCase().contains("tokyo")) {
            return new WeatherModel(city, "28.5C", "Sunny", "45%", "12.3 km/h", "31.2C");
        } else if (city.toLowerCase().contains("paris")) {
            return new WeatherModel(city, "18.7C", "Cloudy", "72%", "6.8 km/h", "17.9C");
        } else {
            return new WeatherModel(city, "22.0C", "Partly cloudy", "60%", "10.0 km/h", "24.5C");
        }
    }

    private int calculateLevenshteinDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int [][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {               
                if (str1.charAt(i-1) == str2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1]; // no op
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(
                        dp[i-1][j],    // del
                        dp[i][j-1]),   // ins
                        dp[i-1][j-1]   // subst
                    );
                }
            }
        }
        return dp[len1][len2];
    }

    private String correctCityName(String inputCity) {
        System.out.println("DEBUG: correctCityName called with: " + inputCity);
        for (String city : EUROPEAN_CITIES) {
            if (city.equalsIgnoreCase(inputCity)) {
                return city;
            }
        }

        String bestMatch = inputCity;
        int minDistance = Integer.MAX_VALUE;

        for (String city : EUROPEAN_CITIES) {
        int distance = calculateLevenshteinDistance(inputCity.toLowerCase(), city.toLowerCase());
        
        // dist max = 2
        if (distance <= 2 && distance < minDistance) {
            minDistance = distance;
            bestMatch = city;
        }
    }

        if (!bestMatch.equals(bestMatch)) {
            System.out.println("City name corrected: '" + inputCity + "' -> '" + bestMatch + "'");
        }

        System.out.println("DEBUG: correctCityName returning: " + bestMatch);
        return bestMatch;
    }

    private boolean hasOneDigit(String city) {
        
        int digitCount = 0;
        
        for (int i = 0; i < city.length(); i++) {
            char c = city.charAt(i);
            
            if (Character.isDigit(c)) {
                digitCount++;
            }
        }
        
        boolean result = digitCount <= 1;
        return result;
    }

    private boolean isValidCoordinates(double lat, double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    private WeatherModel createMockWeatherDataForLocation(double lat, double lon) {
        // mock data
        String locationName = "Your Location";
        
        if (lat > 50) {
            // cold (northen hemisphere)
            return new WeatherModel(locationName, "8.5°C", "Cold and cloudy", "75%", "7.2 m/s", "6.1°C");
        } else if (lat < 0) {
            // southern hemisphere
            return new WeatherModel(locationName, "25.3°C", "Warm and sunny", "55%", "4.8 m/s", "27.1°C");
        } else if (lat > 30) {
            // temperate regions
            return new WeatherModel(locationName, "18.7°C", "Partly cloudy", "65%", "6.3 m/s", "19.2°C");
        } else {
            // tropical/equatorial regions
            return new WeatherModel(locationName, "29.8°C", "Hot and humid", "85%", "3.5 m/s", "34.2°C");
        }
    }
}

