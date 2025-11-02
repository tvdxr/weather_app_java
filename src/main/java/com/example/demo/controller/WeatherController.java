package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.demo.service.WeatherService;
import com.example.demo.model.WeatherModel;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.service.WeatherServiceException;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/weather")

public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{city}")
    public ResponseEntity<?> getWeatherByCity(@PathVariable String city) {
        try {
            WeatherModel weather = weatherService.getWeatherByCity(city);
            return ResponseEntity.ok(weather);
            
        } catch (WeatherServiceException e) {
            
            if (e.getMessage().contains("City not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found: " + city);
            } else if (e.getMessage().contains("Invalid or missing API key")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Weather service temporarily unavailable");
            } else if (e.getMessage().contains("Network error")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Network error - please try again later");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Weather service error: " + e.getMessage());
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @GetMapping("/coordinates")
    public ResponseEntity<?> getWeatherByCoordinates(
            @RequestParam double lat, 
            @RequestParam double lon) {
        try {
            WeatherModel weather = weatherService.getWeatherByCoordinates(lat, lon);
            return ResponseEntity.ok(weather);
            
        } catch (WeatherServiceException e) {
            
            if (e.getMessage().contains("Invalid coordinates")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid coordinates provided");
            } else if (e.getMessage().contains("Invalid or missing API key")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Weather service temporarily unavailable");
            } else if (e.getMessage().contains("Network error")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Network error - please try again later");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Weather service error: " + e.getMessage());
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }
    
}
